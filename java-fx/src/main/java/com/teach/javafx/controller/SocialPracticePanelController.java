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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SocialPracticePanelController {

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<Map, String> aspectColumn1;

    @FXML
    private TableView<Map<String, Object>> dataTableView;

    @FXML
    private Button deleteButton;

    @FXML
    private TableColumn<Map, String> descriptionColumn;

    @FXML
    private TableColumn<Map, String> detailColumn;

    @FXML
    private Button editButton;

    @FXML
    private TableColumn<Map, String> endTimeColumn;

    @FXML
    private TableColumn<Map, String> signupColumn;

    @FXML
    private TableColumn<Map, String> signupCountColumn;

    @FXML
    private TableColumn<Map, String> socialPracticeIdColumn;

    @FXML
    private TableColumn<Map, String> startTimeColumn;

    @FXML
    private TableColumn<Map, String> titleColumn;
    private static ArrayList<Map<String, Object>> practiceList = new ArrayList();

    @FXML
    void initialize() {
        // 初始化方法，绑定表格列，添加事件监听等
        dataTableView.setEditable(false);

        String currentUserRole = AppStore.getJwt().getRole();
        if ("ROLE_STUDENT".equals(currentUserRole)) {
            editButton.setVisible(false);
            deleteButton.setVisible(false);
            addButton.setVisible(false);
            aspectColumn1.setVisible(false);
        }
        socialPracticeIdColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("socialPracticeId");
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
            private final Button detailBtn = new Button("详情");

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
            private final Button uploadProofBtn = new Button("上传附件");
            private final HBox signedUpBox = new HBox(5); // 报名后显示的两个按钮
            private final HBox signupBox = new HBox();    // 报名前显示的报名按钮

            {
                // 报名按钮行为
                signupBtn.setOnAction(event -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    handleSignup(row);
                });

                // 取消报名按钮行为
                cancelSignupBtn.setOnAction(event -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    handleCancelSignup(row);
                });

                // 上传附件按钮行为
                uploadProofBtn.setOnAction(event -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    handleUploadProof(row);
                });

                signedUpBox.getChildren().addAll(cancelSignupBtn, uploadProofBtn);
                signupBox.getChildren().add(signupBtn);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    Boolean isSignedUp = (Boolean) row.get("isSignedUp");

                    if (Boolean.TRUE.equals(isSignedUp)) {
                        setGraphic(signedUpBox);
                    } else {
                        setGraphic(signupBox);
                    }
                }
            }
        });

        aspectColumn1.setCellValueFactory(param -> new ReadOnlyStringWrapper("")); // 占位
        aspectColumn1.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("查看");

            {
                viewBtn.setOnAction(event -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    showSignupList(row); // 弹出报名详情
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewBtn);
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

        // 加载活动数据
        loadActivityData();
    }

    private void showSignupList(Map<String, Object> practiceRow) {
        Object socialPracticeIdObj = practiceRow.get("socialPracticeId");
        int socialPracticeId = parseSocialPracticeId(socialPracticeIdObj);
        if (socialPracticeId == -1) return;

        DataRequest req = new DataRequest();
        req.add("socialPracticeId", socialPracticeId);

        DataResponse res = HttpRequestUtil.request("/api/socialPracticeSignup/getSignupList", req);
        if (res == null || res.getCode() != 0) {
            showAlert("获取报名信息失败", Alert.AlertType.ERROR);
            return;
        }

        List<Map<String, Object>> signupList = (List<Map<String, Object>>) res.getData();

        // 创建表格
        TableView<Map<String, Object>> tableView = new TableView<>();
        tableView.setPrefWidth(500);
        tableView.setPrefHeight(300);

        // 学号列
        TableColumn<Map<String, Object>, String> numCol = new TableColumn<>("学号");
        numCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                String.valueOf(cell.getValue().getOrDefault("num", ""))
        ));

        // 姓名列
        TableColumn<Map<String, Object>, String> nameCol = new TableColumn<>("姓名");
        nameCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                String.valueOf(cell.getValue().getOrDefault("name", ""))
        ));

        // 报名时间列
        TableColumn<Map<String, Object>, String> timeCol = new TableColumn<>("报名时间");
        timeCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                String.valueOf(cell.getValue().getOrDefault("signupTime", ""))
        ));

        // 附件查看列
        TableColumn<Map<String, Object>, String> photoCol = new TableColumn<>("附件");
        photoCol.setCellValueFactory(param -> new ReadOnlyStringWrapper(""));
        photoCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("查看");

            {
                viewBtn.setOnAction(event -> {
                    Map<String, Object> row = getTableView().getItems().get(getIndex());
                    row.put("socialPracticeId", socialPracticeId); // 传给 displayPhoto 方法
                    displayPhoto(row);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewBtn);
                }
            }
        });

        tableView.getColumns().addAll(numCol, nameCol, timeCol, photoCol);
        tableView.getItems().addAll(signupList);

        // 弹窗窗口
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("报名学生列表");
        VBox root = new VBox(10, tableView);
        root.setPadding(new Insets(10));
        popup.setScene(new Scene(root));
        popup.showAndWait();
    }


    private void loadActivityData() {
        DataRequest req = new DataRequest();
        req.add("personId", AppStore.getJwt().getId());

        DataResponse res = HttpRequestUtil.request("/api/socialPractice/getSocialPracticeList", req);
        if (res != null && res.getCode() == 0) {
            practiceList = (ArrayList<Map<String, Object>>) res.getData();
            if (practiceList != null) {
                dataTableView.getItems().setAll(practiceList);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "加载实践列表失败");
            alert.showAndWait();
        }
    }


    private void showDetail(Map<String, Object> socialPractice) {
        // 从表格拿到noticeId
        Object socialPracticeId = socialPractice.get("socialPracticeId");
        if (socialPracticeId == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "通知ID为空，无法查看详情");
            alert.showAndWait();
            return;
        }

        DataRequest req = new DataRequest();
        req.add("socialPracticeId", socialPracticeId);

        // 调用后端接口，获取通知详情
        DataResponse res = HttpRequestUtil.request("/api/socialPractice/getSocialPracticeContent", req);

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
        Object socialPracticeIdObj = row.get("socialPracticeId");
        int socialPracticeId = parseSocialPracticeId(socialPracticeIdObj);
        if (socialPracticeId == -1) return;

        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("socialPracticeId", socialPracticeId);

        DataResponse res = HttpRequestUtil.request("/api/socialPracticeSignup/signUp", req);
        if (res != null && res.getCode() == 0) {
            showAlert("报名成功", Alert.AlertType.INFORMATION);
            row.put("isSignedUp", true);
            dataTableView.refresh();
            loadActivityData();
        } else {
            showAlert("报名失败：" + (res == null ? "无响应" : res.getMsg()), Alert.AlertType.ERROR);
        }
    }

    private void handleUploadProof(Map<String, Object> row) {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("图片上传");
        Object socialPracticeIdObj = row.get("socialPracticeId");
        int socialPracticeId = parseSocialPracticeId(socialPracticeIdObj);
        if (socialPracticeId == -1) return;
//        fileDialog.setInitialDirectory(new File("C:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG 文件", "*.jpg"));
        File file = fileDialog.showOpenDialog(null);
        if(file == null)
            return;
        DataResponse res =HttpRequestUtil.uploadFile("/api/base/uploadPhoto",file.getPath(),"verifyPhoto/" + socialPracticeId + ".jpg"
                ,null);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
            // displayPhoto(row);
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    public void displayPhoto(Map<String, Object> row) {
        DataRequest req = new DataRequest();
        Object socialPracticeIdObj = row.get("socialPracticeId");
        int socialPracticeId = parseSocialPracticeId(socialPracticeIdObj);
        if (socialPracticeId == -1) return;

        req.add("fileName", "verifyPhoto/" + socialPracticeId + ".jpg");  // 个人照片路径
        byte[] bytes = HttpRequestUtil.requestByteData("/api/base/getFileByteData", req);
        if (bytes != null) {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            Image img = new Image(in);

            // 创建ImageView显示图片
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(400);
            imageView.setPreserveRatio(true);

            // 弹窗显示
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("查看图片");

            VBox root = new VBox(imageView);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(10));
            Scene scene = new Scene(root, 420, 420);
            popup.setScene(scene);
            popup.showAndWait();
        } else {
            showAlert("图片不存在或加载失败", Alert.AlertType.WARNING);
        }
    }




    private void showAlert(String msg) {
        showAlert(msg, Alert.AlertType.ERROR);
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void handleCancelSignup(Map<String, Object> row) {
        Integer personId = AppStore.getJwt().getId();
        Object socialPracticeIdObj = row.get("socialPracticeId");
        int socialPracticeId = parseSocialPracticeId(socialPracticeIdObj);
        if (socialPracticeId == -1) return;

        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("socialPracticeId", socialPracticeId);

        DataResponse res = HttpRequestUtil.request("/api/socialPracticeSignup/cancelSignUp", req);
        if (res != null && res.getCode() == 0) {
            showAlert("取消报名成功", Alert.AlertType.INFORMATION);
            row.put("isSignedUp", false);
            dataTableView.refresh();
            loadActivityData();
        } else {
            showAlert("取消报名失败：" + (res == null ? "无响应" : res.getMsg()), Alert.AlertType.ERROR);
        }
    }

    private int parseSocialPracticeId(Object socialPracticeIdObj) {
        if (socialPracticeIdObj instanceof Number) {
            return ((Number) socialPracticeIdObj).intValue();
        }
        try {
            return Integer.parseInt(socialPracticeIdObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("实践ID格式错误", Alert.AlertType.ERROR);
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
        DataResponse noticeRes = HttpRequestUtil.request("/api/socialPractice/getNoticeList", noticeReq);
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

            DataResponse res = HttpRequestUtil.request("/api/socialPractice/addSocialPractice", req);
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


    @FXML
    private void onEditButtonClicked() {
        Map<String, Object> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("请选择一个实践活动进行编辑");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("编辑实践活动");

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
        DataResponse noticeRes = HttpRequestUtil.request("/api/socialPractice/getNoticeList", noticeReq);
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
                req.add("socialPracticeId", selected.get("socialPracticeId"));
                req.add("title", title);
                req.add("description", description);
                req.add("startTime", startDateStr);
                req.add("endTime", endDateStr);
                req.add("noticeId", selectedNotice.get("noticeId"));

                DataResponse res = HttpRequestUtil.request("/api/socialPractice/updateSocialPractice", req);
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
            showAlert("请选择一个实践活动进行删除");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "确定删除选中的实践活动吗？", ButtonType.YES, ButtonType.NO);
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                DataRequest req = new DataRequest();
                req.add("socialPracticeId", selected.get("socialPracticeId"));

                DataResponse res = HttpRequestUtil.request("/api/socialPractice/deleteSocialPractice", req);
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
