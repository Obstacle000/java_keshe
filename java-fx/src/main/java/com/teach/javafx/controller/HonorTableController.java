package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HonorTableController {
    @FXML
    private TableColumn<Map,String> classNameColumn;

    @FXML
    private TableView<Map> dataTableView;

    @FXML
    private TableColumn<Map,String> editColumn;

    @FXML
    private TableColumn<Map,String> honorColumn;

    @FXML
    private ComboBox<OptionItem> personComboBox;

    @FXML
    private TableColumn<Map,String> nameColumn;

    @FXML
    private TableColumn<Map,String> numColumn;

    private ArrayList<Map> honorList = new ArrayList();  // 对应scoreList
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表




    private List<OptionItem> personList; // 对应studentList

    private HonorEditController honorEditController = null;

    private Stage stage = null;

    public List<OptionItem> getPersonList() {
        return personList;
    }



    @FXML
    public void initialize() {


        numColumn.setCellValueFactory(new MapValueFactory<>("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className")); // 可以没有

        honorColumn.setCellValueFactory(new MapValueFactory<>("honor"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));

        DataRequest req =new DataRequest();
        personList = HttpRequestUtil.requestOptionItemList("/api/honor/getPersonItemOptionList",req); //从后台获取所有学生信息列表集合

        OptionItem item = new OptionItem(null,"0","请选择");
        // 后端返回personid的Integer和String和返回格式
        personComboBox.getItems().addAll(item);
        personComboBox.getItems().addAll(personList);


        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        onQueryButtonClick();
    }

    @FXML
    void onQueryButtonClick() {
        Integer personId = 0;

        OptionItem op;

        op = personComboBox.getSelectionModel().getSelectedItem();
        if(op != null)
            personId = Integer.parseInt(op.getValue());

        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("personId",personId);
        res = HttpRequestUtil.request("/api/honor/getHonorList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            honorList = (ArrayList<Map>)res.getData();
        }
        setTableViewData();
    }
    private void setTableViewData() {
        observableList.clear();
        Map map;
        Button editButton;
        for (int j = 0; j < honorList.size(); j++) {
            map = honorList.get(j);
            // 给每一行添加按钮并绑定编辑事件
            editButton = new Button("编辑");
            editButton.setId("edit"+j); // 设置id,以便后续用getSource拿到当前行数
            editButton.setOnAction(e->{
                // 弹出窗口方法
                editItem(((Button)e.getSource()).getId());
            });
            map.put("edit",editButton);
            // 展示这一行
            observableList.addAll(FXCollections.observableArrayList(map));
        }
        // 表格里放数据
        dataTableView.setItems(observableList);
    }
    public void editItem(String name){
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(4,name.length())); // 当前行数
        Map data = honorList.get(j);

        initDialog(); // 展示编辑框
        honorEditController.showDialog(data); // 数据回显,根据添加或者修改按钮而不同,具体修改逻辑在scoreEditController里面做
        MainApplication.setCanClose(false);
        stage.showAndWait();

    }

    private void initDialog() {
        if(stage!= null)
            return;
        FXMLLoader fxmlLoader ;
        Scene scene = null;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("honor-edit-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 260, 140);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.NONE);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("荣誉录入对话框！");
            stage.setOnCloseRequest(event ->{
                MainApplication.setCanClose(true);
            });

            honorEditController = (HonorEditController) fxmlLoader.getController();
            honorEditController.setHonorTableController(this);
            honorEditController.init(); //准备工作,往scoreEditController的成员里放数据

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        stage.close();
        if(!"ok".equals(cmd))
            return;
        DataResponse res;
        Integer personId = CommonMethod.getInteger(data,"personId");
        if(personId == null) {
            MessageDialog.showDialog("没有选中人员不能添加保存！");
            return;
        }

        DataRequest req =new DataRequest();
        req.add("personId",personId);
        req.add("honorId",CommonMethod.getInteger(data,"honorId"));
        req.add("honor",CommonMethod.getInteger(data,"honor"));
        res = HttpRequestUtil.request("/api/honor/honorSave",req); //保存更新信息
        if(res != null && res.getCode()== 0) {
            onQueryButtonClick();//显示
        }
    }



    @FXML
    void onAddButtonClick(ActionEvent event) {
        initDialog();
        honorEditController.showDialog(null);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    @FXML
    void onDeleteButtonClick(ActionEvent event) {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer honorId = CommonMethod.getInteger(form,"honorId");
        DataRequest req = new DataRequest();
        req.add("honorId", honorId);
        DataResponse res = HttpRequestUtil.request("/api/honor/honorDelete",req);
        if(res.getCode() == 0) {
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    void onEditButtonClick(ActionEvent event) {
        Map data = dataTableView.getSelectionModel().getSelectedItem();
        if(data == null) {
            MessageDialog.showDialog("没有选中，不能修改！");
            return;
        }
        initDialog();
        honorEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }
}
