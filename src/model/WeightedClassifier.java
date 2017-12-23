//package model;
//
//import java.util.Map;
//
//public class WeightedClassifier {
//
//    private KNN classifier;
//    private Map<Tuple, Double> weights;
//
//
//    public WeightedClassifier(KNN classifier, double weight, Map<Tuple, Double> weights) {
//        this.classifier = classifier;
//        this.weights = weights;
//
//    }
//
//
//    public KNN getClassifier() {
//        return classifier;
//    }
//
//    public void setClassifier(KNN classifier) {
//        this.classifier = classifier;
//    }
//
//    public double getWeight() {
//        return weight;
//    }
//
//    public void setWeight(double weight) {
//        this.weight = weight;
//    }
//
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof WeightedClassifier)) return false;
//
//        WeightedClassifier that = (WeightedClassifier) o;
//
//        return classifier != null ? classifier.equals(that.classifier) : that.classifier == null;
//    }
//
//    @Override
//    public int hashCode() {
//        return classifier != null ? classifier.hashCode() : 0;
//    }
//}
