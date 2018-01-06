package lab;

import com.sun.org.apache.regexp.internal.RE;
import common.Utils;
import javafx.util.Pair;
import model.SetStarter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lab {

    public static ArrayList<AdaboostExperiment> experiments = new ArrayList<>();

    private ArrayList<Result> allResults;

    private ArrayList<Result> allResultsForTesting;

    private int counter = 0;
    private int index = 0;

    public Lab(AdaboostExperiment exp1) {
        allResults = new ArrayList<>();
        allResultsForTesting = new ArrayList<>();

        index = 0;
        counter = 0;
        for (int i = 1; i < SetStarter.getMaxFolds(); i++) {
            counter = 0;
            index = i;

            exp1.getConfusionMatrixForTRAINING().forEach((k, v) ->
            {

                if (k.getKey().intValue() == index) {
                    if (v[1][1] > 0) {
                        int[][] currentMatrix = exp1.getConfusionMatrixForTRAINING().get(new Pair<>(k.getKey().intValue(), k.getValue().intValue()));
                        ConfusionMatrix cmm = Utils.transformToCM(currentMatrix);
                        extractResults(cmm, currentMatrix, k.getKey().intValue(), k.getValue().intValue());
                        counter++;
                    }
                }


            });

            exp1.getConfusionMatrixForTESTING().forEach((k, v) -> {
                if (k.intValue() == index) {
                    if (v[1][1] > 0) {
                        int[][] currentMatrix = exp1.getConfusionMatrixForTESTING().get(index);
                        ConfusionMatrix cmm = Utils.transformToCM(currentMatrix);
                        extractResults(cmm, currentMatrix, k.intValue());
                        counter++;
                    }
                }
            });
        }

    }

//    public double getAverageForResult(Result ){
//
//    }

    public ArrayList<Result> getAllResults() {
        return allResults;
    }


    public ArrayList<Result> getAllResultsForTesting() {
        return allResultsForTesting;
    }

    /**
     * takes an expirement and returns an array of results.
     *
     * @param cm
     * @param arr
     * @param foldIteration
     * @param trainingIteration
     */
    private void extractResults(ConfusionMatrix cm, int[][] arr, int foldIteration, int trainingIteration) {
        /**
         * class, TP, FP, misclassification rate, precision, recall, accuracy, FMeasure
         */


        //System.out.println("Confusion Matrix for fold iteration " + foldIteration + " and training iteration " + trainingIteration);

        //System.out.println(cm);

        int sum = 0;
        for (int i = 1; i < arr.length; i++) {
            sum += arr[i][i];
        }
        //System.out.println("class, TP, FP, misclassification rate, precision, recall, accuracy, FMeasure");
        StringBuilder report = new StringBuilder();

        // classNum -> map(fp, 0.89)
        //Map<Integer, Map<String, Double>> rs = new HashMap<>();
        //rs.put(i, new HashMap<>("tp", ))
        for (int i = 1; i < arr.length; i++) {
            //fp


            String cl = i + "";
            report.append(cl + ",") //class
                    .append(cm.getRecallForLabel(cl) + ",") //TP
                    .append((double) (cm.getRowSum(cl) - arr[i][i]) / arr[i][i] + ",") //fp
                    .append(cm.getPrecisionForLabel(cl) + ",") //precision
                    .append(((double) cm.getTotalSum() - sum) / cm.getTotalSum() + ",")//misclassification
                    .append(cm.getAccuracy()) //accuracy
                    .append(cm.getFMeasureForLabels())  //fm
                    .append(System.getProperty("line.separator"));


            allResults.add(
                    new Result(foldIteration, trainingIteration, i
                            , cm.getRecallForLabel(cl), (double) (cm.getRowSum(cl) - arr[i][i]) / arr[i][i]
                            , ((double) cm.getTotalSum() - sum) / cm.getTotalSum()
                            , cm.getPrecisionForLabel(cl), cm.getRecallForLabel(cl)
                            , cm.getAccuracy(), cm.getFMeasureForLabels().get(cl)
                            , cm));
        }

        //System.out.println(report.toString());


    }


    /**
     * extract results for testing sets
     *
     * @param cm
     * @param arr
     * @param foldIteration
     */
    private void extractResults(ConfusionMatrix cm, int[][] arr, int foldIteration) {
        /**
         * class, TP, FP, misclassification rate, precision, recall, accuracy, FMeasure
         */

//        System.out.println("Confusion Matrix Testing  in fold iteration " + foldIteration);

//        System.out.println(cm);

        int sum = 0;
        for (int i = 1; i < arr.length; i++) {
            sum += arr[i][i];
        }
//        System.out.println("class, TP, FP, misclassification rate, precision, recall, accuracy, FMeasure");
        StringBuilder report = new StringBuilder();
        for (int i = 1; i < arr.length; i++) {
            //fp
            String cl = i + "";
            report.append(cl + ",") //class
                    .append(cm.getRecallForLabel(cl) + ",") //TP
                    .append((double) (cm.getRowSum(cl) - arr[i][i]) / arr[i][i] + ",") //fp
                    .append(cm.getPrecisionForLabel(cl) + ",") //precision
                    .append(((double) cm.getTotalSum() - sum) / cm.getTotalSum() + ",")//misclassification
                    .append(cm.getAccuracy()) //accuracy
                    .append(cm.getFMeasureForLabels())  //fm
                    .append(System.getProperty("line.separator"));


            allResultsForTesting.add(
                    new Result(foldIteration, i
                            , cm.getRecallForLabel(cl), (double) (cm.getRowSum(cl) - arr[i][i]) / arr[i][i]
                            , ((double) cm.getTotalSum() - sum) / cm.getTotalSum()
                            , cm.getPrecisionForLabel(cl), cm.getRecallForLabel(cl)
                            , cm.getAccuracy(), cm.getFMeasureForLabels().get(cl)
                            , cm));
        }

//        System.out.println(report.toString());


    }

    /**
     * return the averages for a training run
     *
     * @param r
     * @return
     */
    public StringBuilder getAveragesForTraining2classes(Result r) {
        StringBuilder averages = new StringBuilder();
        for (Lab.Result r2 :
                this.getAllResults()) {
            if (r.foldingIteration == r2.foldingIteration && r.trainingIteration == r2.trainingIteration) {
                if (r.cl != r2.cl) {

                    averages.append("Averages for fold " + r.foldingIteration + " Training " + r.trainingIteration + "\n")
                            .append("tp " + (r.tp + r2.tp) / 2)
                            .append("fp " + (r.fp + r2.fp) / 2)
                            .append("misc " + (r.misc + r2.misc) / 2)
                            .append("prec " + (r.precision + r2.precision) / 2)
                            .append("rec " + (r.recall + r2.recall) / 2)
                            .append("fmeasure " + (r.fmeasure + r2.fmeasure) / 2)
                            .append("acc " + (r.accuracy + r2.accuracy) / 2);
                }
            }
        }
        return averages;
    }

    /**
     * return the averages for a testing run
     *
     * @param r
     * @return
     */
    public StringBuilder getAveragesForTest2Classes(Result r) {
        StringBuilder averages = new StringBuilder();
        for (Lab.Result r2 :
                this.getAllResultsForTesting()) {
            if (r.foldingIteration == r2.foldingIteration) {
                if (r.cl != r2.cl) {

                    averages.append("Averages for fold " + r.foldingIteration + "\n")
                            .append("tp " + (r.tp + r2.tp) / 2 + " ,")
                            .append("fp " + (r.fp + r2.fp) / 2 + " ,")
                            .append("misc " + (r.misc + r2.misc) / 2 + " ,")
                            .append("prec " + (r.precision + r2.precision) / 2 + " ,")
                            .append("rec " + (r.recall + r2.recall) / 2 + " ,")
                            .append("fmeasure " + (r.fmeasure + r2.fmeasure) / 2 + " ,")
                            .append("acc " + (r.accuracy + r2.accuracy) / 2 + "\n");
                }
            }
        }
        return averages;
    }

    public StringBuilder getAveragesForTraining3classes(Result r) {
        StringBuilder averages = new StringBuilder();
        for (Lab.Result r3 :
                this.getAllResults()) {
            if (r.cl != r3.cl && r.trainingIteration == r3.trainingIteration && r.foldingIteration == r3.foldingIteration) {
                for (Lab.Result r2 :
                        this.getAllResults()) {
                    if (r.foldingIteration == r2.foldingIteration && r.trainingIteration == r2.trainingIteration) {
                        if (r.cl != r2.cl && r2.cl != r3.cl) {

                            averages.append("Averages for fold " + r.foldingIteration + " Training " + r.trainingIteration + "\n")
                                    .append("tp " + (r.tp + r2.tp + r2.tp) / 3 + " ,")
                                    .append("fp " + (r.fp + r2.fp + r2.fp) / 3 + " ,")
                                    .append("misc " + (r.misc + r2.misc + r2.misc) / 3 + " ,")
                                    .append("prec " + (r.precision + r2.precision + r2.precision) / 3 + " ,")
                                    .append("rec " + (r.recall + r2.recall + r2.recall) / 3 + " ,")
                                    .append("fmeasure " + (r.fmeasure + r2.fmeasure + r2.fmeasure) / 3 + " ,")
                                    .append("acc " + (r.accuracy + r2.accuracy + r2.accuracy) / 3 + "\n");
                        }
                    }
                }
            }

        }

        return averages;
    }

    public StringBuilder getAveragesForTesting3classes(Result r) {
        StringBuilder averages = new StringBuilder();
        for (Lab.Result r3 :
                this.getAllResultsForTesting()) {
            if (r.cl != r3.cl && r.trainingIteration == r3.trainingIteration && r.foldingIteration == r3.foldingIteration) {
                for (Lab.Result r2 :
                        this.getAllResults()) {
                    if (r.foldingIteration == r2.foldingIteration && r.trainingIteration == r2.trainingIteration) {
                        if (r.cl != r2.cl && r2.cl != r3.cl) {

                            averages.append("Averages for fold " + r.foldingIteration + "\n")
                                    .append("tp " + (r.tp + r2.tp + r2.tp) / 3 + " ,")
                                    .append("fp " + (r.fp + r2.fp + r2.fp) / 3 + " ,")
                                    .append("misc " + (r.misc + r2.misc + r2.misc) / 3 + " ,")
                                    .append("prec " + (r.precision + r2.precision + r2.precision) / 3 + " ,")
                                    .append("rec " + (r.recall + r2.recall + r2.recall) / 3 + " ,")
                                    .append("fmeasure " + (r.fmeasure + r2.fmeasure + r2.fmeasure) / 3 + " ,")
                                    .append("acc " + (r.accuracy + r2.accuracy + r2.accuracy) / 3 + "\n");
                        }
                    }
                }
            }

        }

        return averages;
    }

    /**
     * foldingIteration: the folding iteration
     * trainingIteration: the training iteration
     * cl : the classified class
     */
    public static class Result {
        public int foldingIteration;
        public int trainingIteration;
        public int cl;
        public double tp;
        public double fp;
        public double misc;
        public double precision;
        public double recall;
        public double accuracy;
        public double fmeasure;
        public ConfusionMatrix cm;

        public Result(int foldingIteration, int cl, double tp, double fp, double misc, double precision, double recall, double accuracy, double fmeasure, ConfusionMatrix cm) {
            this.foldingIteration = foldingIteration;
            this.cl = cl;
            this.tp = tp;
            this.fp = fp;
            this.misc = misc;
            this.precision = precision;
            this.recall = recall;
            this.accuracy = accuracy;
            this.fmeasure = fmeasure;
            this.cm = cm;
            this.trainingIteration = -1;
        }


        public Result(int foldingIteration, int trainingIteration, int cl, double tp, double fp, double misc, double precision, double recall, double accuracy, double fmeasure, ConfusionMatrix cm) {
            this.foldingIteration = foldingIteration;
            this.trainingIteration = trainingIteration;
            this.cl = cl;
            this.tp = tp;
            this.fp = fp;
            this.misc = misc;
            this.precision = precision;
            this.recall = recall;
            this.accuracy = accuracy;
            this.fmeasure = fmeasure;
            this.cm = cm;
            this.trainingIteration = trainingIteration;
        }

        @Override
        public String toString() {
            String replaces;
            if (trainingIteration == -1) {
                replaces = "";
            } else {
                replaces = ", trainingIteration=" + trainingIteration;
            }

            return "Result{" +
                    " Confusion Matrix=\n" + cm +
                    "\n,foldingIteration=" + foldingIteration +
                    replaces +
                    ", cl=" + cl +
                    ", tp=" + tp +
                    ", fp=" + fp +
                    ", misc=" + misc +
                    ", precision=" + precision +
                    ", recall=" + recall +
                    ", accuracy=" + accuracy +
                    ", fmeasure=" + fmeasure +
                    '}' + "\n\n";
        }
    }

//    public static void runExpiriments(){
//        AdaboostExperiment exp1 = new AdaboostExperiment("/weights1.csv", "/data1.csv", 2, 0.9);
//        exp1.start();
//        experiments.add(exp1);
//
//
//        AdaboostExperiment exp2 = new AdaboostExperiment("/weights1.csv", "/data1.csv", 2, 0.9);
//        exp2.start();
//        experiments.add(exp2);
//
//
//        AdaboostExperiment exp3 = new AdaboostExperiment("/weights1.csv", "/data1.csv", 2, 0.9);
//        exp3.start();
//        experiments.add(exp3);
//
//        AdaboostExperiment exp4 = new AdaboostExperiment("/weights2.csv", "/data2.csv", 2, 0.9);
//        exp4.start();
//        experiments.add(exp4);
//
//
//        AdaboostExperiment exp5 = new AdaboostExperiment("/weights2.csv", "/data2.csv", 2, 0.9);
//        exp5.start();
//        experiments.add(exp5);
//
//
//        AdaboostExperiment exp6 = new AdaboostExperiment("/weights2.csv", "/data2.csv", 2, 0.9);
//        exp6.start();
//        experiments.add(exp6);
//
//
//        AdaboostExperiment exp7 = new AdaboostExperiment("/weights3.csv", "/data3.csv", 2, 0.9);
//        exp7.start();
//        experiments.add(exp7);
//
//
//        AdaboostExperiment exp8 = new AdaboostExperiment("/weights3.csv", "/data3.csv", 2, 0.9);
//        exp8.start();
//        experiments.add(exp8);
//
//
//        AdaboostExperiment exp9 = new AdaboostExperiment("/weights3.csv", "/data3.csv", 2, 0.9);
//        exp9.start();
//        experiments.add(exp9);
//    }


}
