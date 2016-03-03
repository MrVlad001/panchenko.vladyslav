package panchenko.vladyslav;

import java.awt.Color;

/**
 *
 * @author Vladyslav
 */
public class XYZ {

    public static float refX = 0.9505f;
    public static float refY = 1.0f;
    public static float refZ = 1.0891f;
    public static float[][] mMatrix;
    public static float[][] mMatrix2;
    private float[] arrValue = new float[256];

    public XYZ() {
        mMatrix = genMmatrix();
        mMatrix2 = genMmatrix2();
        genArrValue();
    }

    public float[][] konwertujDoXYZ(int rgb) {
        Color color = new Color(rgb, true);
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        float[][] rgbMatrix = new float[1][3];
        rgbMatrix[0][0] = arrValue[r];
        rgbMatrix[0][1] = arrValue[g];
        rgbMatrix[0][2] = arrValue[b];

        return matrixMultiplication(rgbMatrix, mMatrix);
    }

    public int konwertujDoRGB(float[][] xyzMatrix) {
        float[][] rgbMatrix = matrixMultiplication(xyzMatrix, mMatrix2);
        float gamma = 1 / 2.2f;
        int r, g, b;

        r = convert256(Math.pow(rgbMatrix[0][0], gamma));
        g = convert256(Math.pow(rgbMatrix[0][1], gamma));
        b = convert256(Math.pow(rgbMatrix[0][2], gamma));

        return jrgb(r, g, b);
    }

    public static float[][] genMmatrix() {
        float[][] matrix = new float[3][3];
        matrix[0][0] = 0.57667f;
        matrix[0][1] = 0.29734f;
        matrix[0][2] = 0.02703f;
        matrix[1][0] = 0.18556f;
        matrix[1][1] = 0.62736f;
        matrix[1][2] = 0.07069f;
        matrix[2][0] = 0.18823f;
        matrix[2][1] = 0.07529f;
        matrix[2][2] = 0.99134f;
        return matrix;
    }

    public static float[][] genMmatrix2() {
        float[][] matrix = new float[3][3];
        matrix[0][0] = 2.04159f;
        matrix[0][1] = -0.96924f;
        matrix[0][2] = 0.01344f;
        matrix[1][0] = -0.56501f;
        matrix[1][1] = 1.87597f;
        matrix[1][2] = -0.11836f;
        matrix[2][0] = -0.34473f;
        matrix[2][1] = 0.04156f;
        matrix[2][2] = 1.01517f;
        return matrix;
    }

    private void genArrValue() {
        float gamma = 2.2f;
        for (int i = 0; i < arrValue.length; i++) {
            arrValue[i] = (float) Math.pow(i / 255.0, gamma);
        }
    }
    
    public static int jrgb(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }
    
    public static int convert256(double color) {
        return erase256((int) (color * 255.0));
    }
       
    public static int erase256(int color) {
        if (color > 255) {
            color = 255;
        } else if (color < 0) {
            color = 0;
        }
        return color;
    }  
 
      public static float[][] matrixMultiplication(float[][] tab1, float[][] tab2) {
        float[][] newMatrix = new float[tab1.length][tab2[0].length];
        if (tab1[0].length == tab2.length) {
            for (int i = 0; i < tab1.length; i++) {
                for (int j = 0; j < tab2[0].length; j++) {
                    double temp = 0;
                    for (int w = 0; w < tab2.length; w++) {
                        temp += tab1[i][w] * tab2[w][j];
                    }
                    newMatrix[i][j] = (float) temp;
                }
            }
        } else {
            throw new RuntimeException("Podane tablice mają niewłasciwe wymiary");
        }
        return newMatrix;
    }
    
}