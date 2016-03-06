package panchenko.vladyslav;

import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public final class Luv extends SlidersPanel {

    private int widthImage = Image.image.getWidth();
    private int heightImage = Image.image.getHeight();
    private float e = 0.008856f;
    private float k = 903.3f;
    private float ek = e * k;
//    private float Yn = 1.0f;//Skoro wartość =1, to nie ma sensu przez to mnożyć
    private float un = 0.2009f;
    private float vn = 0.4610f;
    private float[][] lTab = new float[widthImage][heightImage];
    private float[][] uTab = new float[widthImage][heightImage];
    private float[][] vTab = new float[widthImage][heightImage];
    private float X, Y, Z, ui, vi, L, u, v, tmpMianownik, tmpL;
    private XYZ xyz = new XYZ();
    private int addL, addU, addV;
    private boolean wywolanoKonstruktor = false;

    public Luv(JFrame parent) {
        super(parent, "Konwersja Lab", 3);
        sliderLabels[0].setText("L");
        sliderLabels[1].setText("u");
        sliderLabels[2].setText("v");

        slider[0].setMinimum(-100);
        slider[0].setMaximum(100);
        slider[0].setValue(0);
        slider[1].setMinimum(-354);
        slider[1].setMaximum(354);
        slider[1].setValue(0);
        slider[2].setMinimum(-262);
        slider[2].setMaximum(262);
        slider[2].setValue(0);
        convertToLuv();
        wywolanoKonstruktor = true;
        sliderAction();
    }

    @Override
    public void sliderAction() {
        if (wywolanoKonstruktor) {
            int rgb;
            addL = slider[0].getValue();
            addU = slider[1].getValue();
            addV = slider[2].getValue();

            for (int x = 0; x < widthImage; x++) {
                for (int y = 0; y < heightImage; y++) {
                    addToLuv(x, y);
                    rgb = xyz.konwertujDoRGB(convertToXYZ());
                    Image.image.setRGB(x, y, rgb);
                }
            }
        }
    }

    private void addToLuv(int x, int y) {
        L = eraseCustomFloat((lTab[x][y] + addL), 0, 100);
        u = eraseCustomFloat((uTab[x][y] + addU), -134, 220);
        v = eraseCustomFloat((vTab[x][y] + addV), -140, 122);
    }

    private void convertToLuv() {
        int rgb;
        float[][] convertToXYZ;
        float tmpY;
        for (int x = 0; x < widthImage; x++) {
            for (int y = 0; y < heightImage; y++) {
                rgb = Image.image.getRGB(x, y);
                convertToXYZ = xyz.konwertujDoXYZ(rgb);

                X = convertToXYZ[0][0];
                Y = convertToXYZ[0][1];
                Z = convertToXYZ[0][2];

                tmpMianownik = (float) (X + 15.0 * Y + 3.0 * Z);
                ui = (float) (4.0 * X / tmpMianownik);
                vi = (float) (9.0 * Y / tmpMianownik);
//                tmpY = Y / Yn;
                tmpY = Y;
                if (tmpY > e) {
                    lTab[x][y] = (float) ((116.0 * Math.pow(tmpY, (1 / 3.0))) - 16);
                } else {
                    lTab[x][y] = k * tmpY;
                }
                tmpL = (float) (13.0 * lTab[x][y]);
                uTab[x][y] = tmpL * (ui - un);
                vTab[x][y] = tmpL * (vi - vn);
            }
        }
    }
    
     public static float eraseCustomFloat(float color, float limitDown, float limitUp) {
        if (color > limitUp) {
            color = limitUp;
        } else if (color < limitDown) {
            color = limitDown;
        }
        return color;
    }

    private float[][] convertToXYZ() {
        float[][] xyzMatrix = new float[1][3];
        xyzMatrix[0] = new float[3];
        tmpL = (float) (L * 13.0);
        ui = (u / tmpL) + un;
        vi = (v / tmpL) + vn;

        if (L > ek) {
//            Y = Yn * Math.pow((L + 16) / 116.0, 3.0);
            Y = (float) Math.pow((L + 16) / 116.0, 3.0);
        } else {
//            Y = Yn * L / k;
            Y = L / k;
        }

        tmpMianownik = (float) (4.0 * vi);
        X = (float) (Y * (9.0 * ui) / tmpMianownik);
        Z = (float) (Y * (12 - 3.0 * ui - 20.0 * vi) / tmpMianownik);

        xyzMatrix[0][0] = X;
        xyzMatrix[0][1] = Y;
        xyzMatrix[0][2] = Z;
        return xyzMatrix;
    }
}