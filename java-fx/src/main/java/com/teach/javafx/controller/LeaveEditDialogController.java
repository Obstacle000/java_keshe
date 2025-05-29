package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LeaveEditDialogController {

    @FXML
    private ComboBox<OptionItem> teacherComboBox;

    @FXML
    private TextField reasonField;
    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;


    @FXML
    private TextField replyField;
    @FXML private Button approveButton;
    @FXML private Button rejectButton;
    @FXML private Button cancelButton;
    @FXML private Button confirmCancelButton;
    @FXML private Button saveButton;

    private Map<String, Object> currentItem;

    public void setData(Map<String, Object> item) {
        boolean isTeacher = AppStore.getJwt().getRole().equals("ROLE_TEACHER");

        // 学生不允许编辑回复内容，教师可以
        replyField.setEditable(isTeacher);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.currentItem = item;
        reasonField.setText((String) item.get("reason"));
        replyField.setText((String) item.get("reply"));

        try {
            // 处理开始时间
            String startDateStr = (String) item.get("startDate");
            if (startDateStr != null && !startDateStr.isEmpty()) {
                Date startDate = sdf.parse(startDateStr);
                startDatePicker.setValue(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }

            // 处理结束时间
            String endDateStr = (String) item.get("endDate");
            if (endDateStr != null && !endDateStr.isEmpty()) {
                Date endDate = sdf.parse(endDateStr);
                endDatePicker.setValue(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 加载老师下拉
        DataRequest req = new DataRequest();
        List<OptionItem> optionItems = HttpRequestUtil.requestOptionItemList("/api/leave/getTeacherItemOptionList", req);
        if (optionItems != null ) {
            List<OptionItem> options = optionItems;
            teacherComboBox.setItems(FXCollections.observableArrayList(options));

        } else {
            // 数据为空或接口失败，设为空列表，避免空指针
            teacherComboBox.setItems(FXCollections.observableArrayList());
        }

        // 根据身份和状态显示按钮
        Number statusNumber = (Number) item.get("status");
        int status = statusNumber.intValue();


        approveButton.setVisible(false);
        rejectButton.setVisible(false);
        cancelButton.setVisible(false);
        confirmCancelButton.setVisible(false);
        saveButton.setVisible(false);

        if (isTeacher) {
            if (status == 0) {
                approveButton.setVisible(true);
                rejectButton.setVisible(true);
            } else if (status == 3) {
                confirmCancelButton.setVisible(true); // 销假中
            }
        } else { // 学生
            if (status == 1) {
                cancelButton.setVisible(true); // 已批准，可以申请销假
            }
            if (status == 0) {
                saveButton.setVisible(true); // 保存
            }
        }
    }


    @FXML
    public void onSave() {
        boolean isTeacher = AppStore.getJwt().getRole().equals("ROLE_TEACHER");

        DataRequest req = new DataRequest();
        req.add("leaveId", currentItem.get("leaveId"));

        if (isTeacher) {
            // 教师只能修改 reply
            req.add("status", currentItem.get("status"));
            req.add("reply", replyField.getText());
            DataResponse res = HttpRequestUtil.request("/api/leave/updateTeacher", req);
            if (res != null && res.getCode() == 0) {
                ((Stage) reasonField.getScene().getWindow()).close();
            }
        } else {
            // 学生可以修改 reason、时间、teacher
            if (teacherComboBox.getValue() == null) return;
            req.add("teacherId", Integer.parseInt(teacherComboBox.getValue().getValue()));
            req.add("reason", reasonField.getText());
            req.add("reply", replyField.getText()); // 也可以加上，避免被清空

            if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
                req.add("startDate", startDatePicker.getValue().toString()); // 例如 "2025-05-29"
                req.add("endDate", endDatePicker.getValue().toString());
            }

            req.add("applyTime", currentItem.get("applyTime")); // 不变

            DataResponse res = HttpRequestUtil.request("/api/leave/updateStudent", req);
            if (res != null && res.getCode() == 0) {
                ((Stage) reasonField.getScene().getWindow()).close();
            }
        }

        LeavePanelController controller = new LeavePanelController();
        controller.refreshTable();
    }



    @FXML
    public void onApprove() {
        DataRequest req = new DataRequest();
        req.add("leaveId", currentItem.get("leaveId"));
        req.add("reply", replyField.getText());

        DataResponse res = HttpRequestUtil.request("/api/leave/approve", req);
        if (res != null && res.getCode() == 0) {
            ((Stage) replyField.getScene().getWindow()).close();  // 成功后关闭窗口
        }
        LeavePanelController controller = new LeavePanelController();
        controller.refreshTable();
    }


    @FXML
    public void onReject() {
        DataRequest req = new DataRequest();
        req.add("leaveId", currentItem.get("leaveId"));
        req.add("reply", replyField.getText());

        DataResponse res = HttpRequestUtil.request("/api/leave/disApprove", req);
        if (res != null && res.getCode() == 0) {
            ((Stage) replyField.getScene().getWindow()).close();  // 成功后关闭弹窗
        }
        LeavePanelController controller = new LeavePanelController();
        controller.refreshTable();

    }


    @FXML
    public void onCancelRequest() {
        DataRequest req = new DataRequest();
        req.add("leaveId", currentItem.get("leaveId"));
        req.add("status", currentItem.get("status")); // 后端需要验证 status==1

        DataResponse res = HttpRequestUtil.request("/api/leave/reportCancel", req);
        if (res != null && res.getCode() == 0) {
            ((Stage) cancelButton.getScene().getWindow()).close(); // 成功后关闭弹窗
        }
        LeavePanelController controller = new LeavePanelController();
        controller.refreshTable();
    }

    @FXML
    public void onConfirmCancel() {
        DataRequest req = new DataRequest();
        req.add("leaveId", currentItem.get("leaveId"));
        req.add("status", currentItem.get("status")); // 后端需要验证 status==3

        DataResponse res = HttpRequestUtil.request("/api/leave/finishLeave", req);
        if (res != null && res.getCode() == 0) {
            ((Stage) confirmCancelButton.getScene().getWindow()).close(); // 成功后关闭弹窗
        }
        LeavePanelController controller = new LeavePanelController();
        controller.refreshTable();
    }

}
