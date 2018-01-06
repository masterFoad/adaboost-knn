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


    static int counter = 0;
    static int index = 0;

    public static void main(String args[]) throws InterruptedException {


        long startTime = System.currentTimeMillis();
//        ADABOOST superClassifier = ADABOOST.create("/weights1.csv", "/data1.csv", 2, 0.9);
//        ADABOOST superClassifier = ADABOOST.create("/weights2.csv", "/data2.csv", 2, 0.9);
//        ADABOOST superClassifier = ADABOOST.create("/weights3.csv", "/data3.csv", 3, 0.9);


//        Lab.runExpiriments();

        AdaboostExperiment exp1 = new AdaboostExperiment("/weights3.csv", "/data3.csv", 3, 0.66);
        exp1.start();

        exp1.getSuperClassifier().getPredictedTraining();

        ConfusionMatrix cm = new ConfusionMatrix();

        for (int i = 1; i < exp1.getConfusionMatrixForTRAINING().get(new Pair<>(1, 1)).length; i++) {
            for (int j = 1; j < exp1.getConfusionMatrixForTRAINING().get(new Pair<>(1, 1)).length; j++) {
                cm.increaseValue(i + "", j + "", exp1.getConfusionMatrixForTRAINING().get(new Pair<>(1, 1))[i][j]);
            }
        }


        //Pair<Integer, Integer>, int[][]
//        exp1.getConfusionMatrixForTRAINING().forEach((k,v)->
//        {
//            if(k.getValue().intValue() == 1){
//                counter++;
//            }
//        });
        //TODO maybe we start at 1
        for (int i = 0; i < SetStarter.getMaxFolds(); i++) {
            counter = 0;
            index = i;
            exp1.getConfusionMatrixForTRAINING().forEach((k, v) ->
            {
                if (k.getValue().intValue() == index) {
                    counter++;
                }
                int[][] currentMatrix = exp1.getConfusionMatrixForTRAINING().get(new Pair<>(index, counter));
                ConfusionMatrix cmm = Utils.transformToCM(currentMatrix);

            });
        }

//        int[][] arr = exp1.getConfusionMatrixForTRAINING().get(new Pair<>(1, 1));
//        System.out.println(cm);
//        System.out.println("FP for 1 " + (double) (cm.getRowSum("1") - arr[1][1]) / arr[1][1]);
//        System.out.println("FP for 2 " + (double) (cm.getRowSum("2") - arr[2][2]) / arr[2][2]);
//        System.out.println("FP for 3 " + (double) (cm.getRowSum("3") - arr[3][3]) / arr[3][3]);
//        System.out.println("misclassification rate " + (((double) cm.getTotalSum() - arr[1][1] - arr[2][2] - arr[3][3]) / cm.getTotalSum()));
//        System.out.println("TP for 1 " + cm.getRecallForLabel("1"));
//        System.out.println("TP for 2 " + cm.getRecallForLabel("2"));
//        System.out.println("TP for 3 " + cm.getRecallForLabel("3"));
//        System.out.println("Precision for 1 " + cm.getPrecisionForLabel("1"));
//        System.out.println("Precision for 2 " + cm.getPrecisionForLabel("2"));
//        System.out.println("Precision for 3 " + cm.getPrecisionForLabel("3"));
//        System.out.println("TP accuracy " + cm.getAccuracy());
//        System.out.println("" + cm.printClassDistributionGold());
//        System.out.println("" + cm.printNiceResults());

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


    public void printReport(ConfusionMatrix cm, int[][] arr) {

        System.out.println(cm);
        for (int i = 1; i < arr.length; i++) {
            System.out.println("FP for "+i+" " + (double) (cm.getRowSum(i+"") - arr[i][i]) / arr[i][i]);
        }
        System.out.println("FP for 1 " + (double) (cm.getRowSum("1") - arr[1][1]) / arr[1][1]);
        System.out.println("FP for 2 " + (double) (cm.getRowSum("2") - arr[2][2]) / arr[2][2]);
        System.out.println("FP for 3 " + (double) (cm.getRowSum("3") - arr[3][3]) / arr[3][3]);
//        System.out.println("misclassification rate " + (((double) cm.getTotalSum() - arr[1][1] - arr[2][2] - arr[3][3]) / cm.getTotalSum()));
//        System.out.println("TP for 1 " + cm.getRecallForLabel("1"));
//        System.out.println("TP for 2 " + cm.getRecallForLabel("2"));
//        System.out.println("TP for 3 " + cm.getRecallForLabel("3"));
//        System.out.println("Precision for 1 " + cm.getPrecisionForLabel("1"));
//        System.out.println("Precision for 2 " + cm.getPrecisionForLabel("2"));
//        System.out.println("Precision for 3 " + cm.getPrecisionForLabel("3"));
//        System.out.println("TP accuracy " + cm.getAccuracy());
//        System.out.println("" + cm.printClassDistributionGold());
//        System.out.println("" + cm.printNiceResults());


    }


}
