package org.example.hakmana;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FooterController extends VBox implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public FooterController() {
        super();
        FXMLLoader fxmlFooterLoader = new FXMLLoader(getClass().getResource("Component/Footer.fxml"));
        fxmlFooterLoader.setController(this);
        fxmlFooterLoader.setRoot(this);

        try {
            fxmlFooterLoader.load();
        }
        catch (IOException Footerexception){
            throw new RuntimeException(Footerexception);
        }

    }
}
