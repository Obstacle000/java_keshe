package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.SocialPracticeSignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/social-practice-signup")
@RequiredArgsConstructor
public class SocialPracticeSignupController {

    private final SocialPracticeSignupService service;

    @PostMapping("/signup")
    public DataResponse signup(@Valid @RequestBody DataRequest request) {
        return null;
    }


    @PostMapping("/cancel")
    public DataResponse cancelSignup(@Valid @RequestBody DataRequest request) {
        return null;
    }
}
