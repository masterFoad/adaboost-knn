package lab;

import javafx.util.Pair;
import model.ADABOOST;
import model.SetStarter;
import model.Tuple;

import java.util.HashMap;
import java.util.Map;

public class AdaboostExperiment {

    private Map<Pair<Integer, Integer>, int[][]> confusionMatrixForFoldTRAINING;
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
        for (int i = 0; i < 30; i++) {
            confusionMatrixForFoldTESTING.put(i, new int[numberOfClasses + 1][numberOfClasses + 1]);
        }


    }

    public void start() {
        superClassifier.buildModel();
    }

    public Map<Pair<Integer, Integer>, int[][]> getConfusionMatrixForTRAINING() {
        //TODO LATER
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 201; j++) {
                confusionMatrixForFoldTRAINING.put(new Pair<>(i, j), new int[numOfClasses + 1][numOfClasses + 1]);
            }

        }

        confusionMatrixForFoldTRAINING.forEach((k, v) -> {
            for (int i = 1; i < numOfClasses + 1; i++) {
                v[0][i] = i;
                v[i][0] = i;
            }
        });

        for (Pair<Pair<Integer, Integer>, Pair<Tuple, Integer>> foldTupleResult :
                superClassifier.getPredictedTraining()) {
            int foldNum = foldTupleResult.getKey().getKey().intValue();
            int trainingIteration = foldTupleResult.getKey().getValue().intValue();
            int actualClass = foldTupleResult.getValue().getKey().getClassNum();
            int predictedClass = foldTupleResult.getValue().getValue();
            Pair<Integer, Integer> index = new Pair<>(foldNum, trainingIteration);
            if (confusionMatrixForFoldTRAINING.get(index)[actualClass][predictedClass] == 0) {
                confusionMatrixForFoldTRAINING.get(index)[actualClass][predictedClass]++;
            } else {
                confusionMatrixForFoldTRAINING.get(index)[actualClass][predictedClass] =
                        confusionMatrixForFoldTRAINING.get(index)[actualClass][predictedClass] + 1;
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

    public ADABOOST getSuperClassifier() {
        return superClassifier;
    }
}
