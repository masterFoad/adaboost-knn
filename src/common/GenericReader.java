package common;

import model.KNN;
import model.Tuple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GenericReader {


    /**
     * Initialize the files from csv.
     * @param filePath
     * @param numOfClasses
     * @param fromCSV
     * @param <T>
     * @return
     */
    public static <T> List<T> init(String filePath, int numOfClasses, Loadable<T> fromCSV) {
        List<T> dataFromCSV = new ArrayList<>();
        InputStream is = null;
        BufferedReader reader = null;

        try {

            is = common.GenericReader.class.getResourceAsStream(filePath);
            reader = new BufferedReader(new InputStreamReader(is));

            String line = reader.readLine();
            line = reader.readLine();

            while (line != null) {
                String[] attributes = line.split(",");


                T obj = fromCSV.create(attributes, numOfClasses);

                dataFromCSV.add(obj);

                line = reader.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                is.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return dataFromCSV;


    }

    public static KNN createClassifier(String[] metadata, int numOfClasses) {
//        int randomK = ThreadLocalRandom.current().nextInt(1, 15);
//        if (randomK % 2 == 0) {
//            randomK++;
//        }

        double [] weights = new double[metadata.length];
        int index = 0;
        for(String s: metadata){
            weights[index++] = Double.parseDouble(s);
        }
        return new KNN(3, numOfClasses, weights);
    }

    public static Tuple createTuple(String[] metadata) {
        double[] tuples = new double[metadata.length - 1];
        for (int i = 0; i < metadata.length - 1; i++) {
            tuples[i] = Double.parseDouble(metadata[i]);
        }
        return new Tuple(tuples, Integer.parseInt(metadata[metadata.length - 1]));
    }

}
