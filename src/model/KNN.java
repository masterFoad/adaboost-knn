package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KNN {

    /**
     * this array will hold the indexes of the k neighbors
     */
    private Tuple[] k;

    /**
     * this list will hold the distances, the index of the element will be the same index in the trainingSet
     */
    private List<Double> distances;

    private int k_size;

    private double accuracy;

    private int[] classes;

    private double xWeight;
    private double yWeight;

    //TODO change weights to num of classes
    public KNN(int k, int numofClasses, double xWeight, double yWeight) {
        this.k = new Tuple[k];
        this.distances = new ArrayList<>();
        k_size = this.k.length;
        /**
         * so we don't have to use 0
         */
        classes = new int[numofClasses + 1];

        this.xWeight = xWeight;
        this.yWeight = yWeight;
    }

    public int init(Tuple[] testingSet, Tuple newObservation) {

        for (int i = 0; i < testingSet.length; i++) {
            testingSet[i].setDistance(0);
        }

        distance(testingSet, newObservation, (training, newObs) -> {
            for (Tuple old : training) {
                double sum = 0.0;
                for (int i = 0; i < old.getDataVector().length; i++) {
                    sum += (old.getDataVector()[i] * xWeight - newObs.getDataVector()[i] * yWeight) * (old.getDataVector()[i] * xWeight - newObs.getDataVector()[i] * yWeight);
                }
                old.setDistance(Math.sqrt(sum));
            }
        });

        for (Tuple t : testingSet
                ) {
//            System.out.println("distances:" + t.getDistance());
            distances.add(t.getDistance());
        }

        Collections.sort(distances);
        for (int i = 0; i < k.length; i++) {

            for (int j = 0; j < testingSet.length; j++) {
                if (testingSet[j].getDistance() == distances.get(i)) {
                    k[i] = testingSet[j];
                    break;
                }
            }

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
        return index;
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

    @Override
    public int hashCode() {
        return k_size;
    }
}
