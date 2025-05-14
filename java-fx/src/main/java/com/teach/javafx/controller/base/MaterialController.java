package com.teach.javafx.controller.base;

import com.teach.javafx.AppStore;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.MyTreeNode;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class MaterialController {

    @FXML private TreeTableView<MyTreeNode> treeTable;
    @FXML
    private TreeTableColumn<MyTreeNode, Integer> idColumn;
    @FXML private TreeTableColumn<MyTreeNode, String> courseNameColumn;
    @FXML private TreeTableColumn<MyTreeNode, String> titleColumn;
    @FXML private Button uploadButton;
    @FXML private Button editTitleButton;
    @FXML private Button deleteButton;
    @FXML private Button downloadButton;
    @FXML private Button addButton;

    // 初始化树结构或其他必要操作
    @FXML
    public void initialize() {
        treeTable.setEditable(true);
        // 获取当前用户角色，判断是否为学生
        String currentUserRole = AppStore.getJwt().getRole();
        if ("ROLE_STUDENT".equals(currentUserRole)) {
            uploadButton.setVisible(false);
            editTitleButton.setVisible(false);
            deleteButton.setVisible(false);
        }

        // 请求后端获取教学资料树形数据（课程节点 + 文件节点）
        List<MyTreeNode> materialList = HttpRequestUtil.requestTreeNodeList("/api/material/getMaterialTreeNode", new DataRequest());
        if (materialList == null || materialList.isEmpty()) return;

        // 设置列数据绑定
        idColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));
        courseNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value")); // value 字段表示文件名

        // 设置单元格可编辑
        idColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new IntegerStringConverter()));
        titleColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        courseNameColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

        idColumn.setOnEditCommit(e->{
            MyTreeNode node = e.getRowValue().getValue();
            node.setId(e.getNewValue());
        });
        titleColumn.setOnEditCommit(e -> {
            MyTreeNode node = e.getRowValue().getValue();
            node.setTitle(e.getNewValue());
        });
        courseNameColumn.setOnEditCommit(e -> {
            MyTreeNode node = e.getRowValue().getValue();
            node.setValue(e.getNewValue());
        });

        // 构建根节点（虚拟节点）
        MyTreeNode rootData = new MyTreeNode();
        rootData.setTitle("根目录");
        TreeItem<MyTreeNode> rootNode = new TreeItem<>(rootData);
        rootNode.setExpanded(true);

        // 构建一、二级节点（课程 -> 文件）
        for (MyTreeNode courseNode : materialList) {
            TreeItem<MyTreeNode> courseItem = new TreeItem<>(courseNode);
            if (courseNode.getChildren() != null) {
                for (MyTreeNode fileNode : courseNode.getChildren()) {
                    TreeItem<MyTreeNode> fileItem = new TreeItem<>(fileNode);
                    courseItem.getChildren().add(fileItem);
                }
            }
            rootNode.getChildren().add(courseItem);
        }

        // 设置 TreeTableView
        treeTable.setRoot(rootNode);
        treeTable.setShowRoot(false); // 不显示虚拟根节点
        treeTable.getSelectionModel().selectFirst();
        treeTable.setPlaceholder(new Label("暂无资料，请点击上传添加"));
    }

    // 自动聚焦并编辑某一行的第一个单元格。
    private void editItem(TreeItem<MyTreeNode> item) {
        int newRowIndex = treeTable.getRow(item);
        treeTable.scrollTo(newRowIndex);
        TreeTableColumn<MyTreeNode, ?> firstCol = treeTable.getColumns().get(0);
        treeTable.getSelectionModel().select(item);
        treeTable.getFocusModel().focus(newRowIndex, firstCol); // 聚焦
        treeTable.edit(newRowIndex, firstCol); // 让这一行的第一列进入编辑模式
    }
    @FXML
    public void onAddButtonClick(){
        if (treeTable.getSelectionModel().isEmpty()) {
            MessageDialog.showDialog("选择一个要添加的的行");
            return;
        }
        TreeTableView.TreeTableViewSelectionModel<MyTreeNode> sm = treeTable.getSelectionModel();
        int rowIndex = sm.getSelectedIndex(); // 选择的行索引
        TreeItem<MyTreeNode> selectedItem = sm.getModelItem(rowIndex); // 根据索引拿到选中的行的Node
        MyTreeNode node = selectedItem.getValue();
        MyTreeNode newNode = new MyTreeNode();
        newNode.setPid(node.getId()); // 设置选中的行为父节点
        node.getChildren().add(newNode);
        TreeItem<MyTreeNode> item = new TreeItem<>(newNode);
        selectedItem.getChildren().add(item);
        selectedItem.setExpanded(true); // 让指定的 TreeItem 节点 展开（显示其子节点）。
        this.editItem(item);
    }


    // 上传资料 - 仅仅是吧本地文件用io流写到某一个指定文件(模拟云服务)
    // 下载的时候请求后端,拿到字节数据,把数据放到一个地方,用户点击下载按钮,再请求后端io流写到指定文件
    // 本地文件 - 远程文件 - 指定文件
    @FXML
    private void onUploadButtonClick() {
        // 逻辑应该是我先点击一个节点然后再上传
        // 获取当前选中的 TreeItem
        TreeItem<MyTreeNode> selectedItem = treeTable.getSelectionModel().getSelectedItem();

        if (selectedItem == null || selectedItem.getValue() == null) {
            MessageDialog.showDialog("请先选择一个节点再上传资料！");
            return;
        }
        if(selectedItem.getValue().getPid() == null)
        {
            MessageDialog.showDialog("请选择一个子节点再上传资料！");
            return;
        }

        MyTreeNode selectedNode = selectedItem.getValue();

        // 上传资料的逻辑
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("资料上传");
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT 文件", "*.txt"));
        // 让用户选择一个文件，并返回所选文件的 File 对象。
        File file = fileDialog.showOpenDialog(null);

        if(file == null)
            return;
        DataResponse res =HttpRequestUtil.uploadFile("/api/material/uploadMaterial"
                ,file.getPath()
                ,"material/" + selectedNode.getId()+ ".txt"
                ,selectedNode);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    // 修改资料标题
    @FXML
    private void onEditTitleButtonClick() {
        // 修改资料标题的逻辑
    }

    // 删除资料
    @FXML
    private void onDeleteButtonClick() {
        // 删除资料的逻辑
    }

    // 下载资料
    @FXML
    private void onDownloadButtonClick() {
        // 下载资料的逻辑
    }
}
