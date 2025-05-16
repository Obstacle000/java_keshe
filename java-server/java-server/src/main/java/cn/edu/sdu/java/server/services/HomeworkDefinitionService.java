package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

/*
HomeworkService
需要的功能
老师：
1.getHomeworkList 查看当前作业列表
2.HomeworkAdd 布置作业
3.HomeworkDelete 删除作业
4.HomeworkSave 修改作业并保存
5.HomeworkCheck 查看作业完成情况


学生：
1.getHomeworkList 查看当前作业列表
2.HomeworkDone 标注完成作业


其中与作业提交相关的功能请移步HomeworkSubmission板块
 */

@Service
@RequiredArgsConstructor
public class HomeworkDefinitionService {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    HomeworkSubmissionRepository homeworkSubmissionRepository;
    @Autowired
    StudentRepository studentRepository;
    private final HomeworkDefinitionRepository homeworkDefinitionRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;


    public DataResponse getHomeworkList(DataRequest dataRequest){
        Integer personId = dataRequest.getInteger("personId");
        List<HomeworkDefinition> cList = homeworkDefinitionRepository.findAll();  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        String name = personRepository.findById(personId).get().getName();
        for (HomeworkDefinition h : cList) {
            m = new HashMap<>();
            m.put("num", h.getDefinitionId()+"");
            m.put("title",h.getHomeworkTitle());
            m.put("course",h.getCourse().getName());
            m.put("teacher",name);
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse homeworkSave(DataRequest dataRequest){
        Integer definitionId = dataRequest.getInteger("num");
        String homeworkTitle = dataRequest.getString("title");
        String courseName = dataRequest.getString("course");
        Integer personId = dataRequest.getInteger("personId");
        Optional<Course> h = courseRepository.findByName(courseName);
        Optional<Person> t = personRepository.findByPersonId(personId);
        Course course;
        Person person;
        if (h.isPresent()){
            course = h.get();
        }else{
            course = new Course();
        }
        if (t.isPresent()){
            person = t.get();
        }else{
            person = new Person();
        }
        Optional<HomeworkDefinition> op;
        HomeworkDefinition c= null;

        if(definitionId != null) {
            op = homeworkDefinitionRepository.findByDefinitionId(definitionId);
            if(op.isPresent())
                c= op.get();
        }
        if(c== null)
            c = new HomeworkDefinition();
        c.setHomeworkTitle(homeworkTitle);
        c.setCourse(course);
        c.setPerson(person);
        homeworkDefinitionRepository.save(c);




        return CommonMethod.getReturnMessageOK();
    }
    @Transactional
    public DataResponse homeworkDelete(DataRequest dataRequest) {
        Integer definitionId = dataRequest.getInteger("num");
        if (definitionId != null) {
            Optional<HomeworkDefinition> op = homeworkDefinitionRepository.findById(definitionId);
            if (op.isPresent()) {
                HomeworkDefinition h = op.get();
                // 第一步：删除关联的提交记录
                try {
                    homeworkSubmissionRepository.deleteByHomeworkDefinitionDefinitionId(definitionId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 第二步：删除作业定义
                homeworkDefinitionRepository.delete(h);
            }
        }
        return CommonMethod.getReturnMessageOK();  // 通知前端操作正常
    }


    public DataResponse homeworkAdd(DataRequest dataRequest){
        String homeworkTitle = dataRequest.getString("title");
        String course = dataRequest.getString("course");

        Integer personId = dataRequest.getInteger("personId");

        if (homeworkTitle == null ) {
            return new DataResponse(1,null,"作业标题不能为空！");
        }

        // 构建课程对象（假设你有 Homework 实体类）
        HomeworkDefinition homework = new HomeworkDefinition();
        homework.setHomeworkTitle(homeworkTitle);

        Optional<Course> cOp = courseRepository.findByName(course);
        Optional<Person> pOp = personRepository.findByPersonId(personId);
        if (cOp.isPresent()) {
            homework.setCourse(cOp.get());
        }else {
            return CommonMethod.getReturnMessageError("请正确填写课程名");
        }
        if (pOp.isPresent()) {
            homework.setPerson(pOp.get());
        }

        // 保存到数据库（假设你有 homeworkService）

        homeworkDefinitionRepository.save(homework);


        // 导入学生到submission
        List<Student> Students = studentRepository.findAll();

        List<HomeworkSubmission> submissionList = new ArrayList<>();
        for (Student s : Students) {
            HomeworkSubmission hs = new HomeworkSubmission();
            hs.setStudent(s);
            hs.setHomeworkDefinition(homework);
            submissionList.add(hs);
        }
        homeworkSubmissionRepository.saveAll(submissionList);

        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse getHomeworkListStudent(@Valid DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");// 学生personId
        Optional<Student> byPersonPersonId = studentRepository.findByPersonPersonId(personId);
        Student s = null;
        if (byPersonPersonId.isPresent()) {
            s = byPersonPersonId.get();
        }
        List<HomeworkSubmission> byStudent = homeworkSubmissionRepository.findByStudent(s);

        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (HomeworkSubmission hs : byStudent) {
            HomeworkDefinition h = hs.getHomeworkDefinition();
            String name = h.getPerson().getName();
            m = new HashMap<>();
            m.put("num", h.getDefinitionId()+"");
            m.put("title",h.getHomeworkTitle());
            m.put("course",h.getCourse().getName());
            m.put("teacher",name);
            m.put("status",hs.getCompleted());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);

    }
}
