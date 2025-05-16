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
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HomeworkStudentTablePanelController {

    @FXML
    private TableColumn<Map,String> courseColumn;

    @FXML
    private ComboBox<OptionItem> courseComboBox;

    @FXML
    private TableView<Map<String, Object>> dataTableView;

    @FXML
    private TableColumn<Map,String> editColumn;

    @FXML
    private TableColumn<Map,String> numColumn;

    @FXML
    private TableColumn<Map,String> statusColumn;

    @FXML
    private TableColumn<Map,String> teacherColumn;

    @FXML
    private TableColumn<Map,String> titleColumn;
    private List<Map<String, Object>> homeworkList; // 对应studentList

    @FXML
    public void initialize() {
        dataTableView.setEditable(true);
        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 设置列值工厂（显示数据）
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
        statusColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("status");
            String statusStr;
            if (val instanceof Boolean) {
                statusStr = (Boolean) val ? "已完成" : "未完成";
            } else if (val instanceof String) {
                statusStr = "true".equalsIgnoreCase(val.toString()) ? "已完成" : "未完成";
            } else {
                statusStr = "未完成";
            }
            return new ReadOnlyStringWrapper(statusStr);
        });

        // 设置列为可编辑
        numColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        courseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        teacherColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // 编辑后更新 Map
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
        statusColumn.setOnEditCommit(event -> {
            Map<String, Object> row = event.getRowValue();
            row.put("status", event.getNewValue());
        });

        // 操作列：提交按钮
        editColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(""));
        editColumn.setCellFactory(column -> new TableCell<>() {
            private final Button submitButton = new Button("提交");

            {
                submitButton.setOnAction(event -> {
                    int index = getIndex();
                    Map<String, Object> row = dataTableView.getItems().get(index);

                    // 弹出文本输入框
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("作业提交");
                    dialog.setHeaderText("请输入你的答案：");
                    dialog.setContentText("答案：");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(answer -> {
                        // 构造请求
                        DataRequest req = new DataRequest();
                        req.add("personId", AppStore.getJwt().getId());
                        req.add("definitionId", Integer.parseInt(row.get("num").toString()));
                        req.add("answer", answer);

                        // 发请求
                        DataResponse res = HttpRequestUtil.request("/api/homeworkSubmission/submitHomeworkCompletion", req);

                        if (res != null && res.getCode() == 0) {
                            // 提交成功，更新行状态
                            row.put("status", true);
                            dataTableView.refresh();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "提交成功！");
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "提交失败，请重试！");
                            alert.showAndWait();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(submitButton);
                }
            }
        });


        // 加载作业数据
        DataRequest req = new DataRequest();
        req.add("personId", AppStore.getJwt().getId());
        DataResponse res = HttpRequestUtil.request("/api/homeworkDefinition/getHomeworkListStudent", req);
        if (res != null && res.getCode() == 0) {
            homeworkList = (List<Map<String, Object>>) res.getData();
            if (homeworkList != null) {
                dataTableView.getItems().setAll(homeworkList);
            }
        }
    }



    @FXML
    void onQueryButtonClick(ActionEvent event) {

    }

}
