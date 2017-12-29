import common.GenericReader;
import model.ADABOOST;
import model.KNN;
import model.SetStarter;
import model.Tuple;

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


        long startTime = System.currentTimeMillis();
        ADABOOST superClassifier = new ADABOOST(
                new ArrayList<>(Arrays.asList(SetStarter.getWeakClassifiers())),
                trainingSet);

        superClassifier.buildModel();
        testingSet[3].setWeight(1);
        System.out.println(testingSet[3].toString());
        superClassifier.checkNewPoint(testingSet[3]);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);






    }
}
