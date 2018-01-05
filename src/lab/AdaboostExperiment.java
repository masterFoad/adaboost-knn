package lab;

import javafx.util.Pair;
import model.ADABOOST;
import model.SetStarter;
import model.Tuple;

import java.util.HashMap;
import java.util.Map;

public class AdaboostExperiment {

    private Map<Integer, int[][]> confusionMatrixForFoldTRAINING;
    private Map<Integer, int[][]> confusionMatrixForFoldTESTING;
    private ADABOOST superClassifier;
    private int numOfClasses;


    public AdaboostExperiment(String weightsPath, String dataPath, int numberOfClasses, double cvPercent) {


        this.superClassifier = ADABOOST.create(weightsPath, dataPath, numberOfClasses, cvPercent);
        this.numOfClasses = numberOfClasses;
        confusionMatrixForFoldTRAINING = new HashMap<>();
        confusionMatrixForFoldTESTING = new HashMap<>();


        //+1 for good measures
        int foldSize = (int) (SetStarter.getAllData().length / (SetStarter.getAllData().length * cvPercent)) + 1;
        for (int i = 0; i < foldSize + 1; i++) {
            confusionMatrixForFoldTRAINING.put(i, new int[numberOfClasses + 1][numberOfClasses + 1]);
            confusionMatrixForFoldTESTING.put(i, new int[numberOfClasses + 1][numberOfClasses + 1]);
        }

    }

    public void start() {
        superClassifier.buildModel();
    }

    public Map<Integer, int[][]> getConfusionMatrixForTRAINING() {
        confusionMatrixForFoldTRAINING.forEach((k, v) -> {
            for (int i = 1; i < numOfClasses + 1; i++) {
                v[0][i] = i;
                v[i][0] = i;
            }
        });

        for (Pair<Integer, Pair<Tuple, Integer>> foldTupleResult :
                superClassifier.getPredictedTraining()) {
            int foldNum = foldTupleResult.getKey().intValue();
            int actualClass = foldTupleResult.getValue().getKey().getClassNum();
            int predictedClass = foldTupleResult.getValue().getValue();
            if (confusionMatrixForFoldTRAINING.get(foldNum)[actualClass][predictedClass] == 0) {
                confusionMatrixForFoldTRAINING.get(foldNum)[actualClass][predictedClass]++;
            } else {
                confusionMatrixForFoldTRAINING.get(foldNum)[actualClass][predictedClass] =
                        confusionMatrixForFoldTRAINING.get(foldNum)[actualClass][predictedClass] + 1;
            }


        }

        return confusionMatrixForFoldTRAINING;
    }

    public Map<Integer, int[][]> getConfusionMatrixForTESTING() {

        confusionMatrixForFoldTESTING.forEach((k, v) -> {
            for (int i = 1; i < numOfClasses + 1; i++) {
                v[0][i] = i;
                v[i][0] = i;
            }
        });

        for (Pair<Integer, Pair<Tuple, Integer>> foldTupleResult :
                superClassifier.getPredictedTesting()) {
            int foldNum = foldTupleResult.getKey().intValue();
            int actualClass = foldTupleResult.getValue().getKey().getClassNum();
            int predictedClass = foldTupleResult.getValue().getValue();
            if (confusionMatrixForFoldTESTING.get(foldNum)[actualClass][predictedClass] == 0) {
                confusionMatrixForFoldTESTING.get(foldNum)[actualClass][predictedClass]++;
            } else {
                confusionMatrixForFoldTESTING.get(foldNum)[actualClass][predictedClass] =
                        confusionMatrixForFoldTESTING.get(foldNum)[actualClass][predictedClass] + 1;
            }


        }
        return confusionMatrixForFoldTESTING;
    }


}
