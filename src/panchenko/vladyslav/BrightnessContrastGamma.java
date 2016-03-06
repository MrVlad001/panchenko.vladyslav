package panchenko.vladyslav;

import java.awt.Color;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class BrightnessContrastGamma extends SlidersPanel {

    private boolean wywolanoKonstruktor = false;

    public BrightnessContrastGamma(JFrame parent) {
        super(parent, "Brightness/Contrast/Gamma", 3);
        sliderLabels[0].setText("Brightness");
        sliderLabels[1].setText("Contrast");
        sliderLabels[2].setText("Gamma");

        slider[0].setMinimum(-255);
        slider[0].setMaximum(255);
        slider[0].setValue(0);

        slider[1].setMinimum(-128);
        slider[1].setMaximum(127);
        slider[1].setValue(0);

        slider[2].setMinimum(-9);
        slider[2].setMaximum(9);
        slider[2].setValue(0);

        wywolanoKonstruktor = true;
    }

    @Override
    public void sliderAction() {
        if (wywolanoKonstruktor) {
            jasnosc();
            kontrast();
            gamma();
        }
    }

    @Override
    public void setSliderValuesLabels() {
        double text;
        int temp;
        for (int i = 0; i < countSlider - 1; i++) {
            text = (slider[i].getValue());
            temp = (int) text;
            sliderValuesLabels[i].setText("" + temp);
        }
        text = slider[2].getValue();
        text = wartoscSuwakaGamma(text);
        sliderValuesLabels[2].setText("" + text);
    }

    public void jasnosc() {
        int dodajJasnosc = slider[0].getValue();
        if (dodajJasnosc != 0) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    r += dodajJasnosc;
                    g += dodajJasnosc;
                    b += dodajJasnosc;
                    r = erase256(r);
                    g = erase256(g);
                    b = erase256(b);
                    rgb = jrgb(r, g, b);
                    Image.image.setRGB(x, y, rgb);
                }
            }
        }
    }

    public void kontrast() {
        int dodajKontrast = slider[1].getValue();
        if (dodajKontrast != 0) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    double r = color.getRed();
                    double g = color.getGreen();
                    double b = color.getBlue();
                    if (dodajKontrast >= 0) {
                        r = 127.0 / (127.0 - dodajKontrast) * (r - dodajKontrast);
                        g = 127.0 / (127.0 - dodajKontrast) * (g - dodajKontrast);
                        b = 127.0 / (127.0 - dodajKontrast) * (b - dodajKontrast);
                    } else {
                        r = ((127.0 + dodajKontrast) / 127.0) * r - dodajKontrast;
                        g = ((127.0 + dodajKontrast) / 127.0) * g - dodajKontrast;
                        b = ((127.0 + dodajKontrast) / 127.0) * b - dodajKontrast;
                    }
                    int r2 = erase256((int) r);
                    int g2 = erase256((int) g);
                    int b2 = erase256((int) b);
                    rgb = jrgb(r2, g2, b2);
                    Image.image.setRGB(x, y, rgb);
                }
            }
        }
    }

    public void gamma() {
        double dodajGamma = slider[2].getValue();
        if (dodajGamma != 0) {
            dodajGamma = wartoscSuwakaGamma(dodajGamma);

            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    double rf = r / 255.0;
                    double gf = g / 255.0;
                    double bf = b / 255.0;

                    rf = (double) Math.pow(rf, dodajGamma);
                    gf = (double) Math.pow(gf, dodajGamma);
                    bf = (double) Math.pow(bf, dodajGamma);

                    rf = rf * 255;
                    gf = gf * 255;
                    bf = bf * 255;

                    r = erase256((int) rf);
                    g = erase256((int) gf);
                    b = erase256((int) bf);
                    rgb = jrgb(r, g, b);
                    Image.image.setRGB(x, y, rgb);
                }
            }
        }
    }
    
     public double wartoscSuwakaGamma(double dodajGamma) {
        dodajGamma++;
        if (dodajGamma < 0) {
            dodajGamma = 1 + (dodajGamma - 1) / 10.0;
            dodajGamma = Math.round(dodajGamma * 10.0) / 10.0;
        } else if (dodajGamma == 0) {
            dodajGamma = 0.9;
        }
        return dodajGamma;
    }
    
    public static int erase256(int color) {
        if (color > 255) {
            color = 255;
        } else if (color < 0) {
            color = 0;
        }
        return color;
    }
    
     public static int jrgb(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }

   
}