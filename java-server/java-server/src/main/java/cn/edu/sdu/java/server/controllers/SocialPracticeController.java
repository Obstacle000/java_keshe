package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.SocialPracticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.edu.sdu.java.server.models.SocialPractice;


@RestController
@RequestMapping("/api/socialPractice")
public class SocialPracticeController {
    @Autowired
    private SocialPracticeService socialPracticeService;


    /**
     * 返回所有社会实践
     * @param dataRequest
     * @return
     */
    @PostMapping("/getSocialPracticeList")
    public DataResponse getSocialPracticeList(@Valid @RequestBody DataRequest dataRequest) {
        return socialPracticeService.getSocialPracticeList(dataRequest);
    }

    /**
     * 获取通知内容
     * @param dataRequest
     * @return
     */
    @PostMapping("/getSocialPracticeContent")
    public DataResponse getSocialPracticeContent(@Valid @RequestBody DataRequest dataRequest) {
        return socialPracticeService.getSocialPracticeContent(dataRequest);
    }

    /**
     * 创建社会实践,社会实践和通知分别创建
     * @param dataRequest
     * @return
     */
    @PostMapping("/addSocialPractice")
    public DataResponse addSocialPractice(@Valid @RequestBody DataRequest dataRequest) {
        return socialPracticeService.addSocialPractice(dataRequest);
    }

    /**
     * 更新和添加一样弹出窗口,然后回显
     * @param dataRequest
     * @return
     */
    @PostMapping("/updateSocialPractice")
    public DataResponse updateSocialPractice(@Valid @RequestBody DataRequest dataRequest) {
        return socialPracticeService.updateSocialPractice(dataRequest);
    }

    @PostMapping("/deleteSocialPractice")
    public DataResponse deleteSocialPractice(@Valid @RequestBody DataRequest dataRequest) {
        return socialPracticeService.deleteSocialPractice(dataRequest);
    }

    /**
     * 用户前端添加活动的时候会看到一个下拉框选择对应通知,此时请求后端拿到通知Id,然后在添加社会实践的时候带上noticeId
     * @param dataRequest
     * @return
     */
    @PostMapping("getNoticeList")
    public DataResponse getNoticeList(@Valid DataRequest dataRequest) {

        return socialPracticeService.getNoticeList(dataRequest);
    }



}



