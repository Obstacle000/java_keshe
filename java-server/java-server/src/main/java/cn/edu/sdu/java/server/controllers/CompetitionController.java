package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.CompetitionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competition")
public class CompetitionController {
    @Autowired
    private CompetitionService competitionService;

    /**
     * 返回所有活动
     * @param dataRequest
     * @return
     */
    @PostMapping("/getCompetitionList")
    public DataResponse getCompetitionList(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getCompetitionList(dataRequest);
    }

    /**
     * 获取通知内容
     * @param dataRequest
     * @return
     */
    @PostMapping("/getCompetitionContent")
    public DataResponse getCompetitionContent(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getCompetitionContent(dataRequest);
    }

    /**
     * 创建活动,活动和通知分别创建
     * @param dataRequest
     * @return
     */
    @PostMapping("/addCompetition")
    public DataResponse addCompetition(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.addCompetition(dataRequest);
    }

    /**
     * 更新和添加一样弹出窗口,然后回显
     * @param dataRequest
     * @return
     */
    @PostMapping("/updateCompetition")
    public DataResponse updateCompetition(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.updateCompetition(dataRequest);
    }

    @PostMapping("/deleteCompetition")
    public DataResponse deleteCompetition(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.deleteCompetition(dataRequest);
    }

    /**
     * 用户前端添加活动的时候会看到一个下拉框选择对应通知,此时请求后端拿到通知Id,然后在添加活动的时候带上noticeId
     * @param dataRequest
     * @return
     */
    @PostMapping("/getNoticeList")
    public DataResponse getNoticeList(@Valid @RequestBody DataRequest dataRequest) {

        return competitionService.getNoticeList(dataRequest);
    }
    @PostMapping("/score")
    public DataResponse CompetitionScore(@Valid @RequestBody DataRequest dataRequest){
        return competitionService.CompetitionScore(dataRequest);
    }

    @PostMapping("/getScoreRecord")
    public DataResponse getScoreRecord(@Valid @RequestBody DataRequest dataRequest){
        return competitionService.getRecord(dataRequest);
    }




}
