package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.SocialPracticeSignupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/socialPracticeSignup")
public class SocialPracticeSignupController {
    @Autowired
    private SocialPracticeSignupService socialPracticeSignUpService;


    @RequestMapping("/signUp")
    public DataResponse signUp(@Valid @RequestBody DataRequest dataRequest){
        return socialPracticeSignUpService.signup(dataRequest);
    }

    @RequestMapping("/cancelSignUp")
    public DataResponse cancelSignUp(@Valid @RequestBody DataRequest dataRequest){
        return socialPracticeSignUpService.cancelSignup(dataRequest);
    }

    @RequestMapping("/submitProofMaterial")
    public DataResponse submitProofMaterial(@Valid @RequestBody DataRequest dataRequest){
        return socialPracticeSignUpService.submitProofMaterial(dataRequest);
    }

    @RequestMapping("/getSignupList")
    public DataResponse getSignupList(@Valid @RequestBody DataRequest dataRequest) {
        return socialPracticeSignUpService.getSignupList(dataRequest);
    }


}