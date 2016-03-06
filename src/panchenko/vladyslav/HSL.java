package panchenko.vladyslav;

import java.awt.Color;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class HSL extends SlidersPanel {

    private int szerokoscObrazka = Image.image.getWidth();
    private int wysokoscObrazka = Image.image.getHeight();
    private double[][] h = new double[szerokoscObrazka][wysokoscObrazka];
    private double[][] l = new double[szerokoscObrazka][wysokoscObrazka];
    private double[][] s = new double[szerokoscObrazka][wysokoscObrazka];

    public HSL(JFrame parent) {
        super(parent, "Konwersja HSL", 3);
        sliderLabels[0].setText("barwa (H)");
        sliderLabels[1].setText("nasycenie (S)");
        sliderLabels[2].setText("jasność (L)");

        slider[0].setMinimum(-359);
        slider[0].setMaximum(359);
        slider[0].setValue(0);
        for (int i = 1; i < countSlider; i++) {
            slider[i].setMinimum(-100);
            slider[i].setMaximum(100);
            slider[i].setValue(0);
        }
    }

    @Override
    public void sliderAction() {
        konwertujDoHSL();
        dodajDoHSL();
    }

    @Override
    public void setSliderValuesLabels() {
        double text;
        sliderValuesLabels[0].setText("" + slider[0].getValue());
        for (int i = 1; i < countSlider; i++) {
            text = (slider[i].getValue()) / 100.0;
            sliderValuesLabels[i].setText("" + text);
        }
    }

    private void konwertujDoHSL() {
        Form.restoreImage();
        double rU, gU, bU, min, max, dM, l2;
        for (int x = 0; x < Image.image.getWidth(); x++) {
            for (int y = 0; y < Image.image.getHeight(); y++) {
                int rgb = Image.image.getRGB(x, y);
                Color color = new Color(rgb, true);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                rU = r / 255.0;
                gU = g / 255.0;
                bU = b / 255.0;

                min = Math.min(Math.min(rU, gU), bU);
                max = Math.max(Math.max(rU, gU), bU);
                dM = max - min;
                l2 = l[x][y] = (min + max) / 2.0;

                if (l2 == 0 || min == max) {
                    s[x][y] = 0;
                } else if (l2 <= 0.5) {
                    s[x][y] = dM / (2 * l2);
                } else if (l2 > 0.5) {
                    s[x][y] = dM / (2 - 2 * l2);
                }

                if (min == max) {
                    h[x][y] = 0;
                } else if (max == rU && gU >= bU) {
                    h[x][y] = 60 * ((gU - bU) / dM);
                } else if (max == rU && gU < bU) {
                    h[x][y] = 60 * ((gU - bU) / dM) + 360;
                } else if (max == gU) {
                    h[x][y] = 60 * ((bU - rU) / dM) + 120;
                } else if (max == bU) {
                    h[x][y] = 60 * ((rU - gU) / dM) + 240;
                }
            }
        }
    }

    private void dodajDoHSL() {
        double dodajH = slider[0].getValue();
        double dodajL = slider[2].getValue() / 100.0;
        double dodajS = slider[1].getValue() / 100.0;
        double h2, l2, s2;
        int rgb;

        for (int x = 0; x < Image.image.getWidth(); x++) {
            for (int y = 0; y < Image.image.getHeight(); y++) {
                h2 = h[x][y] + dodajH;
                l2 = l[x][y] + dodajL;
                s2 = s[x][y] + dodajS;
                if (h2 < 0) {
                    h2 = 720 + h2;
                }
                h2 %= 360.0;
                l2 = obetnij1(l2);
                s2 = obetnij1(s2);
                rgb = konwertujDoRGB(h2, l2, s2);
                Image.image.setRGB(x, y, rgb);
            }
        }
    }
    
    public static int jrgb(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }
    
    public static int erase256(int color) {
        if (color > 255) {
            color = 255;
        } else if (color < 0) {
            color = 0;
        }
        return color;
    }
    
     public static double obetnij1(double color) {
        if (color > 1) {
            color = 1.0;
        } else if (color < 0) {
            color = 0.0;
        }
        return color;
    }
    
    public int konwertujDoRGB(double h2, double l2, double s2) {
        double r, g, b;
        int r1, g1, b1;

        if (s2 == 0) {
            l2 = Math.round(l2 * 255.0);
            r = g = b = l2;
        } else {
            double q, p, Tr, Tg, Tb;
            double[] Tc = new double[3], Crgb = new double[3];
            if (l2 < 0.5) {
                q = l2 * (1 + s2);
            } else {
                q = l2 + s2 - (l2 * s2);
            }
            p = 2 * l2 - q;
            h2 = h2 / 360.0;
            Tr = h2 + (1 / 3.0);
            Tg = h2;
            Tb = h2 - (1 / 3.0);
            Tc[0] = Tr;
            Tc[1] = Tg;
            Tc[2] = Tb;

            for (int i = 0; i < Tc.length; i++) {
                if (Tc[i] < 0) {
                    Tc[i] = Tc[i] + 1;
                } else if (Tc[i] > 1) {
                    Tc[i] = Tc[i] - 1;
                }
            }

            for (int i = 0; i < Tc.length; i++) {
                if (Tc[i] < (1 / 6.0)) {
                    Crgb[i] = p + (q - p) * 6 * Tc[i];
                } else if (Tc[i] >= (1 / 6.0) && Tc[i] < (1 / 2.0)) {
                    Crgb[i] = q;
                } else if (Tc[i] >= (1 / 2.0) && Tc[i] < (2 / 3.0)) {
                    Crgb[i] = p + (q - p) * 6 * ((2 / 3.0) - Tc[i]);
                } else {
                    Crgb[i] = p;
                }
                Crgb[i] *= 255.0;
            }
            r = Math.round(Crgb[0]);
            g = Math.round(Crgb[1]);
            b = Math.round(Crgb[2]);
        }
        r1 = erase256((int) r);
        g1 = erase256((int) g);
        b1 = erase256((int) b);

        return jrgb(r1, g1, b1);
    }
    
}