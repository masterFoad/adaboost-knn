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

        for (int i = 0; i < testingSet.length; i++) {
            KNN knn = new KNN(27, 2, 1.0, 1.0);
            System.out.println(knn.init(trainingSet, testingSet[i]));
            System.out.println(knn.getAccuracy());
        }

    }

}
