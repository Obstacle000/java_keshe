package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.tools.Tool;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreTableController extends ToolController {
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> studentNumColumn;
    @FXML
    private TableColumn<Map,String> studentNameColumn;
    @FXML
    private TableColumn<Map,String> classNameColumn;
    @FXML
    private TableColumn<Map,String> courseNumColumn;
    @FXML
    private TableColumn<Map,String> courseNameColumn;
    @FXML
    private TableColumn<Map,String> creditColumn;
    @FXML
    private TableColumn<Map,String> markColumn;
    @FXML
    private TableColumn<Map, Button> editColumn;

    // 表格数据,里面存这每行的map,跟之前一样,list给行(map)铺路,list是想象成木桶
    private ArrayList<Map> scoreList = new ArrayList();
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表

    @FXML
    private ComboBox<OptionItem> studentComboBox;

    // 后端返回的两个list的OptionItem里面 studentList有PersonId courseList里有courseId的Integer,String形式和各自在下拉框里显示的数据
    private List<OptionItem> studentList;
    @FXML
    private ComboBox<OptionItem> courseComboBox;


    private List<OptionItem> courseList;

    private ScoreEditController scoreEditController = null;
    private Stage stage = null;
    public List<OptionItem> getStudentList() {
        return studentList;
    }
    public List<OptionItem> getCourseList() {
        return courseList;
    }

    @FXML
    public void initialize() {


        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));  //设置列值工程属性
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseNum"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));

        DataRequest req =new DataRequest();
        studentList = HttpRequestUtil.requestOptionItemList("/api/score/getStudentItemOptionList",req); //从后台获取所有学生信息列表集合
        courseList = HttpRequestUtil.requestOptionItemList("/api/score/getCourseItemOptionList",req); //从后台获取所有学生信息列表集合
        OptionItem item = new OptionItem(null,"0","请选择");

        studentComboBox.getItems().addAll(item);
        studentComboBox.getItems().addAll(studentList);

        courseComboBox.getItems().addAll(item);
        courseComboBox.getItems().addAll(courseList);
        //两个ComboBox 里面就存有各自的list和item了,list里存的是OptionItem自定义类型,展示的时候只展示第三个数据

        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        onQueryButtonClick();
    }

    @FXML
    private void onQueryButtonClick(){
        Integer personId = 0;
        Integer courseId = 0;
        OptionItem op;

        op = studentComboBox.getSelectionModel().getSelectedItem(); // 拿到复选框里的一行数据(OptionItem)
        if(op != null)
            personId = Integer.parseInt(op.getValue());
        op = courseComboBox.getSelectionModel().getSelectedItem();
        if(op != null)
            courseId = Integer.parseInt(op.getValue());

        DataResponse res;
        DataRequest req =new DataRequest();
        req.add("personId",personId);
        req.add("courseId",courseId);
        res = HttpRequestUtil.request("/api/score/getScoreList",req); //从后台获取所有学生信息列表集合
        if(res != null && res.getCode()== 0) {
            scoreList = (ArrayList<Map>)res.getData();
        }
        setTableViewData();
    }
    // 展示数据用observableList
    private void setTableViewData() {
        observableList.clear();
        Map map;
        Button editButton;
        for (int j = 0; j < scoreList.size(); j++) {
            map = scoreList.get(j);
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
        Map data = scoreList.get(j);

        initDialog(); // 展示编辑框
        scoreEditController.showDialog(data); // 数据回显,根据添加或者修改按钮而不同,具体修改逻辑在scoreEditController里面做
        MainApplication.setCanClose(false);
        stage.showAndWait();

    }
    // 展示编辑框
    private void initDialog() {
        if(stage!= null)
            return;
        FXMLLoader fxmlLoader ;
        Scene scene = null;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("score-edit-dialog.fxml"));
            scene = new Scene(fxmlLoader.load(), 260, 140);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.NONE);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("成绩录入对话框！");
            stage.setOnCloseRequest(event ->{
                MainApplication.setCanClose(true);
            });

            scoreEditController = (ScoreEditController) fxmlLoader.getController();
            scoreEditController.setScoreTableController(this);
            scoreEditController.init(); //准备工作,往scoreEditController的成员里放数据

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 整个逻辑链
     * 1. 初始化右上角下拉框数据和两个list成员
     * 2. 点击查询按钮,根据list里的OptionItem的id像后端请求表格数据
     * 3. 用observableList展示数据,给每一行添加编辑按钮(不同于修改按钮),点击后调用editItem()
     * 4. 拿到当前行的数据 ,展示编辑框,回显数据(showDialog)
     * 5. ScoreEditController的okButtonClick方法监听窗口的变化,更新ScoreEditController的data
     * 然后调用本类的doClose("ok",data)方法,保存更新
     * 6. 如果按的是修改按钮,就相当于从第三步开始,editItem和onEditButtonClick方法一样
     *
     * 简单来说就是本类不会进行修改逻辑,会转交给ScoreEditController处理
     * initDialog();
     * scoreEditController.showDialog(null); 里面传递回显的数据,然后就不用管了,ScoreEditController这个类会把
     * 更新的数据传给本类的doClose方法,然后请求后端更新
     */
    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        stage.close();
        if(!"ok".equals(cmd))
            return;
        DataResponse res;
        Integer personId = CommonMethod.getInteger(data,"personId");
        if(personId == null) {
            MessageDialog.showDialog("没有选中学生不能添加保存！");
            return;
        }
        Integer courseId = CommonMethod.getInteger(data,"courseId");
        if(courseId == null) {
            MessageDialog.showDialog("没有选中课程不能添加保存！");
            return;
        }
        DataRequest req =new DataRequest();
        req.add("personId",personId);
        req.add("courseId",courseId);
        req.add("scoreId",CommonMethod.getInteger(data,"scoreId"));
        req.add("mark",CommonMethod.getInteger(data,"mark"));
        res = HttpRequestUtil.request("/api/score/scoreSave",req); //保存更新信息
        if(res != null && res.getCode()== 0) {
            onQueryButtonClick();//显示
        }
    }

    @FXML
    private void onAddButtonClick() {
        initDialog();
        scoreEditController.showDialog(null);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    @FXML
    private void onEditButtonClick() {
//        dataTableView.getSelectionModel().getSelectedItems();
        Map data = dataTableView.getSelectionModel().getSelectedItem();
        if(data == null) {
            MessageDialog.showDialog("没有选中，不能修改！");
            return;
        }
        initDialog();
        scoreEditController.showDialog(data);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }
    @FXML
    private void onDeleteButtonClick() {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer scoreId = CommonMethod.getInteger(form,"scoreId");
        DataRequest req = new DataRequest();
        req.add("scoreId", scoreId);
        DataResponse res = HttpRequestUtil.request("/api/score/scoreDelete",req);
        if(res.getCode() == 0) {
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @Override
    public void doNew() {
        onQueryButtonClick();
    }

    @Override
    public void doDelete() {
        onDeleteButtonClick();
    }
}