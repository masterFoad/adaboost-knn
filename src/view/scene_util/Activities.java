package view.scene_util;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Method that will help open new stages, set scene, load fxml.. etc
 */
public class Activities {

    /**
     *
     * @param context
     * @param fxmlPath
     * @param context
     */
    public static void openNewWindow(Initializable context, String fxmlPath, String title) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(context.getClass().getResource(fxmlPath));

        Pane myPane = loader.load();
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(myPane));
        stage.show();
        stage.requestFocus();


    }

    //TODO
    public static void setNewScene(){}


    /**
     * pass an event object to this method to close window. (must be inside button event handler)
     * @param e
     */
    public static void closeWindow(ActionEvent e){
        final Node source = (Node) e.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }



}
