package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internship")
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    // 学生提交实习申请
    @PostMapping("/apply")
    public DataResponse apply(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.apply(dataRequest);
    }

    // 查询学生的实习记录
    @PostMapping("/listByStudent")
    public DataResponse listByStudent(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.listByStudent(dataRequest);
    }

    // 老师审批实习申请
    @PostMapping("/approve")
    public DataResponse approve(@Valid @RequestBody DataRequest dataRequest) {
        return internshipService.approve(dataRequest);
    }
}
