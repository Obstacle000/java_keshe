package com.teach.javafx.controller.base;

import com.teach.javafx.MainApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
/**
 * ChoiceController 登录交互控制类 对应 base/choice-dialog.fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
// 34
public class ChoiceController {
    @FXML
    private TextFlow textFLow;

    private Text text;
    private Stage stage;
    private int choice;
    /**
     * 页面加载对象创建完成初始话方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize() {
        text = new Text("");
        text.setFill(Color.BLACK);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        textFLow.getChildren().add(text);
        textFLow.setLineSpacing(5);
        textFLow.setDisable(false);
        textFLow.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 1;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: black;");
    }
    //
    @FXML
    public void cancelButtonClick(){
        choice = MessageDialog.CHOICE_CANCEL;
        close();
    }

    @FXML
    public void yesButtonClick(){
        choice = MessageDialog.CHOICE_YES;
        close();
    }
    @FXML
    public void noButtonClick(){
        choice = MessageDialog.CHOICE_NO;
        close();
    }
//
    public void close(){
        // setCanClose 就是比如说你打开了一个小窗口的时候不能关闭其父窗口时用
        MainApplication.setCanClose(true);
        stage.close();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public int  choiceDialog(String msg) {
        text.setText(msg);
        // 显示这个窗口，并暂停当前代码的执行，直到用户关闭这个窗口
        // 和上面的close方法配合,会卡在这一行直到用户点了某一个按钮,choice里面有值了
        this.stage.showAndWait();
        return choice;
    }

}
