package model;

import java.util.*;

public class KNN {

    /**
     * this array will hold the indexes of the k neighbors
     */
    private Tuple[] k;

    /**
     * this list will hold the distances, the index of the element will be the same index in the trainingSet
     */
    private Tuple[] distances;

    private double alpha;

    private int k_size;

    private double accuracy;

    private int[] classes;

    private double xWeight;
    private double yWeight;

    private int countCurrect;

    private double errorRate;

    //TODO change weights to num of classes
    public KNN(int k, int numofClasses, double xWeight, double yWeight) {
        this.k = new Tuple[k];
        k_size = this.k.length;
        /**
         * so we don't have to use 0
         */
        classes = new int[numofClasses + 1];

        this.xWeight = xWeight;
        this.yWeight = yWeight;
    }

    public double init(Tuple[] testingSet, Tuple newObservation) {
        this.distances = new Tuple[testingSet.length];
        classes = new int[classes.length];
        for (int i = 0; i < testingSet.length; i++) {
            testingSet[i].setDistance(0);
        }

        distance(testingSet, newObservation, (training, newObs) -> Arrays.stream(training).filter(old->!old.equals(newObs)).forEach(
                old -> {
                    //System.out.println("setting up distances");
                    double sum = 0.0;
                    for (int i = 0; i < old.getDataVector().length; i++) {
                        if(old.equals(newObs)){
                            continue;
                        }else {
                            sum += (old.getDataVector()[i] * xWeight - newObs.getDataVector()[i] * yWeight) * (old.getDataVector()[i] * xWeight - newObs.getDataVector()[i] * yWeight);
                        }
                    }
                    /**
                     * setting the distance according to the weight of the sample
                     */
                    old.setDistance(Math.sqrt(sum) * old.getWeight());
                }
        ));

        int counter = 0;
        for (Tuple t : testingSet) {
            distances[counter++] = t;
        }
        //System.out.println("sorting and finding k neighbors");

        for(Tuple d : distances){
            if(Double.isNaN(d.getDistance()));
            System.out.println("BUSTEEEEED");
        }

        Arrays.sort(distances);

        for (int i = 0; i < k.length; i++) {
            k[i] = distances[i];
        }

        for (int i = 0; i < k.length; i++) {
            classes[k[i].getClassNum()]++;
        }
        int max = 0;
        int index = 0;
        for (int i = 1; i < classes.length; i++) {
            if (classes[i] > max) {

                max = classes[i];
                index = i;
            }
        }
        accuracy = ((double) classes[index]) / k_size;
        if (newObservation.getClassNum() == index) {
            countCurrect++;
        }


        //calculating error rate
        if (newObservation.getClassNum() != index) {
            errorRate += newObservation.getWeight();
            newObservation.setCorrectlyClassified(false);
        } else {
            newObservation.setCorrectlyClassified(true);
        }


        return (double) index;
    }

    private void distance(Tuple[] observations, Tuple newObservation, Distancable euclideanDistanceToAll) {
        euclideanDistanceToAll.measure(observations, newObservation);

    }


    public double getAccuracy() {
        return accuracy;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KNN)) return false;

        KNN knn = (KNN) o;

        return k_size == knn.k_size;
    }


    public int getCountCurrect() {
        return countCurrect;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public int hashCode() {
        return k_size;
    }

}
