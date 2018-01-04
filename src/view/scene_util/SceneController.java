package view.scene_util;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class SceneController {

    private Stage primaryStage;

    private static class SingleInstance {
        private static final SceneController instance = new SceneController();
    }

    public static SceneController getInstance() {
        return SceneController.SingleInstance.instance;
    }



    public void init(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.initStyle(StageStyle.UNDECORATED);
    }

    /**
     * scene, and title
     * @param sceneToDisplay
     * @param title
     */
    public void display(Pane sceneToDisplay,String title){
        primaryStage.setTitle(title);
        Scene myScene = new Scene(sceneToDisplay);
        primaryStage.setScene(myScene);
        primaryStage.show();
    }


    /**
     * scene to display
     * @param sceneToDisplay
     */
    public void popUp(Pane sceneToDisplay){

        final Stage dialog = new Stage();
        dialog.setAlwaysOnTop(true);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(primaryStage);
        dialog.initStyle(StageStyle.UNDECORATED);
        Scene dialogScene = new Scene(sceneToDisplay);
        dialog.setScene(dialogScene);
        fadeTransition(sceneToDisplay, e-> dialog.show());
        dialog.show();


    }

    public void popUp(Pane sceneToDisplay,javafx.event.EventHandler<WindowEvent> onClose ){

        final Stage dialog = new Stage();
        dialog.setAlwaysOnTop(true);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(primaryStage);
        dialog.initStyle(StageStyle.UNDECORATED);
        Scene dialogScene = new Scene(sceneToDisplay);
        dialog.setScene(dialogScene);
        fadeTransition(sceneToDisplay, e-> dialog.show());
        dialog.show();
        //dialog.setOnCloseRequest(onClose);


    }


    /**
     *  Apply the Fade Transition to the node
     * @param e the node to which apply transition
     */
    public static void fadeTransition(Node e){
        FadeTransition x=new FadeTransition(new Duration(1000),e);
        x.setFromValue(0);
        x.setToValue(1);
        //x.setCycleCount(1);
        x.setInterpolator(Interpolator.EASE_OUT);
        x.play();

    }

    public static void fadeTransition(Node e, javafx.event.EventHandler<ActionEvent> onFinish){
        System.out.println(e.getParent());
        FadeTransition x=new FadeTransition(new Duration(1000),e);
        x.setFromValue(0);
        x.setToValue(100);
        x.setCycleCount(1);
        x.setInterpolator(Interpolator.LINEAR);
        x.setOnFinished(onFinish);
        x.play();


    }

    /**
     *  Apply the Fade Transition to the node
     * @param e the node to which apply transition with speed
     */
    public static void fadeTransition(Node e,int speedInMillis){
        FadeTransition x=new FadeTransition(new Duration(speedInMillis),e);
        x.setFromValue(0);
        x.setToValue(100);
        x.setCycleCount(1);
        x.setInterpolator(Interpolator.EASE_OUT);
        x.play();
    }





}