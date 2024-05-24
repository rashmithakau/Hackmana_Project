package org.example.hakmana.view.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController extends VBox implements Initializable {

    /*Injectors for Labels in fxml file*/
    @FXML
    public Text headerTitle;
    @FXML
    public Text userName;
    @FXML
    public Text designation;

    /*Variables for change the labels*/
    @Getter
    private String titleMsg;
    @Setter
    @Getter
    private String fontSize;
    @Getter
    private String usernameMsg;
    @Getter
    private String designationMsg;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public HeaderController() {
        super();
        FXMLLoader fxmlHeaderLoader = new FXMLLoader(org.example.hakmana.view.component.HeaderController.class.getResource("Header.fxml"));
        fxmlHeaderLoader.setController(this);
        fxmlHeaderLoader.setRoot(this);

        try{
            fxmlHeaderLoader.load();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public void setTitleMsg(String titleMsg) {
        String fontStyle = "-fx-font-size:"+fontSize+";";
        headerTitle.setStyle(fontStyle);
        this.titleMsg = titleMsg;
        headerTitle.setText(this.titleMsg);
    }

    public void setUsernameMsg(String usernameMsg) {
        this.usernameMsg = usernameMsg;
        userName.setText(this.usernameMsg);
    }

    public void setDesignationMsg(String designationMsg) {
        this.designationMsg = designationMsg;
        designation.setText(this.designationMsg);
    }
}