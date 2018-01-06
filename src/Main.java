import common.GenericReader;
import common.Utils;
import javafx.util.Pair;
import lab.AdaboostExperiment;
import lab.ConfusionMatrix;
import lab.Lab;
import model.ADABOOST;
import model.KNN;
import model.SetStarter;
import model.Tuple;
import model.thread_center.ThreadPoolCenter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {


    public static void main(String args[]) throws InterruptedException {


        long startTime = System.currentTimeMillis();
//        ADABOOST superClassifier = ADABOOST.create("/weights1.csv", "/data1.csv", 2, 0.9);
//        ADABOOST superClassifier = ADABOOST.create("/weights2.csv", "/data2.csv", 2, 0.9);
//        ADABOOST superClassifier = ADABOOST.create("/weights3.csv", "/data3.csv", 3, 0.9);


//        Lab.runExpiriments();

        AdaboostExperiment exp1 = new AdaboostExperiment("/weights3.csv", "/data3.csv", 3, 0.9);
        exp1.start();

        exp1.getSuperClassifier().getPredictedTraining();

        ConfusionMatrix cm = new ConfusionMatrix();

//        for (int i = 1; i < exp1.getConfusionMatrixForTRAINING().get(new Pair<>(1, 1)).length; i++) {
//            for (int j = 1; j < exp1.getConfusionMatrixForTRAINING().get(new Pair<>(1, 1)).length; j++) {
//                cm.increaseValue(i + "", j + "", exp1.getConfusionMatrixForTRAINING().get(new Pair<>(1, 1))[i][j]);
//            }
//        }
//
//        System.out.println(cm);
//
//        System.out.println(cm.getPrecisionForLabels());
//        System.out.println(cm.getAccuracy());
//        //cm.getRecallForLabel()
//        System.out.println(cm.getRecallForLabels());


//        exp1.getConfusionMatrixForTRAINING().forEach((k, v) -> {
//            if (v[1][1] > 0) {
//                System.out.println("training in step: " + k.getKey().intValue() + " " + k.getValue().intValue());
//                Utils.printMatrix(v);
//                System.out.println("");
//            }
//
//        });
//
//        exp1.getConfusionMatrixForTESTING().forEach((k, v) -> {
//            if (v[1][1] > 0) {
//                System.out.println("testing in step: " + k.intValue());
//                Utils.printMatrix(v);
//                System.out.println("");
//            }
//        });


        //superClassifier.runOnTestingSet();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);

        System.out.println(KNN.counter);
        //superClassifier.runOnTestingSet();


//        }

//        System.out.println("total total : "+ ((double)totalTotal/5));
//        System.out.println(superClassifier.getFinalReport().toString());
        final List<Runnable> rejected = ThreadPoolCenter.getExecutor().shutdownNow();
        System.out.println(("Rejected tasks: {} " + rejected.size()));

        // 2816 5 threads
        // 2575 3 threads
        // 2859 10 threads
        // 4340 2 threads
        // 5269 50 threads
        // 2518 4 threads
    }
}
