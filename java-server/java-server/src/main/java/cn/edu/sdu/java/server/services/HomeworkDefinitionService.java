package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class HomeworkDefinitionService {
    @Autowired
    HomeworkSubmissionRepository homeworkSubmissionRepository;
    @Autowired
    StudentRepository studentRepository;
    private final HomeworkDefinitionRepository homeworkDefinitionRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    public HomeworkDefinitionService(HomeworkDefinitionRepository homeworkDefinitionRepository,CourseRepository courseRepository,TeacherRepository teacherRepository){
        this.homeworkDefinitionRepository = homeworkDefinitionRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
    }

    public DataResponse getHomeworkList(DataRequest dataRequest){
        Integer personId = dataRequest.getInteger("personId");
        List<HomeworkDefinition> cList = homeworkDefinitionRepository.findByTeacherPersonId(personId);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (HomeworkDefinition h : cList) {
            m = new HashMap<>();
            m.put("definitionId", h.getDefinitionId()+"");
            m.put("homeworkTitle",h.getHomeworkTitle());
            m.put("course",h.getCourse());
            m.put("homeworkContent",h.getHomeworkContent());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse homeworkSave(DataRequest dataRequest){
        Integer definitionId = dataRequest.getInteger("definitionId");
        String homeworkTitle = dataRequest.getString("homeworkTitle");
        String homeworkContent = dataRequest.getString("homeworkContent");
        Integer courseId = dataRequest.getInteger("courseId");
        Integer personId = dataRequest.getInteger("personId");
        Optional<Course> h = courseRepository.findByCourseId(courseId);
        Optional<Teacher> t = teacherRepository.findByPersonPersonId(personId);
        Course course;
        Teacher teacher;
        if (h.isPresent()){
            course = h.get();
        }else{
            course = new Course();
        }
        if (t.isPresent()){
            teacher = t.get();
        }else{
            teacher = new Teacher();
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
        c.setHomeworkContent(homeworkContent);
        c.setCourse(course);
        c.setTeacher(teacher);
        homeworkDefinitionRepository.save(c);




        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse homeworkDelete(DataRequest dataRequest){
        Integer definitionId = dataRequest.getInteger("definitionId");  //获取student_id值
        HomeworkDefinition h = null;
        Optional<HomeworkDefinition> op;
        if (definitionId != null) {
            op = homeworkDefinitionRepository.findById(definitionId);   //查询获得实体对象
            if(op.isPresent()) {
                h = op.get();
                homeworkDefinitionRepository.delete(h);
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    public DataResponse homeworkAdd(@Valid @RequestBody DataRequest dataRequest){
        String homeworkTitle = dataRequest.getString("homeworkTitle");
        Integer courseId = dataRequest.getInteger("courseId");
        String homeworkContent = dataRequest.getString("homeworkContent");
        Integer personId = dataRequest.getInteger("personId");

        if (homeworkTitle == null || homeworkContent == null) {
            return new DataResponse(1,null,"作业标题或内容不能为空！");
        }

        // 构建课程对象（假设你有 Homework 实体类）
        HomeworkDefinition homework = new HomeworkDefinition();
        homework.setHomeworkTitle(homeworkTitle);
        homework.setHomeworkContent(homeworkContent);

        Optional<Course> cOp = courseRepository.findById(courseId);
        Optional<Teacher> tOp = teacherRepository.findByPersonPersonId(personId);
        cOp.ifPresent(homework::setCourse);
        tOp.ifPresent(homework::setTeacher);

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

}
