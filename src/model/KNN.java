package model;

import java.util.*;

public class KNN {
    private static int id;
    private int num;

    /**
     * this array will hold the indexes of the k neighbors
     */
//    private Tuple[] k;

    /**
     * this list will hold the distances, the index of the element will be the same index in the trainingSet
     */
    //private Tuple[] distances;

    private double[][] distancesPair;

    private double alpha;

    private int k_size;

    private int[] classes;

    private double[] weights;

    private int countCurrect;

    private volatile double errorRate;


    //TODO change weights to num of classes
    public KNN(int k, int numofClasses, double[] weights) {
        this.num = id;
        id++;
        //this.k = new Tuple[k];
        k_size = k;
        /**
         * so we don't have to use 0
         */
        classes = new int[numofClasses + 1];

        this.weights = weights;


    }

    public void preprocessing() {
        distancesPair = new double
                [SetStarter.getTrainingSet().length + SetStarter.getTestingSet().length]
                [SetStarter.getTrainingSet().length + SetStarter.getTestingSet().length];

        initilizeDistances(SetStarter.getTrainingSet(), distancesPair);
        initilizeDistances(SetStarter.getTestingSet(), distancesPair);
    }


    public void initilizeDistances(Tuple[] set, double[][] distancesPair) {
        for (int o = 0; o < set.length; o++) {
            Tuple old = set[o];
            for (int j = 0; j < set.length; j++) {
                Tuple newObservation = set[j];
                if (!old.equals(newObservation)) {
                    double sum = 0.0;
                    for (int i = 0; i < old.getDataVector().length; i++) {
                        if (old.equals(newObservation)) {
                            continue;
                        } else {


                            sum += (old.getDataVector()[i] - newObservation.getDataVector()[i]) * (old.getDataVector()[i] - newObservation.getDataVector()[i]) * weights[i];


                        }
                    }
                    distancesPair[o][j] = Math.sqrt(sum);
                }
            }
        }
    }


    public double init(Tuple[] set, Tuple newObservation) {
        PriorityQueue<TupleDistance> distances = new PriorityQueue<>(set.length);
        int[] classes = new int[this.classes.length];
        Tuple[] k = new Tuple[k_size];

        for (int o = 0; o < set.length; o++) {
            Tuple old = set[o];
            if (!old.equals(newObservation)) {
                double sum = 0.0;
                for (int i = 0; i < old.getDataVector().length; i++) {
                    if (old.equals(newObservation)) {
                        continue;
                    } else {


                        sum += (old.getDataVector()[i] - newObservation.getDataVector()[i]) * (old.getDataVector()[i] - newObservation.getDataVector()[i]) * weights[i];


                    }
                }
                /**
                 * setting the distance according to the weight of the sample
                 */
                distances.add(new TupleDistance(old, (Math.sqrt(sum) * old.getWeight())));
            }
        }

//        for (int o = 0; o < set.length; o++) {
//            Tuple old = set[o];
//            if(old.getNum()!=newObservation.getNum()){
//                distances.add(new TupleDistance(old, (distancesPair[o][newObservation.getNum()] * old.getWeight())));
//            }
//
//        }

        for (int i = 0; i < k.length; i++) {
            k[i] = distances.poll().tuple;
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

        //calculating error rate
        if (newObservation.getClassNum() != index) {
            setErrorRate(getErrorRate() + newObservation.getWeight());
            newObservation.getIsCorrectlyClassified()[num] = false;
        } else {
            newObservation.getIsCorrectlyClassified()[num] = true;
        }


        return (double) index;
    }

    public static void resetId() {
        id = 0;
    }

    public void prepareForNextStep() {
        errorRate = 0;
    }

    public int getNum() {
        return num;
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

    public synchronized double getErrorRate() {
        return errorRate;
    }

    public synchronized void setErrorRate(double errorRate) {
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


    @Override
    public String toString() {
        return "KNN{" +
                ", alpha=" + alpha +
                ", k_size=" + k_size +
                ", classes=" + Arrays.toString(classes) +
                ", countCurrect=" + countCurrect +
                ", errorRate=" + errorRate +
                '}';
    }


    private class TupleDistance implements Comparable<TupleDistance> {
        private Tuple tuple;
        private double distance;

        public TupleDistance(Tuple tuple, double distance) {
            this.tuple = tuple;
            this.distance = distance;
        }


        @Override
        public int compareTo(TupleDistance o) {
            return Double.compare(this.distance, o.distance);
        }

        @Override
        public String toString() {
            return "TupleDistance{" +
                    ", distance=" + distance +
                    '}';
        }
    }


    private class PairsDistance implements Comparable<Tuple> {

        private int t1Num;
        private int t2Num;
        private double distance;

        public PairsDistance(int t1Num, int t2Num, double distance) {
            this.t1Num = t1Num;
            this.t2Num = t2Num;
            this.distance = distance;
        }


        @Override
        public int compareTo(Tuple o) {
            return 0;
        }
    }


}
