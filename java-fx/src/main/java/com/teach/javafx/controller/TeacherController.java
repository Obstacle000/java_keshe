package com.teach.javafx.controller;

import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * TeacherController 登录交互控制类 对应 teacher_panel.fxml  对应于教师管理的后台业务处理的控制器，主要获取数据和保存数据的方法不同
 *
 * @FXML 属性 对应fxml文件中的
 * @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class TeacherController extends ToolController {
    private ImageView photoImageView;

    @FXML
    private TableColumn<Map, String> addressColumn;

    @FXML
    private TextField addressField;

    @FXML
    private TableColumn<Map, String> birthdayColumn;

    @FXML
    private DatePicker birthdayPick;

    @FXML
    private TableColumn<Map, String> cardColumn;

    @FXML
    private TextField cardField;

    @FXML
    private TableView<Map> dataTableView;

    @FXML
    private TableColumn<Map, String> degreeColumn;

    @FXML
    private TextField degreeField;

    @FXML
    private TableColumn<Map, String> deptColumn;

    @FXML
    private TextField deptField;

    @FXML
    private TableColumn<Map, String> emailColumn;

    @FXML
    private TextField emailField;

    @FXML
    private TableColumn<Map, String> genderColumn;

    @FXML
    private ComboBox<OptionItem> genderComboBox;

    @FXML
    private TableColumn<Map, String> majorColumn;

    @FXML
    private TextField majorField;

    @FXML
    private TableColumn<Map, String> nameColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TableColumn<Map, String> numColumn;

    @FXML
    private TextField numField;

    @FXML
    private TextField numNameTextField;

    @FXML
    private TableColumn<Map, String> phoneColumn;

    @FXML
    private TextField phoneField;

    @FXML
    private Button photoButton;

    @FXML
    private TableColumn<Map, String> titleColumn;

    @FXML
    private TextField titleField;

    private Integer personId = null;  //当前编辑修改的老师的主键

    private ArrayList<Map> teacherList = new ArrayList();  // 老师信息列表数据
    private List<OptionItem> genderList;   //性别选择列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();  // TableView渲染列表

    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < teacherList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(teacherList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    @FXML
    public void initialize() {
        photoImageView = new ImageView();
        photoImageView.setFitHeight(100);
        photoImageView.setFitWidth(100);
        photoButton.setGraphic(photoImageView);
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("numName", "");
        res = HttpRequestUtil.request("/api/teacher/getTeacherList", req); //从后台获取所有学生信息列表集合
        if (res != null && res.getCode() == 0) {
            teacherList = (ArrayList<Map>) res.getData();
        }
        // 以后用设置的值获取表格中的数据 String columnValue = numColumn.getCellData(i);获取第 i 行 numColumn 列的数据
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        deptColumn.setCellValueFactory(new MapValueFactory<>("dept"));
        majorColumn.setCellValueFactory(new MapValueFactory<>("major")); //
        degreeColumn.setCellValueFactory(new MapValueFactory<>("degree")); //
        titleColumn.setCellValueFactory(new MapValueFactory<>("title")); //
        cardColumn.setCellValueFactory(new MapValueFactory<>("card"));
        genderColumn.setCellValueFactory(new MapValueFactory<>("genderName"));
        birthdayColumn.setCellValueFactory(new MapValueFactory<>("birthday"));
        emailColumn.setCellValueFactory(new MapValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new MapValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new MapValueFactory<>("address"));

        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        setTableViewData();
        genderList = HttpRequestUtil.getDictionaryOptionItemList("XBM");

        genderComboBox.getItems().addAll(genderList);
        birthdayPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
    }

    public void clearPanel() {
        personId = null;
        numField.setText("");
        nameField.setText("");
        deptField.setText("");
        majorField.setText("");

        degreeField.setText("");
        titleField.setText("");

        cardField.setText("");
        genderComboBox.getSelectionModel().select(-1);
        birthdayPick.getEditor().setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
    }
    // 选择某一行
    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeStudentInfo();
    }
    // 右边表单回显
    protected void changeStudentInfo() {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        personId = CommonMethod.getInteger(form, "personId");
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        DataResponse res = HttpRequestUtil.request("/api/teacher/getTeacherInfo", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        deptField.setText(CommonMethod.getString(form, "dept"));
        majorField.setText(CommonMethod.getString(form, "major"));
        degreeField.setText(CommonMethod.getString(form, "degree"));
        titleField.setText(CommonMethod.getString(form, "title"));
        cardField.setText(CommonMethod.getString(form, "card"));
        genderComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(genderList, CommonMethod.getString(form, "gender")));
        birthdayPick.getEditor().setText(CommonMethod.getString(form, "birthday"));
        emailField.setText(CommonMethod.getString(form, "email"));
        phoneField.setText(CommonMethod.getString(form, "phone"));
        addressField.setText(CommonMethod.getString(form, "address"));
        displayPhoto();
    }

    @FXML
    protected void onAddButtonClick() {
        clearPanel();
    }
    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        personId = CommonMethod.getInteger(form, "personId");
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        DataResponse res = HttpRequestUtil.request("/api/teacher/teacherDelete", req);
        if(res!= null) {
            if (res.getCode() == 0) {
                MessageDialog.showDialog("删除成功！");
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        }
    }
    @FXML
    protected void onSaveButtonClick() {
        if (numField.getText().isEmpty()) {
            MessageDialog.showDialog("学号为空，不能修改");
            return;
        }
        Map<String,Object> form = new HashMap<>();
        form.put("num", numField.getText());
        form.put("name", nameField.getText());
        form.put("dept", deptField.getText());
        form.put("major", majorField.getText());
        form.put("title", titleField.getText());
        form.put("degree", degreeField.getText());
        form.put("card", cardField.getText());
        if (genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("gender", genderComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("birthday", birthdayPick.getEditor().getText());
        form.put("email", emailField.getText());
        form.put("phone", phoneField.getText());
        form.put("address", addressField.getText());
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/teacher/teacherEditSave", req);
        if (res.getCode() == 0) {
            personId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    public void doNew() {
        clearPanel();
    }

    public void doSave() {
        onSaveButtonClick();
    }

    public void doDelete() {
        onDeleteButtonClick();
    }

    public void doExport() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName", numName);
        byte[] bytes = HttpRequestUtil.requestByteData("/api/teacher/getTeacherListExcl", req);
        if (bytes != null) {
            try {
                FileChooser fileDialog = new FileChooser();
                fileDialog.setTitle("前选择保存的文件");
                fileDialog.setInitialDirectory(new File("C:/"));
                fileDialog.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
                File file = fileDialog.showSaveDialog(null);
                if (file != null) {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(bytes);
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    protected void onImportButtonClick() {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("前选择教师数据表");
        fileDialog.setInitialDirectory(new File("D:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
        File file = fileDialog.showOpenDialog(null);
        String paras = "";
        DataResponse res = HttpRequestUtil.importData("/api/term/importTeacherData", file.getPath(), paras);
        if (res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    void onImportFeeButtonClick(ActionEvent event) {

    }

    @FXML
    void onPhotoButtonClick(ActionEvent event) {

    }

    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/teacher/getTeacherList", req);
        if (res != null && res.getCode() == 0) {
            teacherList = (ArrayList<Map>) res.getData();
            setTableViewData();
        }
    }




    public void displayPhoto(){
        DataRequest req = new DataRequest();
        req.add("fileName", "photo/" + personId + ".jpg");  //个人照片显示
        byte[] bytes = HttpRequestUtil.requestByteData("/api/base/getFileByteData", req);
        if (bytes != null) {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            Image img = new Image(in);
            photoImageView.setImage(img);
        }

    }

    @FXML
    public void onPhotoButtonClick(){
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("图片上传");
//        fileDialog.setInitialDirectory(new File("C:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG 文件", "*.jpg"));
        File file = fileDialog.showOpenDialog(null);
        if(file == null)
            return;
        DataResponse res =HttpRequestUtil.uploadFile("/api/base/uploadPhoto",file.getPath(),"photo/" + personId + ".jpg",null);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
            displayPhoto();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

}
