package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class LeaveApplyDialogController {

    @FXML
    private ComboBox<OptionItem> teacherComboBox;

    @FXML
    private TextField reasonField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button submitButton;

    private Integer studentId; // 需要从登录信息或调用方传进来

    @FXML
    public void initialize() {
        studentId = AppStore.getJwt().getId();
        // 加载老师列表
        DataRequest req = new DataRequest();
        List<OptionItem> optionItems = HttpRequestUtil.requestOptionItemList("/api/leave/getTeacherItemOptionList", req);
        if (optionItems != null ) {
            List<OptionItem> options = optionItems;
            if (options == null) {
                options = java.util.Collections.emptyList();
            }
            teacherComboBox.setItems(FXCollections.observableArrayList(options));
        } else {
            teacherComboBox.setItems(FXCollections.observableArrayList()); // 空列表，防止异常
        }
    }


    @FXML
    public void onSubmit() {
        if (teacherComboBox.getValue() == null) {
            MessageDialog.showDialog("请选择审批老师");
            return;
        }
        if (reasonField.getText() == null || reasonField.getText().isEmpty()) {
            MessageDialog.showDialog("请填写请假理由");
            return;
        }
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            MessageDialog.showDialog("请选择开始和结束日期");
            return;
        }
        if (!endDatePicker.getValue().isAfter(startDatePicker.getValue())) {
            MessageDialog.showDialog("结束日期必须在开始日期之后");
            return;
        }

        DataRequest req = new DataRequest();
        req.add("studentId", studentId);
        req.add("teacherId", Integer.parseInt(teacherComboBox.getValue().getValue()));
        req.add("reason", reasonField.getText());
        req.add("startDate", startDatePicker.getValue().toString()); // 例如 "2025-05-29"
        req.add("endDate", endDatePicker.getValue().toString());
        req.add("applyTime", new Date());

        DataResponse res = HttpRequestUtil.request("/api/leave/apply", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("申请成功！");
            // 关闭窗口
            ((Stage) submitButton.getScene().getWindow()).close();
            // 刷新列表或其他操作
        } else {
            MessageDialog.showDialog("申请失败：" + (res != null ? res.getMsg() : "网络错误"));
        }
    }



    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
}
