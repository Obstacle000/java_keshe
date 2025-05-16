package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.HomeworkDefinition;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.HomeworkDefinitionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/homeworkDefinition")
public class HomeworkDefinitionController {
    private final HomeworkDefinitionService homeworkDefinitionService;
    public HomeworkDefinitionController(HomeworkDefinitionService homeworkDefinitionService){
        this.homeworkDefinitionService = homeworkDefinitionService;
    }

    /**
     * 教师
     * @param dataRequest
     * @return
     */
    @PostMapping("/getHomeworkList")
    public DataResponse getHomeworkList(@Valid @RequestBody DataRequest dataRequest){
        return homeworkDefinitionService.getHomeworkList(dataRequest);
    }

    /**
     * 学生
     * @param dataRequest
     * @return
     */
    @PostMapping("/getHomeworkListStudent")
    public DataResponse getHomeworkListStudent(@Valid @RequestBody DataRequest dataRequest){
        return homeworkDefinitionService.getHomeworkListStudent(dataRequest);
    }

    @PostMapping("/homeworkSave")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse homeworkSave(@Valid @RequestBody DataRequest dataRequest){
        return homeworkDefinitionService.homeworkSave(dataRequest);
    }

    @PostMapping("/homeworkDelete")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse homeworkDelete(@Valid @RequestBody DataRequest dataRequest){
        return homeworkDefinitionService.homeworkDelete(dataRequest);
    }

    @PostMapping("/homeworkAdd")
    @PreAuthorize("hasRole('TEACHER')")
    public DataResponse homeworkAdd(@Valid @RequestBody DataRequest dataRequest){
        return homeworkDefinitionService.homeworkAdd(dataRequest);
    }
}
