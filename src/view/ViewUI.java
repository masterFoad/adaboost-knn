package view;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public abstract class ViewUI extends StackPane implements Initializable{


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        layoutView();

    }


    protected ViewUI(String fxml) {
        try {
            load(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public ViewUI() {

        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }





    protected void load() throws IOException{
        load(resource());
    }


    private void load(String fxml) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }



    public abstract String resource();

    public <E extends Node> E findViewById(String identifier){
        return (E) lookup("#"+identifier);
    }


    public abstract void layoutView();
}