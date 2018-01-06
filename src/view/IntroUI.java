package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lab.AdaboostExperiment;
import view.scene_util.Activities;
import view.scene_util.SceneController;

public class IntroUI extends ViewUI{


    @FXML
    TextField fold;

    @FXML
    ChoiceBox<String> chooseData;

    @FXML
    Button close;

    @FXML
    Button next;

    public static AdaboostExperiment EXPIREMENT;

    @Override
    public String resource() {
        return "/view/res/fxml/main_menu.fxml";
    }

    @Override
    public void layoutView() {
        close.setOnAction(e-> Activities.closeWindow(e));
        next.setOnAction(e->{
            Activities.closeWindow(e);

            String weights=null;

            if(chooseData.getValue().equals("/data1")){
                weights = "/weights1";
            }
            if(chooseData.getValue().equals("/data2")){
                weights = "/weights2";
            }
            if(chooseData.getValue().equals("/data3")){
                weights = "/weights3";
            }

            EXPIREMENT = new AdaboostExperiment(weights, chooseData.getValue(), 3, Double.parseDouble(fold.getText()));


        });
    }
}
