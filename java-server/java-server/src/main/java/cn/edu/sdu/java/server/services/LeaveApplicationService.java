package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;


@Service
public class LeaveApplicationService {
    @Autowired
    LeaveApplicationRepository leaveApplicationRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    PersonRepository personRepository;

    /*请假审批功能

    status:
    0 待处理
    1 审批已通过
    2 审批未通过
    3 已申请销假
    4 销假已成功

1. 需要添加一个返回老师list,用于前端的下拉框,就是学生申请的时候会弹出来窗口,然后选择老师
2. 申请的时候需要添加一个限定条件,就是请假表一个学生只能同时存在一条请假数据
3. 只有在申请的时候后端才能拿到老师id,其他接口拿到的都是请假id,拿不到老师id,
只需把拿到老师id的那部分改成从请假类拿到老师即可

    基础功能：
    【完成】 getApplyList 获取列表（如果有限制条件就自动加）
    【完成】 apply 请假，添加新的假条
    【完成】 approve/disapprove 审批，获取假条id，改status和reply（从0变成1或2）

    拓展功能：
    【完成】getTeacherOptionList 获取教师下拉框
    【完成】updateStudent 学生修改请假理由/审核老师等
    【完成】updateTeacher 老师修改审批理由（准备做）
    【完成】deleteApplication 学生放弃请假，或老师、教务删除请假信息（准备做）
    【完成】reportCancel 请求销假，学生提交类似“已返回，请求销假（status=3）”信息
    【完成】finishLeave 办结事项，老师将状态改为类似”事项已完成“(status=4)

     */

    public DataResponse getApplyList(@Valid DataRequest dataRequest){
        //根据用户信息与权限选择合适的列表
        Integer personId = dataRequest.getInteger("personId");
        Optional<Person> personOption = personRepository.findByPersonId(personId);
        List<LeaveApplication> applyList = new ArrayList<>();
        if (personOption.isPresent()){
            Person person = personOption.get();
            if (person.getType().equals(EUserType.ROLE_STUDENT.toString())){
                Optional<Student> sO = studentRepository.findByPersonPersonId(personId);
                if (sO.isPresent()){
                    Student s = sO.get();
                    Integer studentId = s.getPersonId();
                    applyList = leaveApplicationRepository.findByStudentPersonId(studentId);
                }
            }else if (person.getType().equals(EUserType.ROLE_TEACHER.toString())){
                Optional<Teacher> tO = teacherRepository.findByPersonPersonId(personId);
                if (tO.isPresent()){
                    Teacher t = tO.get();
                    Integer teacherId = t.getPersonId();
                    applyList = leaveApplicationRepository.findByTeacherPersonId(teacherId);
                }
            }
        }
        if (applyList.isEmpty()){
            applyList = leaveApplicationRepository.findAll();
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (LeaveApplication leaveApplication :applyList){
            Map<String, Object> m = new HashMap<>();
            m.put("leaveId",leaveApplication.getLeaveId());
            m.put("student",leaveApplication.getStudent().getPerson().getName());
            m.put("num",leaveApplication.getStudent().getPerson().getNum());
            m.put("teacher",leaveApplication.getTeacher().getPerson().getName());
            m.put("reason",leaveApplication.getReason());
            m.put("startDate",leaveApplication.getStartDate());
            m.put("endDate",leaveApplication.getEndDate());
            m.put("applyTime",leaveApplication.getApplyTime());
            m.put("status",leaveApplication.getStatus());
            m.put("reply",leaveApplication.getReply());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public OptionItemList getTeacherItemOptionList(@Valid DataRequest dataRequest) {
        // 查询所有
        List<Teacher> tList = teacherRepository.findTeacherListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Teacher t : tList) {
            // 返回格式
            itemList.add(new OptionItem( t.getPerson().getPersonId(),t.getPerson().getPersonId()+"",t.getPerson().getNum()+"-"+t.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public DataResponse apply(@Valid DataRequest dataRequest) {
        //提取有用信息
        Integer studentId = dataRequest.getInteger("studentId");
        Integer teacherId = dataRequest.getInteger("teacherId");
        String reason = dataRequest.getString("reason");
        Date startDate = dataRequest.getDate("startDate");
        Date endDate = dataRequest.getDate("endDate");
        Date applyTime = new Date();

        //检查数据有效性
        if (startDate == null || endDate == null)
            return CommonMethod.getReturnMessageError("开始时间或结束时间不能为空");
        if (!endDate.after(startDate))
            return CommonMethod.getReturnMessageError("结束时间必须在开始时间之后");
        if (teacherId == null)
            return CommonMethod.getReturnMessageError("请选择审批老师");
        if (reason == null)
            return CommonMethod.getReturnMessageError("请假理由不能为空");
        //确保学生不能同时存在两个假条
        List<LeaveApplication> applyList;
        if (!leaveApplicationRepository.findByStudentPersonId(studentId).isEmpty()){
            applyList = leaveApplicationRepository.findByStudentPersonId(studentId);
            for (LeaveApplication l:applyList){
                if (l.getStatus()!=4){
                    return CommonMethod.getReturnMessageError("您有未撤销的假条，请先销假");
                }
            }
        }
        //赋值
        LeaveApplication leaveApplication = new LeaveApplication();
        if (studentRepository.findByPersonPersonId(studentId).isPresent())
            leaveApplication.setStudent(studentRepository.findByPersonPersonId(studentId).get());
        if (teacherRepository.findByPersonPersonId(teacherId).isPresent())
            leaveApplication.setTeacher(teacherRepository.findByPersonPersonId(teacherId).get());
        leaveApplication.setReason(reason);
        leaveApplication.setStartDate(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        leaveApplication.setEndDate(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        leaveApplication.setApplyTime(applyTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        leaveApplication.setStatus(0);
        leaveApplication.setReply(null);
        try {
            leaveApplicationRepository.save(leaveApplication);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonMethod.getReturnMessageOK("申请成功！");
    }

    public DataResponse approve(@Valid DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        String reply = dataRequest.getString("reply");
        LeaveApplication leaveApplication;
        if (leaveApplicationRepository.findById(leaveId).isEmpty())
            return CommonMethod.getReturnMessageError("申请不存在");
        else
            leaveApplication = leaveApplicationRepository.findById(leaveId).get();
        if (reply == null)  reply = "";
        leaveApplication.setReply(reply);
        leaveApplication.setStatus(1);
        leaveApplicationRepository.save(leaveApplication);
        return CommonMethod.getReturnMessageOK("审核成功！");
    }

    public DataResponse disApprove(@Valid DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        String reply = dataRequest.getString("reply");
        LeaveApplication leaveApplication;
        if (leaveApplicationRepository.findById(leaveId).isEmpty())
            return CommonMethod.getReturnMessageError("申请不存在");
        else
            leaveApplication = leaveApplicationRepository.findById(leaveId).get();
        if (reply == null)  reply = "";
        leaveApplication.setReply(reply);
        leaveApplication.setStatus(2);
        leaveApplicationRepository.save(leaveApplication);
        return CommonMethod.getReturnMessageOK("审核成功！");
    }

    public DataResponse updateStudent(@Valid DataRequest dataRequest){
        Integer leaveId = dataRequest.getInteger("leaveId");
        Optional<LeaveApplication> applyOpt = leaveApplicationRepository.findById(leaveId);
        if (applyOpt.isEmpty())
            return CommonMethod.getReturnMessageError("申请不存在");

        LeaveApplication leaveApplication = applyOpt.get();

        // 权限判断（可选）
        // 如果你已经通过前端限制角色，那这里可以不写；也可以加一个更保险的验证，如：
        // if (!当前用户是学生本人) return CommonMethod.getReturnMessageError("无权修改");

        Integer teacherId = dataRequest.getInteger("teacherId");
        String reason = dataRequest.getString("reason");
        Date startDate = dataRequest.getDate("startDate");
        Date endDate = dataRequest.getDate("endDate");
        Date applyTime = dataRequest.getDate("applyTime");

        if (startDate == null || endDate == null)
            return CommonMethod.getReturnMessageError("开始时间或结束时间不能为空");
        if (!endDate.after(startDate))
            return CommonMethod.getReturnMessageError("结束时间必须在开始时间之后");
        if (teacherId == null)
            return CommonMethod.getReturnMessageError("请选择审批老师");
        if (reason == null)
            return CommonMethod.getReturnMessageError("请假理由不能为空");

        // 学生只能改这几个字段
        if (teacherRepository.findByPersonPersonId(teacherId).isPresent())
            leaveApplication.setTeacher(teacherRepository.findByPersonPersonId(teacherId).get());
        leaveApplication.setReason(reason);
        leaveApplication.setStartDate(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        leaveApplication.setEndDate(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        leaveApplication.setApplyTime(applyTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        leaveApplicationRepository.save(leaveApplication);
        return CommonMethod.getReturnMessageOK("学生修改成功！");
    }


    public DataResponse updateTeacher(@Valid DataRequest dataRequest){
        Integer leaveId = dataRequest.getInteger("leaveId");
        Optional<LeaveApplication> applyOpt = leaveApplicationRepository.findById(leaveId);
        if (applyOpt.isEmpty())
            return CommonMethod.getReturnMessageError("申请不存在");
        LeaveApplication leaveApplication = applyOpt.get();
        Integer status = dataRequest.getInteger("status");
        String reply = dataRequest.getString("reply");
        if (reply == null)  reply = "";
        leaveApplication.setReply(reply);
        leaveApplication.setStatus(status);
        leaveApplicationRepository.save(leaveApplication);
        return CommonMethod.getReturnMessageOK("修改成功！");
    }

    public DataResponse deleteApplication(@Valid DataRequest dataRequest){
        Integer leaveId = dataRequest.getInteger("leaveId");
        leaveApplicationRepository.deleteById(leaveId);
        return CommonMethod.getReturnMessageOK("删除成功！");
    }

    public DataResponse reportCancel(@Valid DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        Optional<LeaveApplication> applyOpt = leaveApplicationRepository.findById(leaveId);
        LeaveApplication leaveApplication;
        if (applyOpt.isEmpty())
            return CommonMethod.getReturnMessageError("申请不存在");
        leaveApplication = applyOpt.get();

        Integer status = dataRequest.getInteger("status");
        if (status != 1)
            return CommonMethod.getReturnMessageError("该申请未通过，不能请求销假！");
        leaveApplication.setStatus(3);
        leaveApplicationRepository.save(leaveApplication);
        return CommonMethod.getReturnMessageOK("销假申请已提交！");
    }

    public DataResponse finishLeave(@Valid DataRequest dataRequest) {
        Integer leaveId = dataRequest.getInteger("leaveId");
        Optional<LeaveApplication> applyOpt = leaveApplicationRepository.findById(leaveId);
        LeaveApplication leaveApplication;
        if (applyOpt.isEmpty())
            return CommonMethod.getReturnMessageError("申请不存在");
        leaveApplication = applyOpt.get();

        Integer status = dataRequest.getInteger("status");
        if (status != 3)
            return CommonMethod.getReturnMessageError("学生未提交销假申请！");
        leaveApplication.setStatus(4);
        leaveApplicationRepository.save(leaveApplication);
        return CommonMethod.getReturnMessageOK("销假成功！");
    }
}
