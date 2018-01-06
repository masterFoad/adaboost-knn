package model;


import common.GenericReader;
import javafx.util.Pair;
import model.thread_center.ThreadPoolCenter;

import java.util.*;
import java.util.concurrent.*;

public class ADABOOST {

    private ArrayList<KNN> classifiers;
    private PriorityQueue<KNN> priorityKNN;
    private ArrayList<FinalModel> finalModel;
    private Tuple[] tuples;
    private static volatile int index;
    private volatile double overallErrorRate = 0.0;
    private volatile int countTrue = 0;
    private ArrayList<ArrayList<FinalModel>> finalALlModels;
//    private int kIterations;

    private ArrayList<Double> holdLastErrors;

    private ArrayList<Double> saveErrorOfTests;

    private double K;

    private int numberOfFolds;

    /**
     * <<foldIteration, trainingIteratoinInFold>, <tuple, classified as>>
     * <Pair<Pair<Integer,Integer>, Pair<Tuple, Integer>>>
     */
    private ArrayList<Pair<Pair<Integer, Integer>, Pair<Tuple, Integer>>> predictedTraining;
    private ArrayList<Pair<Integer, Pair<Tuple, Integer>>> predictedTesting;

    public ADABOOST(ArrayList<KNN> classifiers, Tuple[] tuples, int numOfClasses) {
        this.classifiers = classifiers;
        this.tuples = tuples;
        this.priorityKNN = new PriorityQueue<>(classifiers.size(), Comparator.comparingDouble(KNN::getErrorRate));
        this.finalModel = new ArrayList<>();
//        this.kIterations = kIterations;
        this.finalALlModels = new ArrayList<>();
        this.K = (double) numOfClasses;
        this.saveErrorOfTests = new ArrayList<>();
        this.predictedTraining = new ArrayList<>();
        this.predictedTesting = new ArrayList<>();
        numberOfFolds = 0;
        holdLastErrors = new ArrayList<>();
    }

    /**
     * if the error rate is bigger than 1-(1/k) means the classifier is no longer a weak classifier, because its worst than random.
     */
    /**
     * will run the adaboost using cross validation
     * the algorithm:
     * while we can still run CV:
     *      for step 1 ... T:
     *          h<-find the weak classifier with the lowest error rate
     *          E<-get the error rate of h
     *          a<-calculate alpha of h
     *          w<-update the weights for the next step
     *          add h to the final model H
     *          test the current model on the training data
     *          break if when the classifiers are no longer weak classifiers
     *          break if the trainingError for the current Model is 0
     *      test current Model H on the testing data
     */
    public void buildModel() {
        try {
            int numberOfFolds = 0;
            while (SetStarter.getCounter() <= SetStarter.getMaxFolds()) {
                numberOfFolds++;
                System.out.println("K fold next");
                SetStarter.resetDataWeights();
                tuples = SetStarter.getTrainingSet();
                int trainingIteration = 0;
                for (int i = 0; i < classifiers.size(); i++) {
                    KNN lowestErrorClassifier = runClassifiers(priorityKNN, classifiers);
                    double E = lowestErrorClassifier.getErrorRate();
                    double alpha = ((1 - E) / (E)) * (K - 1);
                    initNewWeights(lowestErrorClassifier);
                    lowestErrorClassifier.setAlpha(alpha);
                    addToFinalModelH(lowestErrorClassifier, lowestErrorClassifier.getAlpha());
                    setOverallErrorRate(0.0);
                    for (int j = 0; j < tuples.length; j++) {
                        setOverallErrorRate(getOverallErrorRate() + checkModelValidity(tuples[j], predictedTraining, numberOfFolds, trainingIteration));
                    }
                    resetModelErrors();
                    trainingIteration++;
                    System.out.println("training "+finalModel.size()+" "+(1 - ((overallErrorRate / (double) tuples.length))));
                    if ((1 - ((overallErrorRate / (double) tuples.length))) == 0.0 || priorityKNN.stream().allMatch(e -> e.getErrorRate() >= 1 - (1 / K))) {
                        break;
                    }
                    priorityKNN.clear();
                }
                double testingError = runOnTestingSet(numberOfFolds);
                System.out.println("test "+finalModel.size()+" "+testingError);
                saveErrorOfTests.add(testingError);
                if (!SetStarter.nextFold()) {
                    break;
                }
            }

                System.out.println(saveErrorOfTests.stream().mapToDouble(e -> e.doubleValue()).average());
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        /**
         * setting up weights for next step.
         *
         * @param lowestErrorClassifier
         */

    private void initNewWeights(KNN lowestErrorClassifier) {
        Arrays.stream(SetStarter.getTrainingSet()).forEach(t ->
                {
                    if (t.getIsCorrectlyClassified()[lowestErrorClassifier.getNum()]) {
                        t.setWeight((1 / K) * (t.getWeight() / (1 - lowestErrorClassifier.getErrorRate())));
                    } else {
                        t.setWeight(((K - 1) / K) * (t.getWeight() / (lowestErrorClassifier.getErrorRate())));
                    }
                }
        );
    }

    /**
     * ADABOOST factory method, returns a super classifier for the given data.
     *
     * @param weightsPath:     path to the weights data set - used to create the weighted KNNs
     * @param dataPath:        path of the training data for the adaboost
     * @param numberOfClasses: number of the different classes that exists in the training set
     * @param cvPercent:       folding percent, for example 0.9 will take 0.1 of the data for testing and 0.9 for training
     *                         each time - meaning for 10 folds.
     * @return ADABOOST
     */
    public static ADABOOST create(String weightsPath, String dataPath, int numberOfClasses, double cvPercent) {

        SetStarter.initKNNs(GenericReader.init(weightsPath, numberOfClasses, (metaData, numOfClasses) -> GenericReader.createClassifier(metaData, numOfClasses)).toArray(new KNN[0]));
        // reading the data from csv
        SetStarter
                .initialDivision(
                        GenericReader.init(dataPath,
                                0,
                                (metaData, numOfClasses) -> GenericReader.createTuple(metaData)).toArray(new Tuple[0]),
                        cvPercent);

        return new ADABOOST(
                new ArrayList<>(Arrays.asList(SetStarter.getWeakClassifiers()))
                , SetStarter.getTrainingSet()
                , numberOfClasses);
    }

    /**
     * run classifiers on training set, and return the classifier with the lowest error rate;
     * multithreaded; will run the classifiers on the trainng set in parallel.
     *
     * @param priorityKNN
     * @param classifiers
     * @return
     */
    private KNN runClassifiers(PriorityQueue<KNN> priorityKNN, List<KNN> classifiers) {
        priorityKNN.clear();
        for (KNN k : classifiers) {
            k.prepareForNextStep();
        }

//        index = 0;
        for (int j = 0; j < classifiers.size(); j++) {
            CompletableFuture<?>[] futures = initWorkers(classifiers.get(j), tuples).stream()
                    .map(task -> CompletableFuture.runAsync(task, ThreadPoolCenter.getExecutor()))
                    .toArray(CompletableFuture[]::new);


            CompletableFuture.allOf(futures).join();
                    for (int k = 0; k < tuples.length; k++) {
                        classifiers.get(index).init(tuples, tuples[k]);

                    }
            priorityKNN.add(classifiers.get(j));
//            index++;
        }


        return priorityKNN.peek();
    }

    /**
     * adding weak classifier to final model
     * @param knn
     * @param alpha
     */
    private void addToFinalModelH(KNN knn, double alpha){
        FinalModel fm = new FinalModel(knn.getClone() , alpha);
        finalModel.add(fm);
    }


    /**
     * returns the class of the given tuple as classified by the current model.
     *
     * @param t
     * @return class as int (1,..,k)
     */
    public int classifyTuple(Tuple t) {
        double[] finalSums = new double[(int) K + 1];
        Arrays.setAll(finalSums, e -> 1.0);
        for (FinalModel finalModel : finalModel) {
            finalSums[finalModel.knn.init(tuples, t)] *= finalModel.alpha;
        }


        double max = 0;
        int index = 0;
        for (int i = 1; i < finalSums.length; i++) {
            if (finalSums[i] > max) {
                max = finalSums[i];
                index = i;
            }
        }
        return index;
    }

    /**
     * check the current model validity on tuple
     *
     * @param t
     * @return 1 if the tuple is correctly classified, else 0
     */
    public synchronized int checkModelValidity(Tuple t, ArrayList<Pair<Integer, Pair<Tuple, Integer>>> predicted, int numberOfFolds) {

        int index = classifyTuple(t);

        predicted.add(new Pair<>(numberOfFolds, new Pair<>(t, index)));

        if (t.getClassNum() != index) {
            return 0;
        } else {
            return 1;
        }

    }

    /**
     * check the current model validity on tuple
     *
     * @param t
     * @return 1 if the tuple is correctly classified, else 0
     */
    public synchronized int checkModelValidity(Tuple t, ArrayList<Pair<Pair<Integer, Integer>, Pair<Tuple, Integer>>> predicted, int numberOfFolds, int trainingIteration) {

        int index = classifyTuple(t);

        predicted.add(new Pair<>(new Pair<>(numberOfFolds, trainingIteration), new Pair<>(t, index)));

        if (t.getClassNum() != index) {
            return 0;
        } else {
            return 1;
        }

    }


    /**
     * run the current model on the current testing set
     *
     * @return
     */
    public double runOnTestingSet(int numberOfFolds) {
        setCountTrue(0);
        for (int i = 0; i < SetStarter.getTestingSet().length; i++) {
            setCountTrue(getCountTrue() + checkModelValidity(SetStarter.getTestingSet()[i], predictedTesting, numberOfFolds));
        }

        return 1.0 - (double) getCountTrue() / (double) SetStarter.getTestingSet().length;
    }

    /**
     * start working on parallel on the data
     *
     * @param knn
     * @param set
     * @return returns runnables equal to the number of tuples in the data, so that it can be divided between the threads.
     */
    private ArrayList<Runnable> initWorkers(KNN knn, Tuple[] set) {
        ArrayList<Runnable> runnables = new ArrayList<>();
        int start = 0;
        int finish = 1;
        while (finish <= tuples.length) {
            runnables.add(RunnableFactory.create(start, finish, knn, set));

            if (finish == tuples.length)
                break;

            start = finish;
            finish += 1;
            if (finish > tuples.length) {
                finish = tuples.length;
            }

        }

        return runnables;
    }

    public synchronized int getCountTrue() {
        return countTrue;
    }

    public synchronized void setCountTrue(int countTrue) {
        this.countTrue = countTrue;
    }

    public ArrayList<FinalModel> getFinalModel() {
        return finalModel;
    }

    public synchronized double getOverallErrorRate() {
        return overallErrorRate;
    }

    public synchronized void setOverallErrorRate(double overallErrorRate) {
        this.overallErrorRate = overallErrorRate;
    }


    public ArrayList<Pair<Pair<Integer, Integer>, Pair<Tuple, Integer>>> getPredictedTraining() {
        return predictedTraining;
    }

    public ArrayList<Pair<Integer, Pair<Tuple, Integer>>> getPredictedTesting() {
        return predictedTesting;
    }

    /**
     * to hold the final model
     */
    private class FinalModel {
        private KNN knn;
        private double alpha;

        public FinalModel(KNN knn, double alpha) {
            this.knn = knn;
            this.alpha = alpha;
        }

        @Override
        public String toString() {
            return "finalModel{" +
                    "knn=" + knn.getNum() +
                    "k=" + this.knn.getK_size() +
                    ", alpha=" + alpha +
                    '}';
        }
    }


    private void resetModelErrors(){
        for(FinalModel fm : finalModel){
            fm.knn.setErrorRate(0.0);
        }
    }

//    public StringBuilder getFinalReport() {
//        StringBuilder report = new StringBuilder();
//
//        report.append("Final Report:");
//        report.append(System.getProperty("line.separator"));
//        report.append("Training data size: " + SetStarter.getTrainingSet().length)
//                .append(System.getProperty("line.separator"));
//        report.append("Final Model: ").append(System.getProperty("line.separator")).append(getFinalModel().toString());
//        report.append(System.getProperty("line.separator"));
//        report.append("Number of correctly classified training data class 1:").append(System.getProperty("line.separator"));
//        report.append(finalResultsForTraining.stream().filter(e -> e.getValue().getClassNum() == 1 && e.getKey().getClassNum() == e.getValue().getClassNum()).count());
//        report.append(System.getProperty("line.separator"));
//        report.append("Number of incorrectly classified training data class 1:").append(System.getProperty("line.separator"));
//        report.append(finalResultsForTraining.stream().filter(e -> e.getValue().getClassNum() == 1 && e.getKey().getClassNum() != e.getValue().getClassNum()).count());
//
//
//        report.append(System.getProperty("line.separator"));
//        report.append("Number of correctly classified training data class 2:").append(System.getProperty("line.separator"));
//        report.append(finalResultsForTraining.stream().filter(e -> e.getValue().getClassNum() == 2 && e.getKey().getClassNum() == e.getValue().getClassNum()).count());
//        report.append(System.getProperty("line.separator"));
//        report.append("Number of incorrectly classified training data class 2:").append(System.getProperty("line.separator"));
//        report.append(finalResultsForTraining.stream().filter(e -> e.getValue().getClassNum() == 2 && e.getKey().getClassNum() != e.getValue().getClassNum()).count());
//
//        report.append(System.getProperty("line.separator"));
//        report.append("Training Error Rate: " + (1 - getOverallErrorRate() / (double) SetStarter.getTrainingSet().length));
//
//        report.append(System.getProperty("line.separator"));
//        report.append("Testing: ");
//        report.append(System.getProperty("line.separator"));
//        report.append("testing data size: " + SetStarter.getTestingSet().length)
//                .append(System.getProperty("line.separator"));
//        report.append("Number of correctly classified testing data class 1:").append(System.getProperty("line.separator"));
//        report.append(finalResultsForTesting.stream().filter(e -> e.getValue().getClassNum() == 1 && e.getKey().getClassNum() == e.getValue().getClassNum()).count());
//        report.append(System.getProperty("line.separator"));
//        report.append("Number of incorrectly classified testing data class 1:").append(System.getProperty("line.separator"));
//        report.append(finalResultsForTesting.stream().filter(e -> e.getValue().getClassNum() == 1 && e.getKey().getClassNum() != e.getValue().getClassNum()).count());
//
//
//        report.append(System.getProperty("line.separator"));
//        report.append("Number of correctly classified testing data class 2:").append(System.getProperty("line.separator"));
//        report.append(finalResultsForTesting.stream().filter(e -> e.getValue().getClassNum() == 2 && e.getKey().getClassNum() == e.getValue().getClassNum()).count());
//        report.append(System.getProperty("line.separator"));
//        report.append("Number of incorrectly classified testing data class 2:").append(System.getProperty("line.separator"));
//        report.append(finalResultsForTesting.stream().filter(e -> e.getValue().getClassNum() == 2 && e.getKey().getClassNum() != e.getValue().getClassNum()).count());
//
//        report.append(System.getProperty("line.separator"));
//        report.append("Testing Error Rate: " + (1 - countTrue / (double) SetStarter.getTestingSet().length));
//
//
//        return report;
//    }


    /**
     * used to divide the data to n runnables
     */
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


    }


}
