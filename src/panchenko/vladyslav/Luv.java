package panchenko.vladyslav;

import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public final class Luv extends SlidersPanel {

    private int szerokoscObrazka = Obraz.image.getWidth();
    private int wysokoscObrazka = Obraz.image.getHeight();
    private float e = 0.008856f;
    private float k = 903.3f;
    private float ek = e * k;
//    private float Yn = 1.0f;//Skoro wartość =1, to nie ma sensu przez to mnożyć
    private float un = 0.2009f;
    private float vn = 0.4610f;
    private float[][] lTab = new float[szerokoscObrazka][wysokoscObrazka];
    private float[][] uTab = new float[szerokoscObrazka][wysokoscObrazka];
    private float[][] vTab = new float[szerokoscObrazka][wysokoscObrazka];
    private float X, Y, Z, ui, vi, L, u, v, tmpMianownik, tmpL;
    private XYZ xyz = new XYZ();
    private int dodajL, dodajU, dodajV;
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
        konwertujDoLuv();
        wywolanoKonstruktor = true;
        sliderAction();
    }

    @Override
    public void sliderAction() {
        if (wywolanoKonstruktor) {
            int rgb;
            dodajL = slider[0].getValue();
            dodajU = slider[1].getValue();
            dodajV = slider[2].getValue();

            for (int x = 0; x < szerokoscObrazka; x++) {
                for (int y = 0; y < wysokoscObrazka; y++) {
                    dodajDoLuv(x, y);
                    rgb = xyz.konwertujDoRGB(konwertujDoXYZ());
                    Obraz.image.setRGB(x, y, rgb);
                }
            }
        }
    }

    private void dodajDoLuv(int x, int y) {
        L = Fje.obetnijCustomFloat((lTab[x][y] + dodajL), 0, 100);
        u = Fje.obetnijCustomFloat((uTab[x][y] + dodajU), -134, 220);
        v = Fje.obetnijCustomFloat((vTab[x][y] + dodajV), -140, 122);
    }

    private void konwertujDoLuv() {
        int rgb;
        float[][] konwertujDoXYZ;
        float tmpY;
        for (int x = 0; x < szerokoscObrazka; x++) {
            for (int y = 0; y < wysokoscObrazka; y++) {
                rgb = Obraz.image.getRGB(x, y);
                konwertujDoXYZ = xyz.konwertujDoXYZ(rgb);

                X = konwertujDoXYZ[0][0];
                Y = konwertujDoXYZ[0][1];
                Z = konwertujDoXYZ[0][2];

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

    private float[][] konwertujDoXYZ() {
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