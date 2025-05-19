package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.CompetitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competition")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    @PostMapping("/getCompetitionList")
    public DataResponse getCompetitionList(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getCompetitionList(dataRequest);
    }

    @PostMapping("/getCompetitionContent")
    public DataResponse getCompetitionContent(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getCompetitionContent(dataRequest);
    }

    // 添加,更新,删除接口


    // 能抽到Notice,懒得抽了,直接用活动的
    /*@PostMapping("getNoticeList")
    public DataResponse getNoticeList(@Valid DataRequest dataRequest) {

        return competitionService.getNoticeList(dataRequest);
    }*/


}
