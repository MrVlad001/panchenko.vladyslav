package panchenko.vladyslav;

import java.awt.Color;

/**
 *
 * @author Vladyslav
 */
public class KonwersjaXYZ {

    public static float refX = 0.9505f;
    public static float refY = 1.0f;
    public static float refZ = 1.0891f;
    public static float[][] mMatrix;
    public static float[][] mMatrix2;
    private float[] tabWartosci = new float[256];

    public KonwersjaXYZ() {
        mMatrix = genMmatrix();
        mMatrix2 = genMmatrix2();
        genTabWartosci();
    }

    public float[][] konwertujDoXYZ(int rgb) {
        Color color = new Color(rgb, true);
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        float[][] rgbMatrix = new float[1][3];
        rgbMatrix[0][0] = tabWartosci[r];
        rgbMatrix[0][1] = tabWartosci[g];
        rgbMatrix[0][2] = tabWartosci[b];

        return Fje.wymnozMacierze(rgbMatrix, mMatrix);
    }

    public int konwertujDoRGB(float[][] xyzMatrix) {
        float[][] rgbMatrix = Fje.wymnozMacierze(xyzMatrix, mMatrix2);
        float gamma = 1 / 2.2f;
        int r, g, b;

        r = Fje.konwertuj256(Math.pow(rgbMatrix[0][0], gamma));
        g = Fje.konwertuj256(Math.pow(rgbMatrix[0][1], gamma));
        b = Fje.konwertuj256(Math.pow(rgbMatrix[0][2], gamma));

        return Fje.jrgb(r, g, b);
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

    private void genTabWartosci() {
        float gamma = 2.2f;
        for (int i = 0; i < tabWartosci.length; i++) {
            tabWartosci[i] = (float) Math.pow(i / 255.0, gamma);
        }
    }
}