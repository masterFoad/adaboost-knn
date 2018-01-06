package view;

import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lab.AdaboostExperiment;
import lab.Lab;
import model.SetStarter;
import model.thread_center.ThreadPoolCenter;
import view.scene_util.Activities;
import view.scene_util.SceneController;

import java.util.List;

public class IntroUI extends ViewUI{


    @FXML
    TextField fold;

    @FXML
    ChoiceBox<String> chooseData;

    @FXML
    Button close;

    @FXML
    Button next;

    public AdaboostExperiment EXPIREMENT;

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

            if(chooseData.getValue().equals("/data1.csv")){
                weights = "/weights1.csv";
            }
            if(chooseData.getValue().equals("/data2.csv")){
                weights = "/weights2.csv";
            }
            if(chooseData.getValue().equals("/data3.csv")){
                weights = "/weights3.csv";
            }

            EXPIREMENT = new AdaboostExperiment(weights, chooseData.getValue(), 3, Double.parseDouble(fold.getText()));


            
            StringBuilder stringBuilder = new StringBuilder();
            long startTime = System.currentTimeMillis();
//        AdaboostExperiment exp1 = new AdaboostExperiment("/weights1.csv", "/data1.csv", 2, 0.66);
//        AdaboostExperiment exp1 = new AdaboostExperiment("/weights2.csv", "/data2.csv", 2, 0.66);
//            AdaboostExperiment exp1 = new AdaboostExperiment("/weights3.csv", "/data3.csv", 3, 0.66);


            stringBuilder.append("Report for "+chooseData.getValue()+" using "+fold.getText()+" training data nad "+(1-Double.parseDouble(fold.getText()))+" testing, "+ SetStarter.getMaxFolds()+" folds CV");
            stringBuilder.append("\n");
            EXPIREMENT.start();
//        exp1.getSuperClassifier().getPredictedTraining();


            Lab l = new Lab(EXPIREMENT);
//        l.getAllResults();


            for (Lab.Result r :
                    l.getAllResults()) {
                stringBuilder.append(r);
                stringBuilder.append("\n");
                stringBuilder.append(l.getAveragesForTraining2classes(r));
                stringBuilder.append("\n");
            }

            for (Lab.Result r :
                    l.getAllResultsForTesting()) {
                stringBuilder.append(r);
                stringBuilder.append("\n");
                stringBuilder.append(l.getAveragesForTest2Classes(r));
                stringBuilder.append("\n");
            }

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            stringBuilder.append("total run time in millis: "+totalTime);
            stringBuilder.append("\n");
//        stringBuilder.append("KNN uses:"+ KNN.counter);
            final List<Runnable> rejected = ThreadPoolCenter.getExecutor().shutdownNow();
            stringBuilder.append(("Rejected tasks: {} " + rejected.size()));

            stringBuilder.append("\n");
            TextArea textArea = new TextArea();
            textArea.setText(stringBuilder.toString());
            textArea.setPrefHeight(700);
            SplitPane sp = new SplitPane(new VBox(textArea));
            sp.setOrientation(Orientation.HORIZONTAL);
            ScrollPane scp = new ScrollPane(sp);
//            Pane p = new Pane(sp);
            Stage stage = new Stage();
            stage.setTitle("ada");
            stage.setScene(new Scene(sp));
            stage.show();
            stage.requestFocus();


        });
    }
}
