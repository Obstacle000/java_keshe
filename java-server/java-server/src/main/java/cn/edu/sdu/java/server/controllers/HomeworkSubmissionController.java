package cn.edu.sdu.java.server.controllers;
import cn.edu.sdu.java.server.models.HomeworkSubmission;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.HomeworkDefinitionService;
import cn.edu.sdu.java.server.services.HomeworkSubmissionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/homeworkSubmission")

public class HomeworkSubmissionController {
    private final HomeworkSubmissionService homeworkSubmissionService;
    public HomeworkSubmissionController(HomeworkSubmissionService homeworkSubmissionService){
        this.homeworkSubmissionService = homeworkSubmissionService;
    }

    @PostMapping("/submitHomeworkCompletion")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse submitHomeworkCompletion(@Valid @RequestBody DataRequest dataRequest){
        return homeworkSubmissionService.submitHomeworkCompletion(dataRequest);
    }

    @PostMapping("/getHomeworkSubmissions")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse getHomeworkSubmissions(@Valid @RequestBody DataRequest dataRequest){
        return homeworkSubmissionService.getHomeworkSubmissions(dataRequest);
    }
}
