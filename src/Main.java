import common.GenericReader;
import model.KNN;
import model.SetStarter;
import model.Tuple;

public class Main {

    public static void main(String args[]) {


//        GenericReader.init("/data1.csv", 2, (metaData, numOfClasses) -> GenericReader.createClassifier(metaData, numOfClasses));
//        GenericReader.init("/data1.csv", 0, (metaData, numOfClasses) -> GenericReader.createTuple(metaData));


        // reading the data from csv
        SetStarter
                .divide(
                        GenericReader.init("/data1.csv",
                                0,
                                (metaData, numOfClasses) -> GenericReader.createTuple(metaData)).toArray(new Tuple[0]),
                        0.66);

        Tuple[] trainingSet = SetStarter.getTrainingSet();
        Tuple[] testingSet = SetStarter.getTestingSet();

        for (int i = 0; i < trainingSet.length; i++) {
            trainingSet[i].setWeight(1.0 / (double) trainingSet.length);
        }

        KNN knn = new KNN(127, 2, 1.0, 1.0);
        for (int i = 0; i < trainingSet.length; i++) {
            System.out.println("Class: output " + knn.init(trainingSet, trainingSet[i]) + " yi:" + trainingSet[i].getClassNum());
            System.out.println("Accuracy: " + knn.getAccuracy());

        }

        System.out.println((double) knn.getCountCurrect() / trainingSet.length);
    }

}
