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

    public HomeworkSubmissionService(HomeworkSubmissionRepository submissionRepository,
                                     HomeworkDefinitionRepository definitionRepository,
                                     StudentRepository studentRepository) {
        this.submissionRepository = submissionRepository;
        this.definitionRepository = definitionRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * 学生提交作业状态（完成/未完成）
     * student   提交学生
     * definitionId 作业定义ID
     * completed 是否完成
     * answer    学生答案（可选）
     */
    public DataResponse submitHomeworkCompletion(DataRequest dataRequest) {
        // 获取作业定义
        Student student;
        if (studentRepository.findByPersonPersonId(dataRequest.getInteger("personId")).isPresent())
            student = studentRepository.findByPersonPersonId(dataRequest.getInteger("personId")).get();
        else
            student = new Student();
        Integer definitionId = dataRequest.getInteger("definitionId");
        Boolean completed = dataRequest.getBoolean("completed");
        String answer = dataRequest.getString("answer");

        HomeworkDefinition definition;
        if (definitionRepository.findByDefinitionId(definitionId).isPresent())
            definition = definitionRepository.findByDefinitionId(definitionId).get();
        else{
            definition = new HomeworkDefinition();
        }
        // 查找或创建提交记录
        HomeworkSubmission submission = submissionRepository
                .findByStudentAndHomeworkDefinition(student, definition)
                .orElseGet(() -> {
                    HomeworkSubmission newSubmission = new HomeworkSubmission();
                    newSubmission.setStudent(student);
                    newSubmission.setHomeworkDefinition(definition);
                    return newSubmission;
                });

        // 更新状态
        submission.setCompleted(completed);
        submission.setStudentAnswer(answer);

        submissionRepository.save(submission);
        return CommonMethod.getReturnMessageOK();
    }

    /**
     * 教师查看某作业的所有学生提交情况
     *  definitionId 作业定义ID
     */
    public DataResponse getHomeworkSubmissions(DataRequest dataRequest) {
        Integer definitionId = dataRequest.getInteger("definitionId");
        HomeworkDefinition definition;
        if (definitionRepository.findByDefinitionId(definitionId).isPresent())
            definition = definitionRepository.findByDefinitionId(definitionId).get();
        else{
            definition = new HomeworkDefinition();
        }
        List<HomeworkSubmission> submissionList = submissionRepository.findByHomeworkDefinition(definition);
        return CommonMethod.getReturnData(submissionList);
    }
}
