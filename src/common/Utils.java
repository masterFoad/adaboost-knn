package common;

public class Utils {


    private Utils(){};

    public static double[][] EXPAND = {{3,0},{0,3}};

    public static double [][] REDUCE_DIM_MATRIX
            = {{1,1,1,1,0,0,0,0},{0,0,0,0,1,1,1,1}};

    public static double [][] SAME_DIM
            = {{1,0},{0,1}};

    public static double[] multiply(double[][] matrix, double[] vector) {
        int rows = matrix.length;
        int columns = matrix[0].length;

        double[] result = new double[rows];

        for (int row = 0; row < rows; row++) {
            double sum = 0;
            for (int column = 0; column < columns; column++) {
                sum += matrix[row][column]
                        * vector[column];
            }
            result[row] = sum;
        }
        return result;
    }
}
