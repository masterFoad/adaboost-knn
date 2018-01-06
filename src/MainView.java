
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import view.IntroUI;
import view.scene_util.SceneController;

public class MainView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        SceneController.getInstance().init(primaryStage);

        IntroUI test = new IntroUI();
        SplitPane sp = new SplitPane(test);
        Scene sce = new Scene(sp);
        primaryStage.setScene(sce);
        primaryStage.show();
        //SceneController.getInstance().display(sp , "Adaboost");

    }
}
