package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TeacherService;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    // 用于表格显示
    @PostMapping("/getTeacherList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getStudentList(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherList(dataRequest);
    }

    @PostMapping("/teacherDelete")
    public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherDelete(dataRequest);
    }

    // 显示在右边表单
    @PostMapping("/getTeacherInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherInfo(dataRequest);
    }

    @PostMapping("/teacherEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse teacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherEditSave(dataRequest);
    }

    @PostMapping("/importExcelData")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse importTeacherExcel(@RequestParam("file") MultipartFile file,@RequestParam(value = "paras", required = false) String paras) {
        try {
            return teacherService.importTeacherExcel(file);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonMethod.getReturnMessageError("导入失败！");
        }
    }

    // 上传excel表文件接口...等等很多其他接口
}
