import lab.AdaboostExperiment;
import lab.ConfusionMatrix;
//import lab.Lab;
import lab.Lab;
import model.KNN;
import model.thread_center.ThreadPoolCenter;


import java.util.List;

public class Main {


    static int counter = 0;
    static int index = 0;

    public static void main(String args[]) throws InterruptedException {


        long startTime = System.currentTimeMillis();
//        AdaboostExperiment exp1 = new AdaboostExperiment("/weights1.csv", "/data1.csv", 2, 0.9);
        AdaboostExperiment exp1 = new AdaboostExperiment("/weights2.csv", "/data2.csv", 2, 0.96);
//        AdaboostExperiment exp1 = new AdaboostExperiment("/weights3.csv", "/data3.csv", 3, 0.9);


        System.out.println("Report for data3 using 66% training data nad 34% testing, 2 folds CV");

        exp1.start();
//        exp1.getSuperClassifier().getPredictedTraining();


        Lab l = new Lab(exp1);
//        l.getAllResults();


        for (Lab.Result r :
                l.getAllResults()) {
            System.out.println(r);
            System.out.println(l.getAveragesForTraining3classes(r));
        }

        for (Lab.Result r :
                l.getAllResultsForTesting()) {
            System.out.println(r);
            System.out.println(l.getAveragesForTesting3classes(r));
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("total run time in millis: "+totalTime);
//        System.out.println("KNN uses:"+ KNN.counter);
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
