package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class CompetitionService {






    public DataResponse getNoticeList(@Valid DataRequest dataRequest) {
        return null;
    }

    public DataResponse getCompetitionList(@Valid DataRequest dataRequest) {
        return null;
    }

    public DataResponse getCompetitionContent(@Valid DataRequest dataRequest) {
        return null;
    }
}
