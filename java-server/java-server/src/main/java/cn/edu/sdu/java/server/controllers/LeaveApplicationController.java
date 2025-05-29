package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.LeaveApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveApplicationController {

    private final LeaveApplicationService leaveService;


    @PostMapping("/getApplyList")
    public DataResponse getApplyList(@Valid @RequestBody DataRequest dataRequest){
        return leaveService.getApplyList(dataRequest);
    }

    @PostMapping("/getTeacherItemOptionList")
    public OptionItemList getTeacherItemOptionList(@Valid @RequestBody DataRequest dataRequest){
        return leaveService.getTeacherItemOptionList(dataRequest);
    }

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

    @PostMapping("/updateStudent")
    public DataResponse updateStudent(@Valid @RequestBody DataRequest dataRequest){
        return leaveService.updateStudent(dataRequest);
    }

    @PostMapping("/updateTeacher")
    public DataResponse updateTeacher(@Valid @RequestBody DataRequest dataRequest){
        return leaveService.updateTeacher(dataRequest);
    }

    @PostMapping("/deleteApplication")
    public DataResponse deleteApplication(@Valid @RequestBody DataRequest dataRequest){
        return leaveService.deleteApplication(dataRequest);
    }

    @PostMapping("/reportCancel")
    public DataResponse reportCancel(@Valid @RequestBody DataRequest dataRequest){
        return leaveService.reportCancel(dataRequest);
    }

    @PostMapping("/finishLeave")
    public DataResponse finishLeave(@Valid @RequestBody DataRequest dataRequest){
        return leaveService.finishLeave(dataRequest);
    }
}
