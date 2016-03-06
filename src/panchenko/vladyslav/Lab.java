package panchenko.vladyslav;

import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public final class Lab extends SlidersPanel {

    private float e = 0.008856f;
    private float k = 903.3f;
    private float ek = e * k;
    public float[][] lTab;
    public float[][] aTab;
    public float[][] bTab;
    private float fx, fy, fz, xr, yr, zr, L, a, b;
    private XYZ xyz = new XYZ();
    private int addL;
    private int addA;
    private int addB;
    private boolean wywolanoKonstruktor = false;

    /**
     *
     * @param parent
     * @param type - 0: dla konwersji Lab; 1: na potrzeby dekompresji obrazów
     */
    public Lab(JFrame parent, int type) {
        super(parent, "Konwersja Lab", 3);
        if (type == 0) {
            lTab = new float[Image.image.getWidth()][Image.image.getHeight()];
            aTab = new float[Image.image.getWidth()][Image.image.getHeight()];
            bTab = new float[Image.image.getWidth()][Image.image.getHeight()];

            sliderLabels[0].setText("L");
            sliderLabels[1].setText("a");
            sliderLabels[2].setText("b");

            slider[0].setMinimum(-100);
            slider[0].setMaximum(100);
            slider[0].setValue(0);
            for (int i = 1; i < countSlider; i++) {
                slider[i].setMinimum(-255);
                slider[i].setMaximum(255);
                slider[i].setValue(0);
            }
            convertToLab();
            wywolanoKonstruktor = true;
            sliderAction();
        } else {
            wywolanoKonstruktor = true;
        }
    }

    @Override
    public void sliderAction() {
        if (wywolanoKonstruktor) {
            int rgb;
            addL = slider[0].getValue();
            addA = slider[1].getValue();
            addB = slider[2].getValue();

            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    addToLab(x, y);
                    rgb = xyz.konwertujDoRGB(convertToXYZ());
                    Image.image.setRGB(x, y, rgb);
                }
            }
        }
    }

    private void addToLab(int x, int y) {
        L = eraseCustomFloat((lTab[x][y] + addL), 0, 100);
        a = eraseToByte(aTab[x][y] + addA);
        b = eraseToByte(bTab[x][y] + addB);
    }

    public void convertToLab() {
        int rgb;
        float[][] convertToXYZ;
        for (int x = 0; x < Image.image.getWidth(); x++) {
            for (int y = 0; y < Image.image.getHeight(); y++) {
                rgb = Image.image.getRGB(x, y);
                convertToXYZ = xyz.konwertujDoXYZ(rgb);

                xr = (convertToXYZ[0][0] / XYZ.refX);
                yr = (convertToXYZ[0][1] / XYZ.refY);
                zr = (convertToXYZ[0][2] / XYZ.refZ);

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
    
     public static float eraseCustomFloat(float color, float limitDown, float limitUp) {
        if (color > limitUp) {
            color = limitUp;
        } else if (color < limitDown) {
            color = limitDown;
        }
        return color;
    }
     
    public static byte eraseToByte(double color) {
        byte limitUp = 127;
        byte limitDown = -128;
        if (color > limitUp) {
            color = limitUp;
        } else if (color < limitDown) {
            color = limitDown;
        }
        return (byte) color;
    } 

    private float[][] convertToXYZ() {
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

        xyzMatrix[0][0] = xr * XYZ.refX;
        xyzMatrix[0][1] = yr * XYZ.refY;
        xyzMatrix[0][2] = zr * XYZ.refZ;
        return xyzMatrix;
    }

    public void convertToRGB() {
        int rgb;
        addL = addA = addB = 0;
        for (int x = 0; x < Image.image.getWidth(); x++) {
            for (int y = 0; y < Image.image.getHeight(); y++) {
                addToLab(x, y);
                rgb = xyz.konwertujDoRGB(convertToXYZ());
                Image.image.setRGB(x, y, rgb);
            }
        }
    }
}