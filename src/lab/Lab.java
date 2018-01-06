package lab;

import java.util.ArrayList;

public class Lab {

    public static ArrayList<AdaboostExperiment> experiments = new ArrayList<>();


    public static void runExpiriments(){
        AdaboostExperiment exp1 = new AdaboostExperiment("/weights1.csv", "/data1.csv", 2, 0.9);
        exp1.start();
        experiments.add(exp1);


        AdaboostExperiment exp2 = new AdaboostExperiment("/weights1.csv", "/data1.csv", 2, 0.9);
        exp2.start();
        experiments.add(exp2);


        AdaboostExperiment exp3 = new AdaboostExperiment("/weights1.csv", "/data1.csv", 2, 0.9);
        exp3.start();
        experiments.add(exp3);

        AdaboostExperiment exp4 = new AdaboostExperiment("/weights2.csv", "/data2.csv", 2, 0.9);
        exp4.start();
        experiments.add(exp4);


        AdaboostExperiment exp5 = new AdaboostExperiment("/weights2.csv", "/data2.csv", 2, 0.9);
        exp5.start();
        experiments.add(exp5);


        AdaboostExperiment exp6 = new AdaboostExperiment("/weights2.csv", "/data2.csv", 2, 0.9);
        exp6.start();
        experiments.add(exp6);


        AdaboostExperiment exp7 = new AdaboostExperiment("/weights3.csv", "/data3.csv", 2, 0.9);
        exp7.start();
        experiments.add(exp7);


        AdaboostExperiment exp8 = new AdaboostExperiment("/weights3.csv", "/data3.csv", 2, 0.9);
        exp8.start();
        experiments.add(exp8);


        AdaboostExperiment exp9 = new AdaboostExperiment("/weights3.csv", "/data3.csv", 2, 0.9);
        exp9.start();
        experiments.add(exp9);
    }




}
