package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.LeaveApplication;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.LeaveApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveApplicationController {

    private final LeaveApplicationService leaveService;



    // 学生提交请假
    @PostMapping("/apply")
    public DataResponse apply(@Valid @RequestBody DataRequest dataRequest) {
        return leaveService.apply(dataRequest);
    }

    // 老师审批
    @PostMapping("/approve")
    public DataResponse approve(@Valid @RequestBody DataRequest dataRequest) {
        return leaveService.approve(dataRequest);
    }

    @PostMapping("/disApprove")
    public DataResponse disApprove(@Valid @RequestBody DataRequest dataRequest) {
        return leaveService.disApprove(dataRequest);
    }

    // 获取某个学生的所有请假申请
    @PostMapping("/studentList")
    public DataResponse listByStudent(@Valid @RequestBody DataRequest dataRequest) {
        return leaveService.listByStudent(dataRequest);
    }


}
