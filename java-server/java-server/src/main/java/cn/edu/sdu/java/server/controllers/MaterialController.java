package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Material;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.MyTreeNode;
import cn.edu.sdu.java.server.services.MaterialService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/material")
@Validated
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    // 获取素材树结构
    @PostMapping("/getMaterialTreeNode")
    public List<MyTreeNode> getMaterialTreeNode(@Valid @RequestBody DataRequest dataRequest) {
        return materialService.getRootMaterials(dataRequest);
    }

    // 上传文件
    @PostMapping("/uploadMaterial")
    public DataResponse uploadMaterial(@RequestBody byte[] barr,
                                       @RequestParam(name = "uploader") String uploader,
                                       @RequestParam(name = "remoteFile") String remoteFile,
                                       @RequestParam(name = "fileName") String fileName,
                                       @RequestParam(name = "id") String id,
                                       @RequestParam(name = "value") String courseName,
                                       @RequestParam(name = "title") String title,
                                       @RequestParam(name = "pid") String pid)  throws IOException {
        // 处理上传的逻辑
        return materialService.uploadFile(barr,remoteFile,uploader,fileName,id,courseName,title,pid);

    }

    // 修改文件标题
    @PostMapping("/updateMaterialTitle")
    public DataResponse updateMaterialTitle(@Valid @RequestBody DataRequest dataRequest) {
        return materialService.updateMaterialTitle(dataRequest);

    }

    // 删除文件或目录
    @PostMapping("/deleteMaterial")
    public DataResponse deleteMaterial(@Valid @RequestBody DataRequest dataRequest) {
        return materialService.deleteMaterial(dataRequest);

    }

    // 下载文件
    @PostMapping("/getFileByteData")
    public DataResponse getFileByteData(@Valid @RequestBody DataRequest dataRequest) {
        return materialService.getFileByteData(dataRequest);
    }

}
