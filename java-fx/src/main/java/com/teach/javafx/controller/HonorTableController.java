package com.teach.javafx.controller;

import com.teach.javafx.request.OptionItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HonorTableController {
    @FXML
    private TableColumn<Map,String> classNameColumn;

    @FXML
    private TableView<Map> dataTableView;

    @FXML
    private TableColumn<Map, Button> editColumn;

    @FXML
    private TableColumn<Map,String> honorColumn;



    @FXML
    private TableColumn<Map,String> nameColumn;

    @FXML
    private TableColumn<Map,String> numColumn;

    private ArrayList<Map> honorList = new ArrayList();  // 学生信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    private ComboBox<OptionItem> nameComboBox;

    private List<OptionItem> personList;

    private HonorEditController honorEditController = null;

    private Stage stage = null;

    public List<OptionItem> getPersonList() {
        return personList;
    }



    @FXML
    void onAddButtonClick(ActionEvent event) {

    }

    @FXML
    void onDeleteButtonClick(ActionEvent event) {

    }

    @FXML
    void onEditButtonClick(ActionEvent event) {

    }

    @FXML
    void onQueryButtonClick(ActionEvent event) {

    }
}
