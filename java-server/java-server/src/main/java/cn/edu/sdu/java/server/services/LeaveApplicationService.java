package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.LeaveApplication;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.LeaveApplicationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service

public class LeaveApplicationService {


    public DataResponse listByStudent(@Valid DataRequest dataRequest) {
        return null;
    }

    public DataResponse approve(@Valid DataRequest dataRequest) {
        return null;
    }

    public DataResponse apply(@Valid DataRequest dataRequest) {
        return null;
    }

    public DataResponse disApprove(@Valid DataRequest dataRequest) {
        return null;
    }
}
