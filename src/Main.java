import common.GenericReader;
import common.Utils;
import lab.AdaboostExperiment;
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


      AdaboostExperiment exp1 = new AdaboostExperiment("/weights1.csv", "/data1.csv", 2, 0.9);
      exp1.start();
      Utils.printMatrix(exp1.getConfusionMatrixForTRAINING().get(2));

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
