package com.teach.javafx.controller;

import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * HonorEditControllerController 成绩管理类 对应 honor-edit-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class HonorEditController {

    @FXML
    private TextField honorField;
    private List<OptionItem> personList;
    @FXML
    private ComboBox<OptionItem> personComboBox;
    private HonorTableController honorTableController= null;
    private Integer honorId= null;




    @FXML
    public void cancelButtonClick(){
        honorTableController.doClose("cancel",null);
    }
    @FXML
    public void okButtonClick(){
        // 修改逻辑,在这里修改成绩
        Map<String,Object> data = new HashMap<>();
        OptionItem op;
        op = personComboBox.getSelectionModel().getSelectedItem();
        if(op != null) {
            data.put("personId",Integer.parseInt(op.getValue()));
        }

        data.put("honorId",honorId);
        data.put("honor",honorField.getText());
        honorTableController.doClose("ok",data);
    }
    public void setHonorTableController(HonorTableController honourTableController) {
        this.honorTableController = honourTableController;
    }
    //准备工作,往honorEditController的成员里放数据
    public void init(){
        personList = honorTableController.getPersonList();
        personComboBox.getItems().addAll(personList);

    }
    public void showDialog(Map data){
        if(data == null) {
            honorId = null;
            personComboBox.getSelectionModel().select(-1);

            personComboBox.setDisable(false);

            honorField.setText("");
        }else {
            honorId = CommonMethod.getInteger(data,"honorId");
            personComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(personList, CommonMethod.getString(data, "personId")));
            personComboBox.setDisable(true);
            honorField.setText(CommonMethod.getString(data, "honor"));
        }
    }

}
