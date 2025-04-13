package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.HonorService;
import cn.edu.sdu.java.server.services.ScoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/honor")
public class HonorController {
    @Autowired
    private HonorService honorService;
    @PostMapping("/getPersonItemOptionList")
    public OptionItemList getPersonItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.getPersonItemOptionList(dataRequest);
    }

    @PostMapping("/getHonorList")
    public DataResponse getHonorList(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.getHonorList(dataRequest);
    }
    @PostMapping("/honorSave")
    public DataResponse honorSave(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.honorSave(dataRequest);
    }
    @PostMapping("/honorDelete")
    public DataResponse honorDelete(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.honorDelete(dataRequest);
    }
}
