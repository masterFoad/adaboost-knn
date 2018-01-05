package model;

import java.util.ArrayList;
import java.util.Arrays;

public class Tuple{

    private static int id = 0;
    private int num;
    private double[] dataVector;
    private boolean[] isCorrectlyClassified;
    private int classNum;
    private double weight;


    public Tuple(int dim) {
        dataVector = new double[dim];
    }

    public Tuple(double[] dataVector, int classNum) {
        this.num = id;
        id++;
        this.dataVector = dataVector;
        this.classNum = classNum;
        this.isCorrectlyClassified = new boolean[SetStarter.getWeakClassifiers().length];
    }

    public Tuple createResultClone(double[] dataVector, int classNum){
        Tuple clone = new Tuple(dataVector.length);
        clone.setDataVector(dataVector);
        clone.setClassNum(classNum);
        return clone;
    }

    public double[] getDataVector() {
        return dataVector;
    }

    public void addPoint(double p) {
        this.dataVector[this.dataVector.length - 1] = p;
    }

    public int getClassNum() {
        return classNum;
    }

    public void setClassNum(int classNum) {
        this.classNum = classNum;
    }


    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean[] getIsCorrectlyClassified() {
        return isCorrectlyClassified;
    }

    public void setDataVector(double[] dataVector) {
        this.dataVector = dataVector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;

        Tuple tuple = (Tuple) o;

        return Arrays.equals(dataVector, tuple.dataVector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(dataVector);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "dataVector=" + Arrays.toString(dataVector) +
                ", isCorrectlyClassified=" + isCorrectlyClassified +
                ", classNum=" + classNum +
                ", weight=" + weight +
                '}';
    }

    public int getNum() {
        return num;
    }

    public static void resetId(){
        id=0;
    }

}
