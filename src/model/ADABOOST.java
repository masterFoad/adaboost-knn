package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.*;

public class ADABOOST {

    private ArrayList<KNN> classifiers;
    private PriorityQueue<KNN> classifiersRankedByLowestError;
    private ArrayList<KNN> H;
    private Tuple[] tuples;

    private int index;


    public ADABOOST(ArrayList<KNN> classifiers, Tuple[] tuples) {
        this.classifiers = classifiers;
        this.tuples = tuples;
        this.classifiersRankedByLowestError = new PriorityQueue<>(Comparator.comparingDouble(KNN::getErrorRate));
        this.classifiersRankedByLowestError.addAll(this.classifiers);
        this.H = new ArrayList<>();
    }

    public void buildModel() throws InterruptedException {
        System.out.println("printing initial weights:");


        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(4, true);
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        ExecutorService executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, queue, handler);



        for (int i = 0; i < classifiers.size(); i++) {

            System.out.println("step "+i);


            index = 0;
            for (int j = 0; j < 1; j++) {
                //System.out.println("taking classifier "+knn);
                executor.execute(() -> {
                    for (int k = 0; k < 100; k++) {
                        classifiers.get(index).init(tuples, tuples[k]);

                    }
                });
                executor.execute(() -> {
                    for (int k = 100; k < 200; k++) {
                        classifiers.get(index).init(tuples, tuples[k]);

                    }
                });
                executor.execute(() -> {
                    for (int k = 200; k < 300; k++) {
                        classifiers.get(index).init(tuples, tuples[k]);

                    }
                });
                executor.execute(() -> {
                    for (int k = 300; k < tuples.length; k++) {
                        classifiers.get(index).init(tuples, tuples[k]);

                    }
                });
                executor.shutdown();
                while (executor.isTerminated() == false) {
                    Thread.sleep(50);
                }
                index++;
            }




            KNN lowestErrorClassifier = classifiersRankedByLowestError.peek();
            double E = lowestErrorClassifier.getErrorRate();
            double alpha = (1 - E) / (E);
            //double alpha = 0.5 * Math.log((1 - E) / (E));

            Arrays.stream(tuples).forEach(t ->
                    {
                        if (t.isCorrectlyClassified()) {
                            t.setWeight(0.5 * (t.getWeight() / (1 - E)));
                        } else {
                            t.setWeight(0.5 * (t.getWeight() / (E)));
                        }
                    }
            );

            lowestErrorClassifier.setAlpha(alpha);
            H.add(lowestErrorClassifier);


//            double overallErrorRate = 0;
//            for (Tuple t : tuples) {
//                double sumOfPlus1 = 1.0;
//                double sumOfMinos1 = 1.0;
//                for (KNN knn : H) {
//                    double type = knn.init(tuples, t);
//                    if (type > 0) {
//                        sumOfPlus1 *= type * knn.getAlpha();
//                    } else {
//                        sumOfMinos1 *= type * knn.getAlpha();
//                    }
//                }
//                if (sumOfPlus1 > sumOfMinos1) {
//                    if (t.getClassNum() != 1) {
//                        overallErrorRate++;
//                    }
//                }
//                if (sumOfPlus1 < sumOfMinos1) {
//                    if (t.getClassNum() == 1) {
//                        overallErrorRate++;
//                    }
//                }
//
//            }
//            if ((overallErrorRate / (double) tuples.length) == 0) {
//                System.out.println("DONE ALL CORRECTLY CLASSIFIED");
//                System.out.println(i);
//                break;
//            }
        }

//        System.out.println("printing final weights:");
//        for (Tuple t : tuples
//                ) {
//            System.out.println(t);
//        }
    }


    public void checkNewPoint(Tuple t) {
        double overallErrorRate = 0;
        double sumOfPlus1 = 1.0;
        double sumOfMinos1 = 1.0;
        for (KNN knn : H) {
            double type = knn.init(tuples, t);
            if (type > 0) {
                sumOfPlus1 *= type * knn.getAlpha();
            } else {
                sumOfMinos1 *= type * knn.getAlpha();
            }
        }

        if (sumOfPlus1 > sumOfMinos1) {
            if (t.getClassNum() != 1) {

            } else {
                System.out.println("class is :" + 1);
            }
        }
        if (sumOfPlus1 < sumOfMinos1) {
            if (t.getClassNum() == 1) {

            } else {
                System.out.println("class is :" + 2);
            }
        }

    }


    public ArrayList<KNN> getClassifiers() {
        return classifiers;
    }

    public PriorityQueue<KNN> getClassifiersRankedByLowestError() {
        return classifiersRankedByLowestError;
    }

    public ArrayList<KNN> getH() {
        return H;
    }

    public Tuple[] getTuples() {
        return tuples;
    }
}
