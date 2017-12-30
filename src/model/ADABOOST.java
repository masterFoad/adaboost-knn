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

    private static volatile int index;

    private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(7, true);
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
    private ExecutorService executor = new ThreadPoolExecutor(7, 7, 0L, TimeUnit.MILLISECONDS, queue, handler);
    //executorprivate ArrayList<Thread> threads = new ArrayList<>();

    public ADABOOST(ArrayList<KNN> classifiers, Tuple[] tuples) {
        this.classifiers = classifiers;
        this.tuples = tuples;
        this.classifiersRankedByLowestError = new PriorityQueue<>(Comparator.comparingDouble(KNN::getErrorRate));
        this.classifiersRankedByLowestError.addAll(this.classifiers);
        this.H = new ArrayList<>();
    }

    public void buildModel() throws InterruptedException {

        System.out.println("printing initial weights:");
        for (int i = 0; i < SetStarter.getTrainingSet().length; i++) {
            SetStarter.getTrainingSet()[i].setWeight(1.0 / (double) SetStarter.getTrainingSet().length);
        }
        try {
            for (int i = 0; i < classifiers.size(); i++) {

                System.out.println("step " + i);

                for (KNN k : classifiers){
                    k.prepareForNextStep();
                }


                for (int j = 0; j < classifiers.size(); j++) {
                    //System.out.println("taking classifier "+knn);
                    ArrayList<Runnable> runnables = new ArrayList<>();

                    index = 0;
                runnables.add(() -> {
                    for (int k = 0; k < 50; k++) {
                        classifiers.get(index).init(tuples, tuples[k]);

                    }
                });
                runnables.add(() -> {
                    for (int k = 50; k < 100; k++) {
                        classifiers.get(index).init(tuples, tuples[k]);

                    }
                });
                runnables.add(() -> {
                    for (int k = 100; k < 150; k++) {
                        classifiers.get(index).init(tuples, tuples[k]);

                    }
                });
                runnables.add(() -> {
                    for (int k = 150; k < 200; k++) {
                        classifiers.get(index).init(tuples, tuples[k]);

                    }
                });
                runnables.add(() -> {
                    for (int k = 200; k < 250; k++) {
                        classifiers.get(index).init(tuples, tuples[k]);

                    }
                });
                runnables.add(() -> {
                    for (int k = 250; k < 300; k++) {
                        classifiers.get(index).init(tuples, tuples[k]);

                    }
                });
                runnables.add(() -> {
                    for (int k = 350; k < tuples.length; k++) {
                        classifiers.get(index).init(tuples, tuples[k]);

                    }
                });

//                    Thread[] threads = new Thread[runnables.size()];
//
//                    int counter = 0;
//                    for (Runnable r : runnables) {
//                        executor.execute(r);
//                    }
                    CompletableFuture<?>[] futures = runnables.stream()
                            .map(task -> CompletableFuture.runAsync(task, executor))
                            .toArray(CompletableFuture[]::new);
                    CompletableFuture.allOf(futures).join();
                   // executor.shutdown();

//
//                    for (Thread t : threads) {
//                        t.start();
//                    }
//
//                    for (Thread t : threads) {
//                        t.join();
//                    }

                    index++;
                }


                KNN lowestErrorClassifier = classifiersRankedByLowestError.peek();
                double E = lowestErrorClassifier.getErrorRate();
                double alpha = (1 - E) / (E);
                //double alpha = 0.5 * Math.log((1 - E) / (E));

                Arrays.stream(SetStarter.getTrainingSet()).forEach(t ->
                        {
                            System.out.println(lowestErrorClassifier.getNum());
                            if (t.getIsCorrectlyClassified()[lowestErrorClassifier.getNum()]) {
                                t.setWeight(0.5 * (t.getWeight() / (1 - E)));
                            } else {
                                t.setWeight(0.5 * (t.getWeight() / (E)));
                            }
                        }
                );

                lowestErrorClassifier.setAlpha(alpha);
                H.add(lowestErrorClassifier);


                double overallErrorRate = 0;
                for (Tuple t : tuples) {
                    double sumOfPlus1 = 1.0;
                    double sumOfMinos1 = 1.0;
                    for (KNN knn : H) {
                        double type = knn.init(SetStarter.getTrainingSet(), t);
                        if (type > 0) {
                            sumOfPlus1 *= type * knn.getAlpha();
                        } else {
                            sumOfMinos1 *= type * knn.getAlpha();
                        }
                    }
                    if (sumOfPlus1 > sumOfMinos1) {
                        if (t.getClassNum() != 1) {
                            overallErrorRate++;
                        }
                    }
                    if (sumOfPlus1 < sumOfMinos1) {
                        if (t.getClassNum() == 1) {
                            overallErrorRate++;
                        }
                    }

                }
                if ((overallErrorRate / (double) tuples.length) == 0) {
                    System.out.println("DONE ALL CORRECTLY CLASSIFIED");
                    System.out.println(i);
                    break;
                }
            }

//        System.out.println("printing final weights:");
//        for (Tuple t : tuples
//                ) {
//            System.out.println(t);
//        }
        }catch (Exception e){
            e.printStackTrace();
        }
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

    public ArrayList<KNN> getH() {
        return H;
    }

    public Tuple[] getTuples() {
        return tuples;
    }


    public void closeThreadPool(){
        executor.shutdown();
    }
}
