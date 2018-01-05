package model;

import java.util.Arrays;
import java.util.Collections;


public class SetStarter {

    private static Tuple[] trainingSet;
    private static Tuple[] testingSet;
    private static KNN[] weakClassifiers;
    private static Tuple[] allData;
    private static int counter = 0;
    private static int dividor;

    public static void initKNNs(KNN[] knns) {

        weakClassifiers = knns;

    }


    public static void initialDivision(Tuple[] tups, double divisionPercent) {
        resetSets();
        if (counter == 0) {
            Collections.shuffle(Arrays.asList(tups));
            allData = tups;
            dividor = (int) (tups.length * divisionPercent);
        }

        trainingSet = new Tuple[dividor];
        testingSet = new Tuple[tups.length - dividor];

        int testCounter = 0;
        for (int i = testingSet.length * counter; i < testingSet.length * counter + testingSet.length; i++) {
            testingSet[testCounter++] = allData[i];
        }

        int trainingCounter = 0;
        for (int i = 0; i < testingSet.length * counter; i++) {
            trainingSet[trainingCounter++] = allData[i];
        }
        for (int i = testingSet.length * counter + testingSet.length; i < allData.length; i++) {
            trainingSet[trainingCounter++] = allData[i];
        }


        counter++;
    }

    public static boolean nextFold() {

        trainingSet = new Tuple[dividor];
        testingSet = new Tuple[allData.length - dividor];

        int testCounter = 0;

        int testingStartIndex = testingSet.length * counter;

        if (testingStartIndex >= allData.length) {
            return false;
        }
        int testingEndIndex = testingSet.length * counter + testingSet.length;

        if (testCounter > allData.length) {
            testCounter = allData.length;
        }


        for (int i = testingStartIndex; i < testingEndIndex; i++) {
            testingSet[testCounter++] = allData[i];
        }

        int trainingCounter = 0;
        for (int i = 0; i < testingStartIndex; i++) {
            trainingSet[trainingCounter++] = allData[i];
        }
        for (int i = testingEndIndex; i < allData.length; i++) {
            trainingSet[trainingCounter++] = allData[i];
        }


        if (testingEndIndex == allData.length) {
            return false;
        }
        counter++;

        return true;
    }

    public static Tuple[] getAllData() {
        return allData;
    }

    public static void resetDataWeights() {
        for (int i = 0; i < SetStarter.getTrainingSet().length; i++) {
            SetStarter.getTrainingSet()[i].setWeight(1.0 / (double) SetStarter.getAllData().length);
        }
        for (int i = 0; i < SetStarter.getTestingSet().length; i++) {
            SetStarter.getTestingSet()[i].setWeight(1.0 / (double) SetStarter.getAllData().length);
        }
    }

    public static void resetSets() {
        counter = 0;
    }

    public static Tuple[] getTrainingSet() {
        return trainingSet;
    }

    public static Tuple[] getTestingSet() {
        return testingSet;
    }

    public static KNN[] getWeakClassifiers() {
        return weakClassifiers;
    }
}
