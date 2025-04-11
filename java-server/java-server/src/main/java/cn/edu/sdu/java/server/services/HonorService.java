package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class HonorService {
    public OptionItemList getNameItemOptionList(@Valid DataRequest dataRequest) {
        return null;
    }

    public DataResponse getHonorList(@Valid DataRequest dataRequest) {
        return null;
    }

    public DataResponse honorSave(@Valid DataRequest dataRequest) {
        return null;
    }

    public DataResponse honorDelete(@Valid DataRequest dataRequest) {
        return null;
    }
}
