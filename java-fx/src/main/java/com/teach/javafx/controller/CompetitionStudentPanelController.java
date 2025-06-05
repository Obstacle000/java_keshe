package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Map;

public class CompetitionStudentPanelController {

    @FXML
    private TableView<Map<String, Object>> dataTableView;

    @FXML
    private TableColumn<Map, String> scoreColumn;

    @FXML
    private TableColumn<Map, String> signUpColumn;

    @FXML
    private TableColumn<Map, String> titleColumn;

    private ArrayList<Map<String, Object>> recordList = new ArrayList();
    @FXML
    private void initialize() {
        dataTableView.setEditable(false);

        titleColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("title");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });


        scoreColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("score");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });
        signUpColumn.setCellValueFactory(cellData -> {
            Object val = cellData.getValue().get("signupTime");
            return new ReadOnlyStringWrapper(val == null ? "" : val.toString());
        });

        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("personId", AppStore.getJwt().getId());
        res = HttpRequestUtil.request("/api/competition/getScoreRecord", req); //从后台获取所有学生信息列表集合
        if (res != null && res.getCode() == 0) {
            recordList = (ArrayList<Map<String, Object>>) res.getData();
        }

        if (recordList != null) {
            dataTableView.getItems().setAll(recordList);
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "加载实践列表失败");
            alert.showAndWait();
        }
    }
}
