package model;

import model.thread_center.ThreadPoolCenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.*;

public class ADABOOST {

    private ArrayList<KNN> classifiers;
    private PriorityQueue<KNN> priorityKNN;
    private ArrayList<FinalModel> FinalModel;
    private Tuple[] tuples;
    private static volatile int index;
    private volatile double overallErrorRate = 0.0;
    private volatile int countTrue = 0;

    public ADABOOST(ArrayList<KNN> classifiers, Tuple[] tuples) {
        this.classifiers = classifiers;
        this.tuples = tuples;
        this.priorityKNN = new PriorityQueue<>(classifiers.size(), Comparator.comparingDouble(KNN::getErrorRate));
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
                //System.out.println(Arrays.stream(tuples).mapToDouble(Tuple::getWeight).sum());
                priorityKNN.clear();
                for (KNN k : classifiers) {
                    k.prepareForNextStep();
                }

                index = 0;
                for (int j = 0; j < classifiers.size(); j++) {
                    CompletableFuture<?>[] futures = initWorkers(classifiers.get(index), tuples).stream()
                            .map(task -> CompletableFuture.runAsync(task, ThreadPoolCenter.getExecutor()))
                            .toArray(CompletableFuture[]::new);


                    CompletableFuture.allOf(futures).join();
                    priorityKNN.add(classifiers.get(index));
                    index++;
                }


                KNN lowestErrorClassifier = priorityKNN.peek();
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

                setOverallErrorRate(0.0);

                for (int j = 0; j < tuples.length; j++) {
                    setOverallErrorRate(getOverallErrorRate() + checkModelValidity(tuples[j]));
                }

                if ((1 - ((overallErrorRate / (double) tuples.length))) == 0.0) {
                    System.out.println("DONE ALL CORRECTLY CLASSIFIED");
                    System.out.println(i);
                    break;
                }
            }

            System.out.println(1 - ((overallErrorRate / (double) tuples.length)));
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


    public synchronized int checkModelValidity(Tuple t) {
        double sumOfPlus1 = 1.0;
        double sumOfMinos1 = 1.0;
        for (FinalModel finalModel : FinalModel) {
            double type = finalModel.knn.init(tuples, t);
            if (type == 1.0) {
                sumOfPlus1 *= finalModel.alpha;
            } else {
                sumOfMinos1 *= finalModel.alpha;
            }
        }

        if (sumOfPlus1 >= sumOfMinos1) {
            if (t.getClassNum() != 1.0) {
                // System.out.println("got it wrong");
                return 0;
            } else {
                // System.out.println("got it correct");
                return 1;
            }
        }
        if (sumOfPlus1 <= sumOfMinos1) {
            if (t.getClassNum() == 1.0) {
                //System.out.println("got it wrong");
                return 0;
            } else {
                //  System.out.println("got it correct");
                return 1;
            }
        }
        return 0;
    }


    public void runOnTestingSet() throws InterruptedException {

        ArrayList<Runnable> runnables = new ArrayList<>();
        setCountTrue(0);
//        runnables.add(() -> {
//            for (int i = 0; i < 100; i++) {
//                setCountTrue(getCountTrue() + checkModelValidity(SetStarter.getTestingSet()[i]));
//            }
//        });
        runnables.add(() -> {
            for (int i = 0; i < SetStarter.getTestingSet().length; i++) {
                setCountTrue(getCountTrue() + checkModelValidity(SetStarter.getTestingSet()[i]));
            }
        });

        CompletableFuture<?>[] futures = runnables.stream()
                .map(task -> CompletableFuture.runAsync(task, ThreadPoolCenter.getExecutor()))
                .toArray(CompletableFuture[]::new);


        CompletableFuture.allOf(futures).join();

        System.out.println(getCountTrue() / (double) SetStarter.getTestingSet().length);

    }

    public synchronized int getCountTrue() {
        return countTrue;
    }

    public synchronized void setCountTrue(int countTrue) {
        this.countTrue = countTrue;
    }

    public static synchronized int getIndex() {
        return index;
    }

    public ArrayList<KNN> getClassifiers() {
        return classifiers;
    }

    public PriorityQueue<KNN> getPriorityKNN() {
        return priorityKNN;
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

    private ArrayList<Runnable> initWorkers(Tuple[] set) {
        ArrayList<Runnable> runnables = new ArrayList<>();
        int start = 0;
        int finish = 10;
        while (finish <= tuples.length) {
            runnables.add(RunnableFactory.currentModelSplit(start, finish, set, this));

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


        public static Runnable currentModelSplit(int start, int finish, Tuple[] set, ADABOOST a) {

            return () -> {
                for (int k = start; k < finish; k++) {
                    a.setOverallErrorRate(a.getOverallErrorRate() + a.checkModelValidity(set[k]));
                }
            };

        }


    }


}
