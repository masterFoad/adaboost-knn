package model;

import java.util.Arrays;
import java.util.Collections;

public class SetStarter {

    private static Tuple[] trainingSet;
    private static Tuple[] testingSet;
    private static KNN[] weakClassifiers;


    public static void initKNNs(KNN[] knns){

            weakClassifiers = knns;

    }

    public static void divide(Tuple[] tups, double divisionPercent) {
        Collections.shuffle(Arrays.asList(tups));
        int dividor = (int) (tups.length * divisionPercent);
        //TODO check if the division percent works

        trainingSet = new Tuple[dividor];
        testingSet = new Tuple[tups.length - dividor];

        for (int i = 0; i < trainingSet.length; i++) {
            trainingSet[i] = tups[i];
        }

        for (int i = 0; i < testingSet.length; i++) {
            testingSet[i] = tups[i + dividor];
        }

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
