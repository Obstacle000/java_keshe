package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Notice;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.ActivityService;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    /**
     * 返回所有活动
     * @param dataRequest
     * @return
     */
    @PostMapping("/getActivityList")
    public DataResponse getActivityList(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getActivityList(dataRequest);
    }

    /**
     * 获取通知内容
     * @param dataRequest
     * @return
     */
    @PostMapping("/getActivityContent")
    public DataResponse getActivityContent(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getActivityContent(dataRequest);
    }

    /**
     * 创建活动,活动和通知分别创建
     * @param dataRequest
     * @return
     */
    @PostMapping("/addActivity")
    public DataResponse addActivity(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.addActivity(dataRequest);
    }

    /**
     * 更新和添加一样弹出窗口,然后回显
     * @param dataRequest
     * @return
     */
    @PostMapping("/updateActivity")
    public DataResponse updateActivity(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.updateActivity(dataRequest);
    }

    @PostMapping("/deleteActivity")
    public DataResponse deleteActivity(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.deleteActivity(dataRequest);
    }

    /**
     * 用户前端添加活动的时候会看到一个下拉框选择对应通知,此时请求后端拿到通知Id,然后在添加活动的时候带上noticeId
     * @param dataRequest
     * @return
     */
    @PostMapping("getNoticeList")
    public DataResponse getNoticeList(@Valid DataRequest dataRequest) {

        return activityService.getNoticeList(dataRequest);
    }


}
