package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.ActivitySignUpService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signup")
public class ActivitySignUpController {
    @Autowired
    private ActivitySignUpService activityService;
    /**
     * 学生报名,插入数据
     * @param dataRequest
     * @return
     */
    @PostMapping("/newSignup")
    public DataResponse newSignUp(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.newSignUp(dataRequest);
    }

    @PostMapping("/cancelSignup")
    public DataResponse cancelSignUp(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.cancelSignUp(dataRequest);
    }



}
