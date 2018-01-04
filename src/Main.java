import common.GenericReader;
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


        //  ArrayList<KNN> knn = GenericReader.buildModel(KNN.class,"/data1.csv", 2, (metaData, numOfClasses) -> GenericReader.createClassifier(metaData, numOfClasses));
//        GenericReader.buildModel("/data1.csv", 0, (metaData, numOfClasses) -> GenericReader.createTuple(metaData));


//

//        for (KNN knn : SetStarter.getWeakClassifiers()) {
//            knn.preprocessing();
//        }

        int totalTotal = 0;


        //44 - random k --- 7 > 0.87
        //50 - k=3 --- 15
        for (int oo = 0; oo < 50; oo++) {



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
            if(superClassifier.runOnTestingSet()>=0.87){
                System.out.println(superClassifier.getFinalModel());
            }




        }

//        System.out.println("total total : "+ ((double)totalTotal/5));

        final List<Runnable> rejected = ThreadPoolCenter.getExecutor().shutdownNow();
        System.out.println(("Rejected tasks: {} "+ rejected.size()));
        //ThreadPoolCenter.closeThreadPool();

        // 2816 5 threads
        // 2575 3 threads
        // 2859 10 threads
        // 4340 2 threads
        // 5269 50 threads
        // 2518 4 threads
    }
}
