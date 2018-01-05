package view;

import common.Utils;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.ADABOOST;
import model.KNN;
import model.SetStarter;
import model.Tuple;
import model.thread_center.ThreadPoolCenter;
import view.scene_util.CircularBubbleChart;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class ScatterChartSample extends Application {

    @Override
    public void start(Stage stage) throws InterruptedException {


        long startTime = System.currentTimeMillis();
        ADABOOST superClassifier = ADABOOST.create("/weights1.csv", "/data1.csv", 2, 0.9);


        superClassifier.buildModel();
        //superClassifier.runOnTestingSet();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);

        System.out.println(KNN.counter);
        //superClassifier.runOnTestingSet();


//        }

//        System.out.println("total total : "+ ((double)totalTotal/5));

        final List<Runnable> rejected = ThreadPoolCenter.getExecutor().shutdownNow();
        System.out.println(("Rejected tasks: {} " + rejected.size()));
        //ThreadPoolCenter.closeThreadPool();


        //SceneController.getInstance().init(stage);


        SplitPane hortSplitPane = new SplitPane(

                chartFactory(Stream.concat(
                        Arrays.stream(SetStarter.getTrainingSet()), Arrays.stream(SetStarter.getTestingSet()))
                        .toArray(Tuple[]::new), "All data")
                , chartFactory(superClassifier.getFinalResultsForTraining(), "Training Data results - showing final weights (x200)", true)


        );
        hortSplitPane.setPrefWidth(stage.getWidth());
        TextArea reportArea = new TextArea(superClassifier.getFinalReport().toString());
        //reportArea.setText(superClassifier.get);

        reportArea.setPrefWidth(1000);
        reportArea.setPrefHeight(600);

        SplitPane verSplitPane = new SplitPane(hortSplitPane, new ScrollPane(
                new HBox(
//                        chartFactory(superClassifier.getFinalResultsForTesting(), "Testing Data results", false)
//                        ,
                        reportArea
                ))
        );

//        verSplitPane.setPrefWidth(1000);
        verSplitPane.setOrientation(Orientation.VERTICAL);
        verSplitPane.setPrefWidth(1000);
        // ScrollPane sp = new ScrollPane(verSplitPane);
        ScrollPane mainPane = new ScrollPane(new HBox(verSplitPane));
        AnchorPane anc = new AnchorPane(mainPane);
        mainPane.setPrefWidth(1000);
        anc.setRightAnchor(mainPane, 100.0);
        anc.setLeftAnchor(mainPane, 100.0);
//        mainPane.setMaxHeight(600);
//        mainPane.setMaxWidth(1000);
        Scene scene = new Scene(anc);
//        SceneController.getInstance().display(mainPane, "charts");
//
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }

    public int calcGraphSize(List<Pair<Tuple, Tuple>> tuples) {
        double minValue = 0.0;
        double maxValue = 0.0;
        double chosen = 0;
        for (Pair<Tuple, Tuple> tps : tuples) {
            for (double d : tps.getValue().getDataVector()) {
                if (d > maxValue) {
                    maxValue = d;
                }
                if (d < minValue) {
                    minValue = d;
                }
            }
        }

        if (Math.abs(maxValue) > Math.abs(minValue)) {
            chosen = (int) Math.abs(maxValue) + 5;
        } else {
            chosen = (int) Math.abs(minValue) + 5;
        }
        return (int) chosen;
    }

    public int calcGraphSize(Tuple[] tuples) {
        double minValue = 0.0;
        double maxValue = 0.0;
        double chosen;
        for (Tuple tps : tuples) {
            for (double d : tps.getDataVector()) {
                if (d > maxValue) {
                    maxValue = d;
                }
                if (d < minValue) {
                    minValue = d;
                }
            }
        }

        if (Math.abs(maxValue) > Math.abs(minValue)) {
            chosen = (int) Math.abs(maxValue) + 5;
        } else {
            chosen = (int) Math.abs(minValue) + 5;
        }
        return (int) chosen;
    }


    public BubbleChart<Number, Number> chartFactory(List<Pair<Tuple, Tuple>> tuples, String title, boolean isTraining) {

        int cof = 1;
        if(tuples.get(0).getValue().getDataVector().length>2){
            cof=2;
        }

        int chosen = calcGraphSize(tuples)*cof;
        final NumberAxis xAxis = new NumberAxis(-chosen, chosen, 1);
        final NumberAxis yAxis = new NumberAxis(-chosen, chosen, 1);
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
            double[] oldVector;
            double[] newVector;
            if (t.getValue().getDataVector().length == 2) {
                oldVector = t.getKey().getDataVector();
                newVector = t.getValue().getDataVector();
            } else {
                oldVector = Utils.multiply(Utils.REDUCE_DIM_MATRIX, t.getKey().getDataVector());
                newVector = Utils.multiply(Utils.REDUCE_DIM_MATRIX, t.getValue().getDataVector());
            }

            if (isTraining) {
                if (t.getKey().getClassNum() == 1) {
                    series1.getData().add(new XYChart.Data(oldVector[0], oldVector[1], t.getKey().getWeight() / 0.005));
                    if (t.getValue().getClassNum() != 1) {
                        series3.getData().add(new XYChart.Data(newVector[0], newVector[1], t.getKey().getWeight() / 0.005));
                    }
                }

                if (t.getKey().getClassNum() == 2) {
                    series2.getData().add(new XYChart.Data(oldVector[0], oldVector[1], t.getKey().getWeight() / 0.005));
                    if (t.getValue().getClassNum() != 2) {
                        series4.getData().add(new XYChart.Data(newVector[0], newVector[1], t.getKey().getWeight() / 0.005));
                    }
                }
            } else {
                if (t.getKey().getClassNum() == 1) {
                    series1.getData().add(new XYChart.Data(t.getKey().getDataVector()[0], t.getKey().getDataVector()[1], t.getKey().getWeight()));
                    if (t.getValue().getClassNum() != 1) {
                        series3.getData().add(new XYChart.Data(newVector[0], newVector[1], t.getKey().getWeight()));
                    }
                }

                if (t.getKey().getClassNum() == 2) {
                    series2.getData().add(new XYChart.Data(oldVector[0], oldVector[1], t.getKey().getWeight()));
                    if (t.getValue().getClassNum() != 2) {
                        series4.getData().add(new XYChart.Data(newVector[0], newVector[1], t.getKey().getWeight()));
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


        int cof = 1;
        if(tuples[0].getDataVector().length>2){
            cof=2;
        }

        int chosen = calcGraphSize(tuples)*cof;

        final NumberAxis xAxis = new NumberAxis(-chosen, chosen, 1);
        final NumberAxis yAxis = new NumberAxis(-chosen, chosen, 1);
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

            double[] oldVector;

            if (t.getDataVector().length == 2) {
                oldVector = t.getDataVector();

            } else {
                oldVector = Utils.multiply(Utils.REDUCE_DIM_MATRIX, t.getDataVector());
                oldVector = Utils.multiply(Utils.EXPAND, oldVector);
            }

            if (t.getClassNum() == 1) {
                series1.getData().add(new XYChart.Data(oldVector[0], oldVector[1], 1));

            }

            if (t.getClassNum() == 2) {
                series2.getData().add(new XYChart.Data(oldVector[0], oldVector[1], 1));
            }
        }
        blc.getData().addAll(series1, series2);
        return blc;
    }


}