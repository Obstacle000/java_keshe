package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CourseController 登录交互控制类 对应 course-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class CourseController {
    @FXML
    private TableView<Map<String, Object>> dataTableView;
    @FXML
    private TableColumn<Map,String> numColumn;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> creditColumn;
    @FXML
    private TableColumn<Map,String> preCourseColumn;
    @FXML
    private TableColumn<Map,FlowPane> operateColumn;

    private List<Map<String,Object>> courseList = new ArrayList<>();  // 学生信息列表数据
    private final ObservableList<Map<String,Object>> observableList= FXCollections.observableArrayList();  // TableView渲染列表


   // 获取所有学生数据,初始化时调用
    private void onQueryButtonClick(){
        DataResponse res;
        DataRequest req =new DataRequest();
        res = HttpRequestUtil.request("/api/course/getCourseList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            courseList = (List<Map<String, Object>>) res.getData();
        }

        setTableViewData();
    }

    // 渲染表格 map:一行 list:竖直的空间
    private void setTableViewData() {
       observableList.clear();
       Map<String,Object> map;
        FlowPane flowPane;
        Button saveButton,deleteButton;
            for (int j = 0; j < courseList.size(); j++) {
                // 一个map里有 很多的 "字段" - "值"
                map = courseList.get(j);
                flowPane = new FlowPane();
                flowPane.setHgap(10);
                flowPane.setAlignment(Pos.CENTER);
                saveButton = new Button("修改保存");
                saveButton.setId("save"+j);
                saveButton.setOnAction(e->{
                    saveItem(((Button)e.getSource()).getId());
                });
                deleteButton = new Button("删除");
                deleteButton.setId("delete"+j); // 设置id,下面删除逻辑按照索引获取对应的表格第几行
                deleteButton.setOnAction(e->{
                    deleteItem(((Button)e.getSource()).getId());
                });
                flowPane.getChildren().addAll(saveButton,deleteButton);
                map.put("operate",flowPane); // 每一行放上按钮


                observableList.add(map);



            }
            dataTableView.setItems(observableList);
    }
    public void saveItem(String name) {
        if (name == null) return;

        int j = Integer.parseInt(name.substring(4));
        Map<String, Object> data = observableList.get(j);

        // 只做 null 检查
        Integer courseId = data.get("courseId") != null ? Integer.parseInt(data.get("courseId").toString()) : null;
        Integer credit = data.get("credit") != null ? Integer.parseInt(data.get("credit").toString()) : null;
        String preCourse = data.get("preCourse") != null ? data.get("preCourse").toString() : null;
        String num = data.get("num") != null ? data.get("num").toString() : "";
        String courseName = data.get("name") != null ? data.get("name").toString() : "";
        String coursePath = data.get("coursePath") != null ? data.get("coursePath").toString() : "";

        DataRequest req = new DataRequest();
        if (courseId != null) req.add("courseId", courseId);
        if (credit != null) req.add("credit", credit);
        if (preCourse != null) req.add("preCourse", preCourse);
        req.add("num", num);
        req.add("name", courseName);
        req.add("coursePath", coursePath);

        DataResponse res = HttpRequestUtil.request("/api/course/courseSave", req);

        if (res != null) {
            if (res.getCode() == 0) {
                MessageDialog.showDialog("修改并保存成功！");
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        }
    }

    public void deleteItem(String name){
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(6));
        Map<String,Object> data = observableList.get(j);

        Integer courseId = Integer.parseInt(data.get("courseId").toString());

        DataResponse res;
        DataRequest req = new DataRequest();
        // req.add("courseId",j); j代表的是行数,不代表课程id
        req.add("courseId",courseId);
        res = HttpRequestUtil.request("/api/course/courseDelete", req); //从后台获取所有学生信息列表集合
        if(res!= null) {
            // code0代表后端返回了OK
            if (res.getCode() == 0) {
                MessageDialog.showDialog("删除成功！");
                onQueryButtonClick(); // 更新表数据.
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        }
        else {
            MessageDialog.showDialog("该课程具有成绩.无法删除");
        }
    }


    @FXML
    void onAddCourse(ActionEvent event) {
        Stage dialog = new Stage();
        dialog.setTitle("添加课程");
        dialog.initModality(Modality.APPLICATION_MODAL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField numField = new TextField();
        TextField nameField = new TextField();
        TextField creditField = new TextField();
        TextField preCourseField = new TextField();

        grid.add(new Label("课程号:"), 0, 0);
        grid.add(numField, 1, 0);
        grid.add(new Label("课程名:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("学分:"), 0, 2);
        grid.add(creditField, 1, 2);
        grid.add(new Label("前序课程名:"), 0, 3);
        grid.add(preCourseField, 1, 3);

        Button submitButton = new Button("提交");
        submitButton.setOnAction(e -> {
            String num = numField.getText();
            String name = nameField.getText();
            String creditText = creditField.getText();
            String preCourse = preCourseField.getText();

            if (num.isEmpty() || name.isEmpty() || creditText.isEmpty()) {
                MessageDialog.showDialog("请填写完整课程信息！");
                return;
            }

            int credit = 0;
            try {
                credit = Integer.parseInt(creditText);
            } catch (NumberFormatException ex) {
                MessageDialog.showDialog("学分必须是整数！");
                return;
            }

            DataRequest req = new DataRequest();
            req.add("num", num);
            req.add("name", name);
            req.add("credit", credit);
            req.add("preCourse", preCourse);

            DataResponse res = HttpRequestUtil.request("/api/course/courseAdd", req);
            if (res != null && res.getCode() == 0) {
                MessageDialog.showDialog("课程添加成功！");
                dialog.close();
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(res != null ? res.getMsg() : "请求失败！");
            }
        });

        HBox buttonBox = new HBox(10, submitButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, 4);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.showAndWait();

    }


    @FXML
    public void initialize() {
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));
        // 设置这一列的单元格为可编辑的文本框样式。 回车之后才能保存到表格
        numColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        // 设置编辑完成后的操作。
        numColumn.setOnEditCommit(event -> {
            // 获得当前行的map
            Map<String,Object> map = event.getRowValue();
            map.put("num", event.getNewValue()); // 更新num对应的列的值
        });

        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("name", event.getNewValue());
        });
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        creditColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        creditColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            String newValue = event.getNewValue();

            try {
                // 尝试转换为整数
                int credit = Integer.parseInt(newValue);
                map.put("credit", credit);
            } catch (NumberFormatException e) {
                // 弹出错误提示
                MessageDialog.showDialog("请输入合法的整数学分！");
                // 回退原来的值
                dataTableView.refresh(); // 强制刷新，不然 UI 可能不更新
            }
        });
        preCourseColumn.setCellValueFactory(new MapValueFactory<>("preCourse"));
        preCourseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        preCourseColumn.setOnEditCommit(event -> {
            Map<String, Object> map = event.getRowValue();
            map.put("preCourse", event.getNewValue());
        });

        operateColumn.setCellValueFactory(new MapValueFactory<>("operate"));
        dataTableView.setEditable(true);
        // 自动调用获取表格数据的方法
        onQueryButtonClick();
    }


}
