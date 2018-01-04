package view;

import common.GenericReader;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.ADABOOST;
import model.KNN;
import model.SetStarter;
import model.Tuple;
import model.thread_center.ThreadPoolCenter;
import view.scene_util.CircularBubbleChart;
import view.scene_util.SceneController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class ScatterChartSample extends Application {

    @Override
    public void start(Stage stage) throws InterruptedException {


        SetStarter.initKNNs(GenericReader.init("/weights.csv", 2, (metaData, numOfClasses) -> GenericReader.createClassifier(metaData, numOfClasses)).toArray(new KNN[0]));
        // reading the data from csv
        SetStarter
                .divide(
                        GenericReader.init("/data1.csv",
                                0,
                                (metaData, numOfClasses) -> GenericReader.createTuple(metaData)).toArray(new Tuple[0]),
                        0.66);

        Tuple[] trainingSet = SetStarter.getTrainingSet();
        Tuple[] testingSet = SetStarter.getTestingSet();
//

//        for (KNN knn : SetStarter.getWeakClassifiers()) {
//            knn.preprocessing();
//        }

        int totalTotal = 0;

//        for (int oo = 0; oo < 5; oo++) {
        for (int i = 0; i < trainingSet.length; i++) {
            trainingSet[i].setWeight(1.0 / (double) trainingSet.length);
        }

//        KNN knn = SetStarter.getWeakClassifiers()[0];
//        for (int i = 0; i < SetStarter.getTrainingSet().length; i++) {
////            System.out.println(trainingSet[i]);
////            System.out.println(knn.init(trainingSet, trainingSet[i]));
//            knn.init(trainingSet, trainingSet[i]);
//        }


        for (int i = 0; i < testingSet.length; i++) {
            testingSet[i].setWeight(1.0);
        }


        long startTime = System.currentTimeMillis();
        ADABOOST superClassifier = new ADABOOST(
                new ArrayList<>(Arrays.asList(SetStarter.getWeakClassifiers())),
                trainingSet);

        superClassifier.buildModel2();
        //superClassifier.runOnTestingSet();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        totalTotal += totalTime;
        System.out.println(totalTime);

        System.out.println(KNN.counter);
        superClassifier.runOnTestingSet();


//        }

//        System.out.println("total total : "+ ((double)totalTotal/5));

        final List<Runnable> rejected = ThreadPoolCenter.getExecutor().shutdownNow();
        System.out.println(("Rejected tasks: {} " + rejected.size()));
        //ThreadPoolCenter.closeThreadPool();


        //SceneController.getInstance().init(stage);



        SplitPane hortSplitPane = new SplitPane(
                new ScrollPane(new HBox(
                        chartFactory(Stream.concat(
                                Arrays.stream(SetStarter.getTrainingSet()), Arrays.stream(SetStarter.getTestingSet()))
                                .toArray(Tuple[]::new), "All data")
                        , chartFactory(superClassifier.getFinalResultsForTraining(), "Training Data results - showing final weights (x200)", true)
                )
              )
       );

        TextArea reportArea = new TextArea(superClassifier.getFinalReport().toString());
        //reportArea.setText(superClassifier.get);

        SplitPane verSplitPane = new SplitPane(hortSplitPane, new ScrollPane(
                new HBox(
                        chartFactory(superClassifier.getFinalResultsForTesting(), "Testing Data results", false)
                        ,
                        reportArea
                ))
        );
        verSplitPane.setOrientation(Orientation.VERTICAL);

       // ScrollPane sp = new ScrollPane(verSplitPane);
        ScrollPane mainPane = new ScrollPane(new HBox(verSplitPane));

//        mainPane.setMaxHeight(600);
//        mainPane.setMaxWidth(1000);
        Scene scene = new Scene(mainPane);
//        SceneController.getInstance().display(mainPane, "charts");
//
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }


    public BubbleChart<Number, Number> chartFactory(List<Pair<Tuple, Tuple>> tuples, String title, boolean isTraining) {

        final NumberAxis xAxis = new NumberAxis(-70, 70, 1);
        final NumberAxis yAxis = new NumberAxis(-70, 70, 1);
        final BubbleChart<Number, Number> blc = new
                CircularBubbleChart<>(xAxis, yAxis);
//        xAxis.setLabel("Age (years)");
//        yAxis.setLabel("Returns to date");
//        sc.setTitle("Investment Overview");
        blc.setTitle(title);
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("class 1");
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("class 2");
        XYChart.Series series3 = new XYChart.Series();
        series3.setName("misclassified class 1");
        XYChart.Series series4 = new XYChart.Series();
        series4.setName("misclassified class 2");

        for (Pair<Tuple, Tuple> t : tuples) {
            if (isTraining) {
                if (t.getKey().getClassNum() == 1) {
                    series1.getData().add(new XYChart.Data(t.getKey().getDataVector()[0], t.getKey().getDataVector()[1], t.getKey().getWeight() / 0.005));
                    if (t.getValue().getClassNum() != 1) {
                        series3.getData().add(new XYChart.Data(t.getValue().getDataVector()[0], t.getValue().getDataVector()[1], t.getKey().getWeight() / 0.005));
                    }
                }

                if (t.getKey().getClassNum() == 2) {
                    series2.getData().add(new XYChart.Data(t.getKey().getDataVector()[0], t.getKey().getDataVector()[1], t.getKey().getWeight() / 0.005));
                    if (t.getValue().getClassNum() != 2) {
                        series4.getData().add(new XYChart.Data(t.getValue().getDataVector()[0], t.getValue().getDataVector()[1], t.getKey().getWeight() / 0.005));
                    }
                }
            }else{
                if (t.getKey().getClassNum() == 1) {
                    series1.getData().add(new XYChart.Data(t.getKey().getDataVector()[0], t.getKey().getDataVector()[1],t.getKey().getWeight()));
                    if (t.getValue().getClassNum() != 1) {
                        series3.getData().add(new XYChart.Data(t.getValue().getDataVector()[0], t.getValue().getDataVector()[1],t.getKey().getWeight()));
                    }
                }

                if (t.getKey().getClassNum() == 2) {
                    series2.getData().add(new XYChart.Data(t.getKey().getDataVector()[0], t.getKey().getDataVector()[1], t.getKey().getWeight()));
                    if (t.getValue().getClassNum() != 2) {
                        series4.getData().add(new XYChart.Data(t.getValue().getDataVector()[0], t.getValue().getDataVector()[1], t.getKey().getWeight()));
                    }
                }
            }

        }
        blc.getData().addAll(series1, series2, series3, series4);
        return blc;
    }

//    public ScatterChart<Number, Number> chartFactory(Tuple[] tuples, String title) {
//
//        final NumberAxis xAxis = new NumberAxis(-70, 70, 1);
//        final NumberAxis yAxis = new NumberAxis(-70, 70, 1);
//        final ScatterChart<Number, Number> sc = new
//                ScatterChart<Number, Number>(xAxis, yAxis);
//        sc.setTitle(title);
////        xAxis.setLabel("Age (years)");
////        yAxis.setLabel("Returns to date");
////        sc.setTitle("Investment Overview");
//
//        XYChart.Series series1 = new XYChart.Series();
//        series1.setName("class 1");
//        XYChart.Series series2 = new XYChart.Series();
//        series2.setName("class 2");
//
//
//        for (Tuple t : tuples) {
//
//            if (t.getClassNum() == 1) {
//                series1.getData().add(new XYChart.Data(t.getDataVector()[0], t.getDataVector()[1]));
//
//            }
//
//            if (t.getClassNum() == 2) {
//                series2.getData().add(new XYChart.Data(t.getDataVector()[0], t.getDataVector()[1]));
//            }
//        }
//        sc.getData().addAll(series1, series2);
//        return sc;
//    }

    public BubbleChart<Number, Number> chartFactory(Tuple[] tuples, String title) {
        final NumberAxis xAxis = new NumberAxis(-70, 70, 1);
        final NumberAxis yAxis = new NumberAxis(-70, 70, 1);
        final BubbleChart<Number, Number> blc = new
                CircularBubbleChart<>(xAxis, yAxis);
        blc.setTitle(title);
//        xAxis.setLabel("Age (years)");
//        yAxis.setLabel("Returns to date");
//        sc.setTitle("Investment Overview");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("class 1");
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("class 2");


        for (Tuple t : tuples) {

            if (t.getClassNum() == 1) {
                series1.getData().add(new XYChart.Data(t.getDataVector()[0], t.getDataVector()[1], 1));

            }

            if (t.getClassNum() == 2) {
                series2.getData().add(new XYChart.Data(t.getDataVector()[0], t.getDataVector()[1], 1));
            }
        }
        blc.getData().addAll(series1, series2);
        return blc;
    }


}