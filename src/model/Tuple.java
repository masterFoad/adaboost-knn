package model;

import java.util.Arrays;

public class Tuple {

    private double[] dataVector;
    private boolean selected;
    private int classNum;
    private double weight;


    /**
     * distance from current new observation.
     */
    private double distance;


    public Tuple(int dim){
        dataVector = new double[dim];
    }

    public Tuple(double[] dataVector, int classNum) {
        this.dataVector = dataVector;
        this.classNum = classNum;
    }

    public double[] getDataVector() {
        return dataVector;
    }

    public void addPoint(double p){
        this.dataVector[this.dataVector.length-1]=p;
    }


    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected(){
        return selected;
    }

    public int getClassNum() {
        return classNum;
    }

    public void setClassNum(int classNum) {
        this.classNum = classNum;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "dataVector=" + Arrays.toString(dataVector) +
                ", selected=" + selected +
                ", classNum=" + classNum +
                '}';
    }
}
