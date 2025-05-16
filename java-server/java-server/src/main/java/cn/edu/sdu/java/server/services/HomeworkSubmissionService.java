package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
@Transactional
public class HomeworkSubmissionService {
    private final HomeworkSubmissionRepository submissionRepository;
    private final HomeworkDefinitionRepository definitionRepository;

    private final StudentRepository studentRepository;
    private final PersonRepository personRepository;

    public HomeworkSubmissionService(HomeworkSubmissionRepository submissionRepository,
                                     HomeworkDefinitionRepository definitionRepository,
                                     StudentRepository studentRepository, PersonRepository personRepository) {
        this.submissionRepository = submissionRepository;
        this.definitionRepository = definitionRepository;
        this.studentRepository = studentRepository;
        this.personRepository = personRepository;
    }

    /**
     * 学生提交作业状态（完成/未完成）
     * student   提交学生
     * definitionId 作业定义ID
     * completed 是否完成
     * answer    学生答案（可选）
     */
    public DataResponse submitHomeworkCompletion(DataRequest dataRequest) {

        String answer = dataRequest.getString("answer");//获取作业定义
        Student student = new Student();
        if (studentRepository.findByPersonPersonId(dataRequest.getInteger("personId")).isPresent())
            student = studentRepository.findByPersonPersonId(dataRequest.getInteger("personId")).get();

        Integer studentId = student.getPersonId();
        Integer definitionId = dataRequest.getInteger("definitionId");
        Optional<HomeworkSubmission> oSubmission = submissionRepository.findByStudentPersonIdAndHomeworkDefinitionDefinitionId(studentId, definitionId);
        HomeworkSubmission submission = new HomeworkSubmission();
        if (oSubmission.isPresent()) {
            submission = oSubmission.get();
        }
            submission.setCompleted(true);
            submission.setStudentAnswer(answer);
            submissionRepository.save(submission);
            return CommonMethod.getReturnMessageOK();

    }
    /**
     * 教师查看某作业的所有学生提交情况
     * definitionId 作业定义ID
     */
    public DataResponse getHomeworkSubmissions(@Valid DataRequest dataRequest) {
        Integer definitionId = dataRequest.getInteger("definitionId");
        if (definitionId == null) {
            return CommonMethod.getReturnMessageError("缺少作业定义ID");
        }

        // 查询未提交该作业的学生ID
        List<Integer> uncompletedStudentIds = submissionRepository.findUncompletedStudentIdsByDefinitionId(definitionId);
        if (uncompletedStudentIds.isEmpty()) {
            return CommonMethod.getReturnData(Collections.emptyList());
        }

        // 查询学生信息
        List<Student> uncompletedList = studentRepository.findAllById(uncompletedStudentIds);


        // 封装为 Map 并加上 status 字段
        List<Map<String, Object>> result = new ArrayList<>();
        for (Student student : uncompletedList) {
            Integer personId = student.getPersonId();
            Person person = personRepository.findById(personId).get();

            Map<String, Object> map = new HashMap<>();
            map.put("num", person.getNum());
            map.put("studentName", person.getName());
            map.put("status", false);
            result.add(map);
        }

        return CommonMethod.getReturnData(result);
    }
}

