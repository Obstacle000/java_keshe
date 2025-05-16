package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NoticeManagementController {

    @FXML
    private TableView<Map<String, Object>> dataTableView;

    @FXML
    private TableColumn<Map, String> idColumn;

    @FXML
    private TableColumn<Map, String> titleColumn;

    // 不在表格显示，注释掉contentColumn
    // @FXML
    // private TableColumn<Map, String> contentColumn;

    @FXML
    private TableColumn<Map, String> createTimeColumn;

    @FXML
    private TableColumn<Map, String> detailColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    private List<Map<String, Object>> noticeList;

    @FXML
    public void initialize() {
        dataTableView.setEditable(false);

        String currentUserRole = AppStore.getJwt().getRole();
        if ("ROLE_STUDENT".equals(currentUserRole)) {
            updateButton.setVisible(false);
            deleteButton.setVisible(false);
            addButton.setVisible(false);
        }

        idColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("noticeId");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });

        titleColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("title");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });


        createTimeColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("createTime");
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

        // 表格选中监听，控制更新和删除按钮状态
        dataTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            updateButton.setDisable(!selected);
            deleteButton.setDisable(!selected);
        });
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        // 加载通知数据
        loadNoticeData();
    }

    private void loadNoticeData() {
        DataRequest req = new DataRequest();
        // 如需传用户id，可添加参数
        DataResponse res = HttpRequestUtil.request("/api/notice/getNoticeList", req);
        if (res != null && res.getCode() == 0) {
            noticeList = (List<Map<String, Object>>) res.getData();
            if (noticeList != null) {
                dataTableView.getItems().setAll(noticeList);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "加载通知列表失败");
            alert.showAndWait();
        }
    }

    private void showDetail(Map<String, Object> notice) {
        // 从表格拿到noticeId
        Object noticeId = notice.get("noticeId");
        if (noticeId == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "通知ID为空，无法查看详情");
            alert.showAndWait();
            return;
        }

        DataRequest req = new DataRequest();
        req.add("noticeId", noticeId);

        // 调用后端接口，获取通知详情
        DataResponse res = HttpRequestUtil.request("/api/notice/getNoticeContent", req);

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


    @FXML
    private void onAddButtonClick(ActionEvent event) {
        Map<String, String> result = showEditDialog("", "");
        if (result == null) return;

        DataRequest req = new DataRequest();
        req.add("title", result.get("title"));
        req.add("content", result.get("content"));

        DataResponse res = HttpRequestUtil.request("/api/notice/addNotice", req);
        if (res != null && res.getCode() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "通知添加成功");
            alert.showAndWait();
            loadNoticeData(); // 刷新表格
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "通知添加失败");
            alert.showAndWait();
        }
    }


    @FXML
    private void onUpdateButtonClick(ActionEvent event) {
        Map<String, Object> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String currentTitle = selected.get("title") == null ? "" : selected.get("title").toString();
        String currentContent = selected.get("content") == null ? "" : selected.get("content").toString();

        Map<String, String> result = showEditDialog(currentTitle, currentContent);
        if (result == null) return;

        DataRequest req = new DataRequest();
        req.add("noticeId", selected.get("noticeId"));
        req.add("title", result.get("title"));
        req.add("content", result.get("content"));

        DataResponse res = HttpRequestUtil.request("/api/notice/updateNotice", req);
        if (res != null && res.getCode() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "通知更新成功");
            alert.showAndWait();
            loadNoticeData(); // 刷新表格
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "通知更新失败");
            alert.showAndWait();
        }
    }

    // 弹出添加/编辑对话框，返回 Map<String, String> 含 title 和 content，取消返回 null
    private Map<String, String> showEditDialog(String defaultTitle, String defaultContent) {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("通知编辑");

        // 按钮
        ButtonType okButtonType = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // 输入字段
        TextField titleField = new TextField(defaultTitle);
        titleField.setPromptText("标题");

        TextArea contentArea = new TextArea(defaultContent);
        contentArea.setPromptText("内容");
        contentArea.setPrefRowCount(8);

        // 布局
        VBox vbox = new VBox(10, new Label("标题："), titleField, new Label("内容："), contentArea);
        dialog.getDialogPane().setContent(vbox);

        // 转换结果
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                Map<String, String> result = new HashMap<>();
                result.put("title", titleField.getText());
                result.put("content", contentArea.getText());
                return result;
            }
            return null;
        });

        Optional<Map<String, String>> result = dialog.showAndWait();
        return result.orElse(null);
    }


    @FXML
    private void onDeleteButtonClick(ActionEvent event) {
        Map<String, Object> selected = dataTableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("删除确认");
        confirm.setHeaderText("确定删除该通知吗？");
        confirm.setContentText("标题：" + selected.get("title"));
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DataRequest req = new DataRequest();
            req.add("noticeId", selected.get("noticeId"));

            DataResponse res = HttpRequestUtil.request("/api/notice/deleteNotice", req);
            if (res != null && res.getCode() == 0) {
                dataTableView.getItems().remove(selected);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "删除成功");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "删除失败");
                alert.showAndWait();
            }
        }
    }
}
