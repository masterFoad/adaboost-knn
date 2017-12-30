package model;

import model.thread_center.ThreadPoolCenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.*;

public class ADABOOST {

    private ArrayList<KNN> classifiers;
    private PriorityQueue<KNN> classifiersRankedByLowestError;
    private ArrayList<FinalModel> FinalModel;
    private Tuple[] tuples;
    private static volatile int index;
    private volatile double overallErrorRate = 0.0;

    public ADABOOST(ArrayList<KNN> classifiers, Tuple[] tuples) {
        this.classifiers = classifiers;
        this.tuples = tuples;
        this.classifiersRankedByLowestError = new PriorityQueue<>(classifiers.size(), Comparator.comparingDouble(KNN::getErrorRate));
        this.FinalModel = new ArrayList<>();
    }

    public void buildModel() throws InterruptedException {

        System.out.println("printing initial weights:");
        for (int i = 0; i < SetStarter.getTrainingSet().length; i++) {
            SetStarter.getTrainingSet()[i].setWeight(1.0 / (double) SetStarter.getTrainingSet().length);
        }
        try {
            for (int i = 0; i < classifiers.size(); i++) {

                System.out.println("step " + i);
                classifiersRankedByLowestError.clear();
                for (KNN k : classifiers) {
                    k.prepareForNextStep();
                }

                index = 0;
                for (int j = 0; j < classifiers.size(); j++) {
                    CompletableFuture<?>[] futures = initWorkers(classifiers.get(index), tuples).stream()
                            .map(task -> CompletableFuture.runAsync(task, ThreadPoolCenter.getExecutor()))
                            .toArray(CompletableFuture[]::new);


                    CompletableFuture.allOf(futures).join();
                    classifiersRankedByLowestError.add(classifiers.get(index));
                    index++;
                }


                KNN lowestErrorClassifier = classifiersRankedByLowestError.peek();
                double E = lowestErrorClassifier.getErrorRate();
                double alpha = (1 - E) / (E);
                //double alpha = 0.5 * Math.log((1 - E) / (E));
                System.out.println(lowestErrorClassifier.getNum());
                Arrays.stream(SetStarter.getTrainingSet()).forEach(t ->
                        {
                            if (t.getIsCorrectlyClassified()[lowestErrorClassifier.getNum()]) {
                                t.setWeight(0.5 * (t.getWeight() / (1 - E)));
                            } else {
                                t.setWeight(0.5 * (t.getWeight() / (E)));
                            }
                        }
                );

                lowestErrorClassifier.setAlpha(alpha);
                FinalModel.add(new FinalModel(lowestErrorClassifier, lowestErrorClassifier.getAlpha()));


                overallErrorRate = 0;
                for (int j = 0; j < tuples.length; j++) {
                    double sumOfPlus1 = 1.0;
                    double sumOfMinos1 = 1.0;
                    for (FinalModel finalModel : FinalModel) {
                        double type = finalModel.knn.init(SetStarter.getTrainingSet(), tuples[j]);
                        if (type > 0) {
                            sumOfPlus1 *= type * finalModel.alpha;
                        } else {
                            sumOfMinos1 *= type * finalModel.alpha;
                        }
                    }
                    if (sumOfPlus1 > sumOfMinos1) {
                        if (tuples[j].getClassNum() != 1) {
                            overallErrorRate++;
                        }
                    }
                    if (sumOfPlus1 < sumOfMinos1) {
                        if (tuples[j].getClassNum() == 1) {
                            overallErrorRate++;
                        }
                    }

                }
                if ((overallErrorRate / (double) tuples.length) == 0.0) {
                    System.out.println("DONE ALL CORRECTLY CLASSIFIED");
                    System.out.println(i);
                    break;
                }
            }

            System.out.println(((overallErrorRate / (double) tuples.length)));
            System.out.println(getFinalModel());
//        System.out.println("printing final weights:");
//        for (Tuple t : tuples
//                ) {
//            System.out.println(t);
//        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void checkNewPoint(Tuple t) {
        double overallErrorRate = 0;
        double sumOfPlus1 = 1.0;
        double sumOfMinos1 = 1.0;
        for (FinalModel finalModel : FinalModel) {
            double type = finalModel.knn.init(tuples, t);
            if (type > 0) {
                sumOfPlus1 *= type * finalModel.knn.getAlpha();
            } else {
                sumOfMinos1 *= type * finalModel.knn.getAlpha();
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


    public void runOnTestingSet() throws InterruptedException {

        ArrayList<Runnable> runnables = new ArrayList<>();

        runnables.add(() -> {
            for (int i = 0; i < 100; i++) {
                checkNewPoint(SetStarter.getTestingSet()[i]);
            }
        });
        runnables.add(() -> {
            for (int i = 100; i < SetStarter.getTestingSet().length; i++) {
                checkNewPoint(SetStarter.getTestingSet()[i]);
            }
        });

        Thread t1 = new Thread(runnables.get(0));
        Thread t2 = new Thread(runnables.get(1));

        t1.start();
        t2.start();

        t1.join();
        t2.join();

    }

    public static synchronized int getIndex() {
        return index;
    }

    public ArrayList<KNN> getClassifiers() {
        return classifiers;
    }

    public PriorityQueue<KNN> getClassifiersRankedByLowestError() {
        return classifiersRankedByLowestError;
    }

    public ArrayList<FinalModel> getFinalModel() {
        return FinalModel;
    }

    public Tuple[] getTuples() {
        return tuples;
    }

    public synchronized double getOverallErrorRate() {
        return overallErrorRate;
    }

    public synchronized void setOverallErrorRate(double overallErrorRate) {
        this.overallErrorRate = overallErrorRate;
    }


    private ArrayList<Runnable> initWorkers(KNN knn, Tuple[] set) {
        ArrayList<Runnable> runnables = new ArrayList<>();
        int start = 0;
        int finish = 10;
        while (finish <= tuples.length) {
            runnables.add(RunnableFactory.create(start, finish, knn, set));

            if (finish == tuples.length)
                break;

            start = finish;
            finish += 10;
            if (finish > tuples.length) {
                finish = tuples.length;
            }

        }

        return runnables;
    }

    private class FinalModel {
        private KNN knn;
        private double alpha;

        public FinalModel(KNN knn, double alpha) {
            this.knn = knn;
            this.alpha = alpha;
        }

        @Override
        public String toString() {
            return "FinalModel{" +
                    "knn=" + knn.getNum() +
                    ", alpha=" + alpha +
                    '}';
        }
    }


    private static class RunnableFactory {

        private RunnableFactory() {
        }


        public static Runnable create(int start, int finish, KNN knn, Tuple[] set) {

            return () -> {
                for (int k = start; k < finish; k++) {
                    knn.init(set, set[k]);

                }
            };

        }


        public static Runnable createTest(int start, int finish, KNN knn, Tuple[] set) {

            return () -> {
                for (int k = start; k < finish; k++) {
                    knn.init(set, set[k]);

                }
            };

        }


    }


}
