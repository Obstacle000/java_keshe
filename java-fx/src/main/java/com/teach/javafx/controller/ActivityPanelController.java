package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;


public class ActivityPanelController {

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<Map<String, Object>> dataTableView;

    @FXML
    private TableColumn<Map, String> activityIdColumn;

    @FXML
    private TableColumn<Map, String> titleColumn;

    @FXML
    private TableColumn<Map, String> descriptionColumn;

    @FXML
    private TableColumn<Map, String> signupCountColumn;

    @FXML
    private TableColumn<Map, String> startTimeColumn;

    @FXML
    private TableColumn<Map, String> endTimeColumn;

    @FXML
    private TableColumn<Map, String> detailColumn;
    @FXML
    private TableColumn<Map, String> signupColumn;

    private List<Map<String, Object>> activityList;

    @FXML
    private void initialize() {
        // 初始化方法，绑定表格列，添加事件监听等
        dataTableView.setEditable(false);

        String currentUserRole = AppStore.getJwt().getRole();
        if ("ROLE_STUDENT".equals(currentUserRole)) {
            editButton.setVisible(false);
            deleteButton.setVisible(false);
            addButton.setVisible(false);
        }

        activityIdColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("activityId");
            if (val instanceof Number) {
                return new ReadOnlyStringWrapper(String.valueOf(((Number) val).intValue()));
            }
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });

        titleColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("title");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });


        descriptionColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("description");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });
        signupCountColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("signupCount");
            if (val instanceof Number) {
                return new ReadOnlyStringWrapper(String.valueOf(((Number) val).intValue()));
            }
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });

        startTimeColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("startTime");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });

        endTimeColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("endTime");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });
        // 详情按钮列
        detailColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(""));
        detailColumn.setCellFactory(col -> new TableCell<>() {
            private final Button detailBtn = new Button("查看");

            {
                detailBtn.setOnAction(event -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    showDetail(row);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailBtn);
                }
            }
        });
        // 报名按钮列设置
        signupColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper("")); // 内容空，因为按钮在cellFactory里显示

        signupColumn.setCellFactory(col -> new TableCell<>() {
            private final Button signupBtn = new Button("报名");
            private final Button cancelSignupBtn = new Button("取消报名");
            private final HBox buttonBox = new HBox(5); // 水平排列按钮，间距5

            {
                signupBtn.setOnAction(event -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    handleSignup(row);
                });

                cancelSignupBtn.setOnAction(event -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    handleCancelSignup(row);
                });

                buttonBox.getChildren().addAll(signupBtn, cancelSignupBtn);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    Boolean isSignedUp = (Boolean) row.get("isSignedUp");
                    signupBtn.setVisible(isSignedUp == null || !isSignedUp);
                    cancelSignupBtn.setVisible(Boolean.TRUE.equals(isSignedUp));
                    setGraphic(buttonBox);
                }
            }
        });

        // 表格选中监听，控制更新和删除按钮状态
        dataTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            editButton.setDisable(!selected);
            deleteButton.setDisable(!selected);
        });
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        // 加载通知数据
        loadActivityData();
    }
    private void loadActivityData() {
        DataRequest req = new DataRequest();
        req.add("personId", AppStore.getJwt().getId());

        DataResponse res = HttpRequestUtil.request("/api/activity/getActivityList", req);
        if (res != null && res.getCode() == 0) {
            activityList = (List<Map<String, Object>>) res.getData();
            if (activityList != null) {
                dataTableView.getItems().setAll(activityList);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "加载活动列表失败");
            alert.showAndWait();
        }
    }


    private void showDetail(Map<String, Object> activity) {
        // 从表格拿到noticeId
        Object activityId = activity.get("activityId");
        if (activityId == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "通知ID为空，无法查看详情");
            alert.showAndWait();
            return;
        }

        DataRequest req = new DataRequest();
        req.add("activityId", activityId);

        // 调用后端接口，获取通知详情
        DataResponse res = HttpRequestUtil.request("/api/activity/getActivityContent", req);

        if (res != null && res.getCode() == 0) {
            Map<String, Object> detailData = (Map<String, Object>) res.getData();
            String title = (String) detailData.get("title");
            String content = (String) detailData.get("content");
            String createTime = detailData.get("createTime") == null ? "" : detailData.get("createTime").toString();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("通知详情");
            alert.setHeaderText(title);
            alert.setContentText("内容：\n" + content + "\n\n创建时间：" + createTime);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "获取通知详情失败");
            alert.showAndWait();
        }
    }
    private void handleSignup(Map<String, Object> row) {
        Integer personId = AppStore.getJwt().getId();
        Object activityIdObj = row.get("activityId");
        int activityId = parseActivityId(activityIdObj);
        if (activityId == -1) return;

        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("activityId", activityId);

        DataResponse res = HttpRequestUtil.request("/api/signup/newSignup", req);
        if (res != null && res.getCode() == 0) {
            showAlert("报名成功", Alert.AlertType.INFORMATION);
            row.put("isSignedUp", true);
            dataTableView.refresh();
            loadActivityData();
        } else {
            showAlert("报名失败：" + (res == null ? "无响应" : res.getMsg()), Alert.AlertType.ERROR);
        }
    }

    private void handleCancelSignup(Map<String, Object> row) {
        Integer personId = AppStore.getJwt().getId();
        Object activityIdObj = row.get("activityId");
        int activityId = parseActivityId(activityIdObj);
        if (activityId == -1) return;

        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("activityId", activityId);

        DataResponse res = HttpRequestUtil.request("/api/signup/cancelSignup", req);
        if (res != null && res.getCode() == 0) {
            showAlert("取消报名成功", Alert.AlertType.INFORMATION);
            row.put("isSignedUp", false);
            dataTableView.refresh();
            loadActivityData();
        } else {
            showAlert("取消报名失败：" + (res == null ? "无响应" : res.getMsg()), Alert.AlertType.ERROR);
        }
    }

    private int parseActivityId(Object activityIdObj) {
        if (activityIdObj instanceof Number) {
            return ((Number) activityIdObj).intValue();
        }
        try {
            return Integer.parseInt(activityIdObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("活动ID格式错误", Alert.AlertType.ERROR);
            return -1;
        }
    }



    @FXML
    private void onAddButtonClicked() {
        // 创建弹窗
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("添加活动");

        // 创建内容面板
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("标题");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("描述");

        TextField startDateField = new TextField();
        startDateField.setPromptText("开始时间 (格式 yyyy-MM-dd)");

        TextField endDateField = new TextField();
        endDateField.setPromptText("结束时间 (格式 yyyy-MM-dd)");

        ComboBox<Map<String, Object>> noticeComboBox = new ComboBox<>();
        noticeComboBox.setPromptText("请选择通知");

        // 拉取通知列表填充下拉框
        DataRequest noticeReq = new DataRequest();
        DataResponse noticeRes = HttpRequestUtil.request("/api/activity/getNoticeList", noticeReq);
        if (noticeRes != null && noticeRes.getCode() == 0) {
            List<Map<String, Object>> notices = (List<Map<String, Object>>) noticeRes.getData();
            noticeComboBox.getItems().addAll(notices);
        }


        // 下拉框显示title字段
        noticeComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.get("title").toString());
                }
            }
        });
        noticeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.get("title").toString());
            }
        });

        grid.add(new Label("标题:"), 0, 0);
        grid.add(titleField, 1, 0);

        grid.add(new Label("描述:"), 0, 1);
        grid.add(descriptionArea, 1, 1);

        grid.add(new Label("开始时间:"), 0, 2);
        grid.add(startDateField, 1, 2);

        grid.add(new Label("结束时间:"), 0, 3);
        grid.add(endDateField, 1, 3);

        grid.add(new Label("通知:"), 0, 4);
        grid.add(noticeComboBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // 添加按钮
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        // 获取 OK 按钮
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String startDateStr = startDateField.getText().trim();
            String endDateStr = endDateField.getText().trim();
            Map<String, Object> selectedNotice = noticeComboBox.getValue();

            if (title.isEmpty()) {
                showAlert("标题不能为空");
                event.consume(); // 阻止对话框关闭
                return;
            }
            if (!startDateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                showAlert("开始时间格式错误，请输入 yyyy-MM-dd 格式");
                event.consume();
                return;
            }
            if (!endDateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                showAlert("结束时间格式错误，请输入 yyyy-MM-dd 格式");
                event.consume();
                return;
            }
            if (selectedNotice == null) {
                showAlert("请选择通知");
                event.consume();
                return;
            }

            // 发送添加请求
            DataRequest req = new DataRequest();
            req.add("title", title);
            req.add("description", description);
            req.add("startTime", startDateStr);
            req.add("endTime", endDateStr);
            req.add("noticeId", selectedNotice.get("noticeId"));

            DataResponse res = HttpRequestUtil.request("/api/activity/addActivity", req);
            if (res != null && res.getCode() == 0) {
                showAlert("添加成功", Alert.AlertType.INFORMATION);
                loadActivityData(); // 重新加载数据
            } else {
                showAlert("添加失败：" + (res == null ? "无响应" : res.getMsg()));
                event.consume(); // 失败时阻止关闭弹窗
            }
        });


        dialog.showAndWait();
    }

    private void showAlert(String msg) {
        showAlert(msg, Alert.AlertType.ERROR);
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }



    @FXML
    private void onEditButtonClicked() {
        Map<String, Object> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("请选择一个活动进行编辑");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("编辑活动");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setText(selected.get("title") == null ? "" : selected.get("title").toString());

        TextArea descriptionArea = new TextArea();
        descriptionArea.setText(selected.get("description") == null ? "" : selected.get("description").toString());

        TextField startDateField = new TextField();
        startDateField.setPromptText("开始时间 (格式 yyyy-MM-dd)");
        startDateField.setText(selected.get("startTime") == null ? "" : selected.get("startTime").toString());

        TextField endDateField = new TextField();
        endDateField.setPromptText("结束时间 (格式 yyyy-MM-dd)");
        endDateField.setText(selected.get("endTime") == null ? "" : selected.get("endTime").toString());

        ComboBox<Map<String, Object>> noticeComboBox = new ComboBox<>();
        noticeComboBox.setPromptText("请选择通知");

        // 加载通知列表
        DataRequest noticeReq = new DataRequest();
        DataResponse noticeRes = HttpRequestUtil.request("/api/notice/getNoticeList", noticeReq);
        if (noticeRes != null && noticeRes.getCode() == 0) {
            List<Map<String, Object>> notices = (List<Map<String, Object>>) noticeRes.getData();
            noticeComboBox.getItems().addAll(notices);

            // 回显对应通知项
            Object currentNoticeId = selected.get("noticeId");
            if (currentNoticeId != null) {
                for (Map<String, Object> notice : notices) {
                    if (currentNoticeId.equals(notice.get("noticeId"))) {
                        noticeComboBox.setValue(notice);
                        break;
                    }
                }
            }
        }

        noticeComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.get("title").toString());
            }
        });
        noticeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.get("title").toString());
            }
        });

        grid.add(new Label("标题:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("描述:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("开始时间:"), 0, 2);
        grid.add(startDateField, 1, 2);
        grid.add(new Label("结束时间:"), 0, 3);
        grid.add(endDateField, 1, 3);
        grid.add(new Label("通知:"), 0, 4);
        grid.add(noticeComboBox, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String title = titleField.getText().trim();
                String description = descriptionArea.getText().trim();
                String startDateStr = startDateField.getText().trim();
                String endDateStr = endDateField.getText().trim();
                Map<String, Object> selectedNotice = noticeComboBox.getValue();

                if (title.isEmpty()) {
                    showAlert("标题不能为空");
                    return null;
                }
                if (!startDateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    showAlert("开始时间格式错误，请输入 yyyy-MM-dd 格式");
                    return null;
                }
                if (!endDateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    showAlert("结束时间格式错误，请输入 yyyy-MM-dd 格式");
                    return null;
                }
                if (selectedNotice == null) {
                    showAlert("请选择通知");
                    return null;
                }

                DataRequest req = new DataRequest();
                req.add("activityId", selected.get("activityId"));
                req.add("title", title);
                req.add("description", description);
                req.add("startTime", startDateStr);
                req.add("endTime", endDateStr);
                req.add("noticeId", selectedNotice.get("noticeId"));

                DataResponse res = HttpRequestUtil.request("/api/activity/updateActivity", req);
                if (res != null && res.getCode() == 0) {
                    showAlert("更新成功", Alert.AlertType.INFORMATION);
                    loadActivityData();
                } else {
                    showAlert("更新失败：" + (res == null ? "无响应" : res.getMsg()));
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    @FXML
    private void onDeleteButtonClicked() {
        Map<String, Object> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("请选择一个活动进行删除");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "确定删除选中的活动吗？", ButtonType.YES, ButtonType.NO);
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                DataRequest req = new DataRequest();
                req.add("activityId", selected.get("activityId"));

                DataResponse res = HttpRequestUtil.request("/api/activity/deleteActivity", req);
                if (res != null && res.getCode() == 0) {
                    showAlert("删除成功", Alert.AlertType.INFORMATION);
                    loadActivityData();
                } else {
                    showAlert("删除失败：" + (res == null ? "无响应" : res.getMsg()));
                }
            }
        });
    }


}
