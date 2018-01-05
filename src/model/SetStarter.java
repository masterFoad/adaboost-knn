package model;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Queue;

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
        if (counter == 0) {
            Collections.shuffle(Arrays.asList(tups));
            allData = tups;
            dividor = (int) (tups.length * divisionPercent);
        }

        trainingSet = new Tuple[dividor];
        testingSet = new Tuple[tups.length - dividor];

        int testCounter = 0;
        for (int i = testingSet.length*counter; i < testingSet.length*counter+testingSet.length; i++) {
            testingSet[testCounter++] = allData[i];
        }

        int trainingCounter = 0;
        for (int i = 0; i < testingSet.length*counter; i++) {
            trainingSet[trainingCounter++] = allData[i];
        }
        for (int i = testingSet.length*counter+testingSet.length; i < allData.length; i++) {
            trainingSet[trainingCounter++] = allData[i];
        }


        counter++;
    }

    public static boolean nextFold(){

        trainingSet = new Tuple[dividor];
        testingSet = new Tuple[allData.length - dividor];

        int testCounter = 0;

        int testingStartIndex = testingSet.length*counter;

        if(testingStartIndex >= allData.length){
            return false;
        }
        int testingEndIndex = testingSet.length*counter+testingSet.length;

        if(testCounter > allData.length){
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


        if(testingEndIndex == allData.length){
            return false;
        }
        counter++;

        return true;
    }

    public static Tuple[] getAllData() {
        return allData;
    }

//    public static void divide(Tuple[] tups, double divisionPercent) {
////        if(counter==0){
////            Collections.shuffle(Arrays.asList(tups));
////            allData = tups;
////            dataQ = new ArrayDeque<>();
////            for (int i = 0; i < allData.length; i++) {
////                dataQ.add(allData[i]);
////            }
////        }
//
//
//        int dividor = (int) (tups.length * divisionPercent);
//        //TODO check if the division percent works
//
//        int countTesting = 0;
//        while (countTesting < dividor) {
//            testingSet[countTesting] = dataQ.pop();
//        }
//
//        trainingSet = new Tuple[dividor];
//        testingSet = new Tuple[tups.length - dividor];
//
//        for (int i = 0; i < trainingSet.length; i++) {
//            trainingSet[i] = tups[i];
//        }
//
//        for (int i = 0; i < testingSet.length; i++) {
//            testingSet[i] = tups[i + dividor];
//        }
//
//        counter++;
//
//    }

    public void startAgain() {
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
