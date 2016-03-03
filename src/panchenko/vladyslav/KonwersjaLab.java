package panchenko.vladyslav;

import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public final class KonwersjaLab extends SuwakiPanel {

    private float e = 0.008856f;
    private float k = 903.3f;
    private float ek = e * k;
    public float[][] lTab;
    public float[][] aTab;
    public float[][] bTab;
    private float fx, fy, fz, xr, yr, zr, L, a, b;
    private KonwersjaXYZ xyz = new KonwersjaXYZ();
    private int dodajL;
    private int dodajA;
    private int dodajB;
    private boolean wywolanoKonstruktor = false;

    /**
     *
     * @param parent
     * @param type - 0: dla konwersji Lab; 1: na potrzeby dekompresji obrazów
     */
    public KonwersjaLab(JFrame parent, int type) {
        super(parent, "Konwersja Lab", 3);
        if (type == 0) {
            lTab = new float[Obraz.image.getWidth()][Obraz.image.getHeight()];
            aTab = new float[Obraz.image.getWidth()][Obraz.image.getHeight()];
            bTab = new float[Obraz.image.getWidth()][Obraz.image.getHeight()];

            suwakiLabels[0].setText("L");
            suwakiLabels[1].setText("a");
            suwakiLabels[2].setText("b");

            suwaki[0].setMinimum(-100);
            suwaki[0].setMaximum(100);
            suwaki[0].setValue(0);
            for (int i = 1; i < ileSuwakow; i++) {
                suwaki[i].setMinimum(-255);
                suwaki[i].setMaximum(255);
                suwaki[i].setValue(0);
            }
            konwertujDoLab();
            wywolanoKonstruktor = true;
            suwakiAkcja();
        } else {
            wywolanoKonstruktor = true;
        }
    }

    @Override
    public void suwakiAkcja() {
        if (wywolanoKonstruktor) {
            int rgb;
            dodajL = suwaki[0].getValue();
            dodajA = suwaki[1].getValue();
            dodajB = suwaki[2].getValue();

            for (int x = 0; x < Obraz.image.getWidth(); x++) {
                for (int y = 0; y < Obraz.image.getHeight(); y++) {
                    dodajDoLab(x, y);
                    rgb = xyz.konwertujDoRGB(konwertujDoXYZ());
                    Obraz.image.setRGB(x, y, rgb);
                }
            }
        }
    }

    private void dodajDoLab(int x, int y) {
        L = Fje.obetnijCustomFloat((lTab[x][y] + dodajL), 0, 100);
        a = Fje.obetnijDoByte(aTab[x][y] + dodajA);
        b = Fje.obetnijDoByte(bTab[x][y] + dodajB);
    }

    public void konwertujDoLab() {
        int rgb;
        float[][] konwertujDoXYZ;
        for (int x = 0; x < Obraz.image.getWidth(); x++) {
            for (int y = 0; y < Obraz.image.getHeight(); y++) {
                rgb = Obraz.image.getRGB(x, y);
                konwertujDoXYZ = xyz.konwertujDoXYZ(rgb);

                xr = (konwertujDoXYZ[0][0] / KonwersjaXYZ.refX);
                yr = (konwertujDoXYZ[0][1] / KonwersjaXYZ.refY);
                zr = (konwertujDoXYZ[0][2] / KonwersjaXYZ.refZ);

                if (xr > e) {
                    fx = (float) Math.pow(xr, (1 / 3.0));
                } else {
                    fx = ((k * xr + 16) / 116.0f);
                }
                if (yr > e) {
                    fy = (float) Math.pow(yr, (1 / 3.0));
                } else {
                    fy = ((k * yr + 16) / 116.0f);
                }
                if (zr > e) {
                    fz = (float) Math.pow(zr, (1 / 3.0));
                } else {
                    fz = ((k * zr + 16) / 116.0f);
                }

                lTab[x][y] = (116 * fy - 16);
                aTab[x][y] = 500 * (fx - fy);
                bTab[x][y] = 200 * (fy - fz);
            }
        }
    }

    private float[][] konwertujDoXYZ() {
        float[][] xyzMatrix = new float[1][3];
        xyzMatrix[0] = new float[3];

        if (L > ek) {
            yr = (float) Math.pow((L + 16) / 116.0, 3);
        } else {
            yr = L / k;
        }

        if (yr > e) {
            fy = ((L + 16) / 116.0f);
        } else {
            fy = ((k * yr + 16) / 116.0f);
        }
        fx = (a / 500.0f + fy);
        fz = (fy - (b / 200.0f));

        float tmp;
        tmp = (float) Math.pow(fx, 3);
        if (tmp > e) {
            xr = tmp;
        } else {
            xr = (116 * fx - 16) / k;
        }

        tmp = (float) Math.pow(fz, 3);
        if (tmp > e) {
            zr = tmp;
        } else {
            zr = (116 * fz - 16) / k;
        }

        xyzMatrix[0][0] = xr * KonwersjaXYZ.refX;
        xyzMatrix[0][1] = yr * KonwersjaXYZ.refY;
        xyzMatrix[0][2] = zr * KonwersjaXYZ.refZ;
        return xyzMatrix;
    }

    public void konwertujDoRGB() {
        int rgb;
        dodajL = dodajA = dodajB = 0;
        for (int x = 0; x < Obraz.image.getWidth(); x++) {
            for (int y = 0; y < Obraz.image.getHeight(); y++) {
                dodajDoLab(x, y);
                rgb = xyz.konwertujDoRGB(konwertujDoXYZ());
                Obraz.image.setRGB(x, y, rgb);
            }
        }
    }
}