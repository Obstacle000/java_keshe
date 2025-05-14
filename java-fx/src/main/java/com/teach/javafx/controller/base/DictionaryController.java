package com.teach.javafx.controller.base;

import com.teach.javafx.request.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;

import java.util.List;

/**
 * DictionaryController 登录交互控制类 对应 base/dictionary-panel.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class DictionaryController {
    @FXML
    private TreeTableView<MyTreeNode> treeTable;
    @FXML
    private TreeTableColumn<MyTreeNode, Integer> idColumn;
    @FXML
    private TreeTableColumn<MyTreeNode, String> valueColumn;
    @FXML
    private TreeTableColumn <MyTreeNode, String>titleColumn;

    /**
     * 页面加载对象创建完成初始话方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */
    //
    public void editCommitValue(TableColumn.CellEditEvent<MyTreeNode,String> editEvent){
        MyTreeNode node = editEvent.getRowValue();
        node.setValue(editEvent.getNewValue());
    }
    public void editCommitLabel(TableColumn.CellEditEvent<MyTreeNode,String> editEvent){
        MyTreeNode node = editEvent.getRowValue();
        node.setLabel(editEvent.getNewValue());
    }
    @FXML
    public void initialize() {
        // 拿到根节点
        List<MyTreeNode> dList= HttpRequestUtil.requestTreeNodeList("/api/base/getDictionaryTreeNodeList",new DataRequest());
        if(dList == null || dList.size() == 0)
            return;
        // String columnValue = idColumn.getCellData(i);获取第 i 行 idColumn 列的数据
        // 去每一行的数据里，找 id 字段的值，作为这一列单元格显示的内容
        idColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("id"));
        valueColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("value"));
        titleColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));

        // 编辑完成后把字符串转回成数字（Integer）保存
        // TextFieldTreeTableCell,单元格变编辑框
        idColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new IntegerStringConverter()));
        valueColumn.setCellFactory(TextFieldTreeTableCell.<MyTreeNode>forTreeTableColumn());
        titleColumn.setCellFactory(TextFieldTreeTableCell.<MyTreeNode>forTreeTableColumn());


        idColumn.setOnEditCommit(e->{
            MyTreeNode node = e.getRowValue().getValue();
            node.setId(e.getNewValue());
        });
        valueColumn.setOnEditCommit(e->{
            MyTreeNode node = e.getRowValue().getValue();
            node.setValue(e.getNewValue());
        });
        titleColumn.setOnEditCommit(e->{
            MyTreeNode node = e.getRowValue().getValue();
            node.setTitle(e.getNewValue());
        });

        // 这段代码的目的是 构建一个多层次的树形结构，并将它封装到 TreeItem 中，最终形成一个树形视图
        MyTreeNode root = new MyTreeNode();
        root.setChildren(dList);

        TreeItem<MyTreeNode> rootNode = new TreeItem<>(root);// 虚拟根节点
        MyTreeNode node;

        TreeItem<MyTreeNode> tNode, tNodes; // 一个是子节点对应Node,一个是孙节点

        List<MyTreeNode> sList;
        List<MyTreeNode> cList = root.getChildren(); // 根节点
        int i,j;
        for(i = 0; i < cList.size(); i++) {
            node = cList.get(i);   // 获取根节点的每个子节点
            tNode = new TreeItem<>(node);   // 创建子节点对应的 TreeItem
            sList = node.getChildren();     // 获取子节点的子节点（即孙节点）
            for(j = 0; j < sList.size(); j++) {
                tNodes = new TreeItem<>(sList.get(j));  // 创建孙节点对应的 TreeItem
                tNode.getChildren().add(tNodes);        // 将孙节点加入到子节点的子节点中
            }
            rootNode.getChildren().add(tNode);  // 将子节点加入到根节点的子节点中
        }

        rootNode.setExpanded(true);
        treeTable.setRoot(rootNode);
        treeTable.setPlaceholder(new Label("点击添加按钮增加一行"));
        treeTable.setEditable(true);
        treeTable.getSelectionModel().selectFirst();

        TreeTableView.TreeTableViewSelectionModel<MyTreeNode> tsm = treeTable.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices(); // 展示列
        list.addListener((ListChangeListener.Change<? extends Integer> change) -> {
            System.out.println("Row selection has changed");
        });
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
    @FXML
    public void onDeleteButtonClick() {
        TreeTableView.TreeTableViewSelectionModel<MyTreeNode> sm = treeTable.getSelectionModel();
        if (sm.isEmpty()) {
            MessageDialog.showDialog("没有选择，无法删除");
            return;
        }

        int rowIndex = sm.getSelectedIndex();
        TreeItem<MyTreeNode> selectedItem = sm.getModelItem(rowIndex);
        TreeItem<MyTreeNode> parent = selectedItem.getParent();

        if (parent == null) {
            MessageDialog.showDialog("不能删除根节点");
            return;
        }

        // 先获取要删除的节点数据
        MyTreeNode node = selectedItem.getValue();
        DataRequest req = new DataRequest();
        req.add("id", node.getId());

        // 删除未保存节点常见处理方法
        if (node.getId() == null) {
            // 没有id是临时新建的节点,只需要前端删除即可
            parent.getChildren().remove(selectedItem); // 菜单树上删除
            parent.getValue().getChildren().remove(node);// 节点上删除
            MessageDialog.showDialog("已删除未保存的节点！");
            return;
        }

        // 先发请求到后端确认是否删除成功
        DataResponse res = HttpRequestUtil.request("/api/base/dictionaryDelete", req);

        if (res.getCode() == 0) {
            // 后端删除成功后，再更新前端树
            // 采用了即使删除,为不是重新渲染
            parent.getChildren().remove(selectedItem);
            parent.getValue().getChildren().remove(node);
            MessageDialog.showDialog("删除成功！");
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @FXML
    public void onSaveButtonClick() {
        TreeTableView.TreeTableViewSelectionModel<MyTreeNode> sm = treeTable.getSelectionModel();
        int rowIndex = sm.getSelectedIndex();

        TreeItem<MyTreeNode> selectedItem = sm.getModelItem(rowIndex);
        MyTreeNode node = selectedItem.getValue();
        DataRequest req = new DataRequest();
        req.add("id", node.getId());
        req.add("value",node.getValue());
        req.add("title",node.getTitle());
        req.add("pid",node.getPid());
        DataResponse res = HttpRequestUtil.request("/api/base/dictionarySave", req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("保存成功！");
        }else {
            MessageDialog.showDialog(res.getMsg());
        }

    }
}
