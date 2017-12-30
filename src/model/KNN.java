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

    private double alpha;

    private int k_size;

    private double accuracy;

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

    public double init(Tuple[] set, Tuple newObservation) {
        //List<TupleDistance> distancesA = Collections.synchronizedList(new ArrayList<TupleDistance>());
//        TupleDistance[] distances = new TupleDistance[set.length-1];
        PriorityQueue<TupleDistance> distances = new PriorityQueue<>(set.length);
        int[] classes = new int[this.classes.length];
        Tuple[] k = new Tuple[k_size];

        int counter = 0;
        for (int o = 0; o < set.length; o++) {
            Tuple old = set[o];
            if (!old.equals(newObservation)) {
                //System.out.println("setting up distances");
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
//                distances[counter++]=new TupleDistance(old, (Math.sqrt(sum) * old.getWeight()));
            }
        }

//        for (int i = 0; i < distancesA.size(); i++) {
//            distances[i]=distancesA.get(i);
//        }
       // Arrays.sort(distances);



        for (int i = 0; i < k.length; i++) {
            k[i] = distances.poll().tuple;
           // k[i] = distances[i].tuple;
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
//        accuracy = ((double) classes[index]) / k_size;
//        if (newObservation.getClassNum() == index) {
//            countCurrect++;
//        }


        //calculating error rate
        if (newObservation.getClassNum() != index) {
            setErrorRate(getErrorRate() + newObservation.getWeight());
            newObservation.getIsCorrectlyClassified()[num] = false;
        } else {
            newObservation.getIsCorrectlyClassified()[num] = true;
        }

//        if (getErrorRate() == 0) {
//            System.out.println(weights[0] + " " + weights[1]+" "+index);
//            for (Tuple t : k) {
//                System.out.println(t);
//            }
//        }


        return (double) index;
    }

    private void distance(Tuple[] observations, Tuple newObservation, Distancable euclideanDistanceToAll) {
        euclideanDistanceToAll.measure(observations, newObservation);

    }

    public void prepareForNextStep() {
        errorRate = 0;
    }

    public int getNum() {
        return num;
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
                ", accuracy=" + accuracy +
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
    }


}
