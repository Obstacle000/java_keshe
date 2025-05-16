package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.NoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    /**
     * 返回所有活动
     * @param dataRequest
     * @return
     */
    @PostMapping("/getNoticeList")
    public DataResponse getNoticeList(@Valid @RequestBody DataRequest dataRequest) {
        return noticeService.getNoticeList(dataRequest);
    }

    /**
     * 获取详情(内容)
     * @param dataRequest
     * @return
     */
    @PostMapping("/getNoticeContent")
    public DataResponse getNoticeContent(@Valid @RequestBody DataRequest dataRequest) {
        return noticeService.getNoticeContent(dataRequest);

    }

    /**
     * 添加
     * @param dataRequest
     * @return
     */
    @PostMapping("/addNotice")
    public DataResponse addNotice(@Valid @RequestBody DataRequest dataRequest) {
        return noticeService.addNotice(dataRequest);

    }

    @PostMapping("/updateNotice")
    public DataResponse updateNotice(@Valid @RequestBody DataRequest dataRequest) {
        return noticeService.updateNotice(dataRequest);
    }

    @PostMapping("/deleteNotice")
    public DataResponse deleteNotice(@Valid @RequestBody DataRequest dataRequest) {
        return noticeService.deleteNotice(dataRequest);
    }


}
