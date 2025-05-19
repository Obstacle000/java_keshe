package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.CompetitionSignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competition-signup")
@RequiredArgsConstructor
public class CompetitionSignupController {

    private final CompetitionSignupService service;

    @PostMapping("/newSignup")
    public DataResponse signup(@Valid @RequestBody DataRequest request) {
        return null;
    }


    @PostMapping("/cancelSignUp")
    public DataResponse cancelSignup(@Valid @RequestBody DataRequest request) {
        return null;
    }
}
