package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeavePanelController {

    @FXML
    private Button applyButton;

    @FXML
    private TableColumn<Map,String> applyTimeColumn;

    @FXML
    private  TableView<Map> dataTableView;

    @FXML
    private TableColumn<Map,String> endTimeColumn;

    @FXML
    private TableColumn<Map,String> leaveIdColumn;

    @FXML
    private TableColumn<Map,String> numColumn;

    @FXML
    private TableColumn<Map,String> operateColumn;

    @FXML
    private TableColumn<Map,String> startTimeColumn;

    @FXML
    private TableColumn<Map,String> statusColumn;

    @FXML
    private TableColumn<Map,String> studentNameColumn;

    @FXML
    private TableColumn<Map,String> teacherNameColumn;

    private static ArrayList<Map> leaveList = new ArrayList();
    private List<OptionItem> teccherList;

    @FXML
    void initialize() {
        boolean isTeacher = AppStore.getJwt().getRole().equals("ROLE_TEACHER");
        applyButton.setVisible(!isTeacher);

        leaveIdColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue().get("leaveId");
            String strValue = "";
            if (value instanceof Number) {
                strValue = String.valueOf(((Number) value).intValue());
            } else if (value != null) {
                try {
                    double d = Double.parseDouble(value.toString());
                    strValue = String.valueOf((int) d);
                } catch (NumberFormatException e) {
                    strValue = value.toString(); // fallback
                }
            }
            return new SimpleStringProperty(strValue);
        });
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("student"));
        teacherNameColumn.setCellValueFactory(new MapValueFactory<>("teacher"));
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));  //设置列值工程属性
        applyTimeColumn.setCellValueFactory(new MapValueFactory<>("applyTime"));
        endTimeColumn.setCellValueFactory(new MapValueFactory<>("endDate")); // 可以没有

        startTimeColumn.setCellValueFactory(new MapValueFactory<>("startDate"));


        statusColumn.setCellValueFactory(cellData -> {
            Object statusObj = cellData.getValue().get("status");
            String statusText;
            if (statusObj == null) {
                statusText = "";
            } else {
                String val = statusObj.toString();
                switch (val) {
                    case "0":
                    case "0.0":
                        statusText = "待处理"; break;
                    case "1":
                    case "1.0":
                        statusText = "审批已通过"; break;
                    case "2":
                    case "2.0":
                        statusText = "审批未通过"; break;
                    case "3":
                    case "3.0":
                        statusText = "已申请销假"; break;
                    case "4":
                    case "4.0":
                        statusText = "销假已成功"; break;
                    default:
                        statusText = "未知状态"; break;
                }
            }
            return new javafx.beans.property.SimpleStringProperty(statusText);
        });


        operateColumn.setCellFactory(col -> {
            return new TableCell<>() {
                private final Button moreButton = new Button("更多");

                {
                    moreButton.setOnAction(e -> {
                        Map<String, Object> item = getTableView().getItems().get(getIndex());
                        showEditDialog(item);  // 弹出窗口
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(moreButton);
                    }
                }
            };
        });


        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("personId", AppStore.getJwt().getId());
        res = HttpRequestUtil.request("/api/leave/getApplyList",req);
        if(res != null && res.getCode()== 0) {
            leaveList = (ArrayList<Map>)res.getData();
        }
        setTableViewData();
    }
    private  void setTableViewData() {
        dataTableView.getItems().clear();
        dataTableView.getItems().addAll(leaveList);
    }


    private void showEditDialog(Map<String, Object> item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/leave-edit-dialog.fxml"));
            Parent root = loader.load();

            LeaveEditDialogController controller = loader.getController();
            controller.setData(item);  // 传入数据

            Stage stage = new Stage();
            stage.setTitle("请假详情编辑");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // 关闭后刷新表格
            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  void refreshTable() {
        DataRequest req = new DataRequest();
        req.add("personId", AppStore.getJwt().getId());
        DataResponse res = HttpRequestUtil.request("/api/leave/getApplyList", req);
        if (res != null && res.getCode() == 0) {
            leaveList = (ArrayList<Map>) res.getData();
            setTableViewData();
        }
    }



    @FXML
    void onApplyButtonClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/leave-apply-dialog.fxml"));
            Parent root = loader.load();

            // 取控制器（可以用来给弹窗传参，或刷新操作）
            LeaveApplyDialogController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("请假申请");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // 弹窗关闭后刷新列表
            refreshTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
