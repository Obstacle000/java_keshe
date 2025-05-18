package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.HomeworkDefinition;
import cn.edu.sdu.java.server.models.HomeworkSubmission;
import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.HomeworkDefinitionRepository;
import cn.edu.sdu.java.server.repositorys.HomeworkSubmissionRepository;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final ScoreRepository scoreRepository;

    @Autowired
    private HomeworkSubmissionRepository homeworkSubmissionRepository;
    @Autowired
    private HomeworkDefinitionRepository homeworkDefinitionRepository;

    public CourseService(CourseRepository courseRepository, ScoreRepository scoreRepository) {
        this.courseRepository = courseRepository;
        this.scoreRepository = scoreRepository;
    }

    public DataResponse getCourseList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Course> cList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        Course pc;
        for (Course c : cList) {
            m = new HashMap<>();
            m.put("courseId", c.getCourseId()+"");
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("credit",c.getCredit()+"");
            m.put("coursePath",c.getCoursePath());
            pc =c.getPreCourse();
            if(pc != null) {
                m.put("preCourse",pc.getName());
                m.put("preCourseId",pc.getCourseId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse courseSave(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        String coursePath = dataRequest.getString("coursePath");
        Integer credit = dataRequest.getInteger("credit");
        String preCourse = dataRequest.getString("preCourse");
        Optional<Course> op;
        Course c= null;

        if(courseId != null) {
            op = courseRepository.findByCourseId(courseId);
            if(op.isPresent())
                c= op.get();
        }
        if(c== null)
            c = new Course();
        Course pc =null;
        if(preCourse != null) {
            op = courseRepository.findByName(preCourse);
            if(op.isPresent())
                pc = op.get();
        }
        c.setNum(num);
        c.setName(name);
        c.setCredit(credit);
        c.setCoursePath(coursePath);
        c.setPreCourse(pc);
        courseRepository.save(c);
        return CommonMethod.getReturnMessageOK();
    }
    @Transactional
    public DataResponse courseDelete(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Optional<Course> op;
        Course c= null;
        if(courseId != null) {
            op = courseRepository.findByCourseId(courseId);
            if(op.isPresent()) {
                c = op.get();
                // 判断是否为前序课
                List<Course> course = courseRepository.findByPreCourse(c);
                if(course.size() > 0) {
                    return CommonMethod.getReturnMessageError("该课程是前序课无法删除");
                }
                // 获取defination
                Optional<HomeworkDefinition> byCourseCourseId = homeworkDefinitionRepository.findByCourseCourseId(courseId);
                HomeworkDefinition homeworkDefinition = null;
                if(byCourseCourseId.isPresent()) {
                    homeworkDefinition = byCourseCourseId.get();
                }
                // 删除作业提交
                homeworkSubmissionRepository.deleteByHomeworkDefinition(homeworkDefinition);
                // 删除作业
                try {
                    if (homeworkDefinition != null) {
                        homeworkDefinitionRepository.delete(homeworkDefinition);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 删除分数
                scoreRepository.deleteByCourse(c);
                try {
                    courseRepository.delete(c);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse courseAdd(@RequestBody @Valid DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        Integer credit = dataRequest.getInteger("credit");
        String preCourse = dataRequest.getString("preCourse");

        if (num == null || name == null || credit == null) {
            return new DataResponse(1,null,"课程号、课程名或学分不能为空！");
        }

        // 构建课程对象（假设你有 Course 实体类）
        Course course = new Course();
        course.setNum(num);
        course.setName(name);
        course.setCredit(credit);

        Optional<Course> pcourseOp = courseRepository.findByName(preCourse);
        if(pcourseOp.isPresent()) {
            course.setPreCourse(pcourseOp.get());
        }

        // 保存到数据库（假设你有 courseService）
        courseRepository.save(course);

        return CommonMethod.getReturnMessageOK();
    }
}

