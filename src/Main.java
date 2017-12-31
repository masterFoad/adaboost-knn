import common.GenericReader;
import model.ADABOOST;
import model.KNN;
import model.SetStarter;
import model.Tuple;
import model.thread_center.ThreadPoolCenter;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {


    public static void main(String args[]) throws InterruptedException {



      //  ArrayList<KNN> knn = GenericReader.buildModel(KNN.class,"/data1.csv", 2, (metaData, numOfClasses) -> GenericReader.createClassifier(metaData, numOfClasses));
//        GenericReader.buildModel("/data1.csv", 0, (metaData, numOfClasses) -> GenericReader.createTuple(metaData));


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


        for (KNN knn: SetStarter.getWeakClassifiers()) {
            knn.preprocessing();
        }


        long startTime = System.currentTimeMillis();
        ADABOOST superClassifier = new ADABOOST(
                new ArrayList<>(Arrays.asList(SetStarter.getWeakClassifiers())),
                trainingSet);

        superClassifier.buildModel();
        superClassifier.runOnTestingSet();
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);


        superClassifier.runOnTestingSet();
        ThreadPoolCenter.closeThreadPool();






    }
}
