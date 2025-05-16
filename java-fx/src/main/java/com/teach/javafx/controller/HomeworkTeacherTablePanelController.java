package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class HomeworkTeacherTablePanelController {

    @FXML
    private TableColumn<Map,String> courseColumn;

    @FXML
    private TableView<Map<String, Object>> dataTableView;

    @FXML
    private TableColumn<Map,String> editColumn;

    @FXML
    private TableColumn<Map,String> numColumn;

    @FXML
    private TableColumn<Map,String> teacherColumn;

    @FXML
    private ComboBox<OptionItem> courseComboBox;

    @FXML
    private TableColumn<Map,String> titleColumn;
    private List<Map<String, Object>> homeworkList; // 对应studentList

    @FXML
    public void initialize() {
        dataTableView.setEditable(true);
        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 使用lambda替代MapValueFactory，防止类型不匹配问题
        numColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("num");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });
        courseColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("course");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });
        teacherColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("teacher");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });
        titleColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("title");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });

        numColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        courseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        teacherColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // 编辑提交事件，更新 Map 数据
        numColumn.setOnEditCommit(event -> {
            Map<String, Object> row = event.getRowValue();
            row.put("num", event.getNewValue());
        });

        courseColumn.setOnEditCommit(event -> {
            Map<String, Object> row = event.getRowValue();
            row.put("course", event.getNewValue());
        });

        teacherColumn.setOnEditCommit(event -> {
            Map<String, Object> row = event.getRowValue();
            row.put("teacher", event.getNewValue());
        });

        titleColumn.setOnEditCommit(event -> {
            Map<String, Object> row = event.getRowValue();
            row.put("title", event.getNewValue());
        });

        // 设置按钮列（查看按钮）
        editColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(""));

        editColumn.setCellFactory(column -> new TableCell<>() {
            private final Button viewButton = new Button("查看");

            {
                viewButton.setOnAction(event -> {
                    Map<String, Object> rowData = getTableView().getItems().get(getIndex());
                    Object definitionId = rowData.get("num");
                    if (definitionId == null) {
                        MessageDialog.showDialog("无法获取作业编号！");
                        return;
                    }

                    DataRequest req = new DataRequest();
                    req.add("definitionId", definitionId);
                    DataResponse res = HttpRequestUtil.request("/api/homeworkSubmission/getHomeworkSubmissions", req);
                    if (res != null && res.getCode() == 0) {
                        List<Map<String, Object>> submissions = (List<Map<String, Object>>) res.getData();
                        showSubmissionDialog(submissions);
                    } else {
                        MessageDialog.showDialog("获取失败：" + (res != null ? res.getMsg() : "服务器无响应"));
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });

        // 加载数据请求
        DataRequest req = new DataRequest();
        req.add("personId", AppStore.getJwt().getId());
        DataResponse res = HttpRequestUtil.request("/api/homeworkDefinition/getHomeworkList", req);
        if (res != null && res.getCode() == 0) {
            homeworkList = (List<Map<String, Object>>) res.getData();
            if (homeworkList != null) {
                dataTableView.getItems().setAll(homeworkList);
            }
        }
    }
    private void showSubmissionDialog(List<Map<String, Object>> submissions) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("未提交作业学生名单");

        TableView<Map<String, Object>> tableView = new TableView<>();

        // 学号列
        TableColumn<Map<String, Object>, String> numColumn = new TableColumn<>("学号");
        numColumn.setCellValueFactory(cellData -> {
            Object num = cellData.getValue().get("num");
            return new ReadOnlyStringWrapper(num == null ? "" : num.toString());
        });
        numColumn.setPrefWidth(100);

        // 学生姓名列
        TableColumn<Map<String, Object>, String> nameColumn = new TableColumn<>("学生姓名");
        nameColumn.setCellValueFactory(cellData -> {
            Object name = cellData.getValue().get("studentName");
            return new ReadOnlyStringWrapper(name == null ? "" : name.toString());
        });
        nameColumn.setPrefWidth(150);

        // 提交状态列（boolean 转文字）
        TableColumn<Map<String, Object>, String> statusColumn = new TableColumn<>("提交状态");
        statusColumn.setCellValueFactory(cellData -> {
            Object statusObj = cellData.getValue().get("status");
            String text = "未知";
            if (statusObj instanceof Boolean) {
                text = ((Boolean) statusObj) ? "已提交" : "未提交";
            }
            return new ReadOnlyStringWrapper(text);
        });
        statusColumn.setPrefWidth(100);

        tableView.getColumns().addAll(numColumn, nameColumn, statusColumn);
        tableView.getItems().setAll(submissions);

        tableView.setPrefHeight(300);
        tableView.setPrefWidth(400);

        VBox content = new VBox(10);
        content.getChildren().add(tableView);
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }


    @FXML
    void onSaveButtonClick(ActionEvent event) {
        List<Map<String, Object>> items = dataTableView.getItems();

        for (Map<String, Object> item : items) {
            DataRequest req = new DataRequest();

            // 将表格中的字段一个个加进去
            for (Map.Entry<String, Object> entry : item.entrySet()) {
                req.add(entry.getKey(), entry.getValue());
            }


            req.add("personId", AppStore.getJwt().getId());

            DataResponse res = HttpRequestUtil.request("/api/homeworkDefinition/homeworkSave", req);
            if (res == null || res.getCode() != 0) {
                MessageDialog.showDialog("保存失败：" + (res != null ? res.getMsg() : "服务器无响应"));
                return;
            }
        }

        MessageDialog.showDialog("所有修改已保存！");
        initialize(); // 重新加载数据
    }


    @FXML
    void onAddButtonClick(ActionEvent event) {
        // 创建弹窗
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("添加新作业");

        // 设置按钮
        ButtonType okButtonType = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // 创建输入字段
        TextField courseField = new TextField();
        courseField.setPromptText("课程名");
        TextField titleField = new TextField();
        titleField.setPromptText("作业标题");

        // 使用 VBox 布局
        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("课程名:"), courseField,
                new Label("作业标题:"), titleField
        );
        dialog.getDialogPane().setContent(content);

        // 结果处理
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return Map.of(
                        "num", String.valueOf(dataTableView.getItems().size() + 1),
                        "course", courseField.getText(),
                        "personId", AppStore.getJwt().getId(),
                        "title", titleField.getText()
                );
            }
            return null;
        });
        // 等待后关闭
        dialog.showAndWait().ifPresent(result -> {
            // 构造请求数据
            DataRequest req = new DataRequest();

            for (Map.Entry<String, Object> entry : result.entrySet()) {
                req.add(entry.getKey(), entry.getValue());
            }

            // 发送请求到后端添加作业信息
            DataResponse res = HttpRequestUtil.request("/api/homeworkDefinition/homeworkAdd", req);

            if (res != null && res.getCode() == 0) {
                MessageDialog.showDialog("添加成功");
                // 后端成功后再添加到表格中显示
                initialize();
            } else {
                MessageDialog.showDialog("添加失败：" + (res != null ? res.getMsg() : "未知错误"));
            }
        });

    }


    @FXML
    void onDeleteButtonClick(ActionEvent event) {
        Map<String, Object> selectedItem = dataTableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            MessageDialog.showDialog("请先选择要删除的作业！");
            return;
        }

        Object numObj = selectedItem.get("num");
        if (numObj == null) {
            MessageDialog.showDialog("该记录没有编号，无法删除！");
            return;
        }

        DataRequest req = new DataRequest();
        try {
            req.add("num", Integer.parseInt(numObj.toString()));
        } catch (NumberFormatException e) {
            MessageDialog.showDialog("编号格式错误！");
            return;
        }

        DataResponse res = HttpRequestUtil.request("/api/homeworkDefinition/homeworkDelete", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            dataTableView.getItems().remove(selectedItem); // 从表格中移除该行
        } else {
            MessageDialog.showDialog("删除失败：" + (res != null ? res.getMsg() : "服务器无响应"));
        }
    }


    @FXML
    void onQueryButtonClick(ActionEvent event) {

    }



}
