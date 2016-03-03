package panchenko.vladyslav;

import java.awt.Color;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class JasnoscKontrastGamma extends SuwakiPanel {

    private boolean wywolanoKonstruktor = false;

    public JasnoscKontrastGamma(JFrame parent) {
        super(parent, "Jasność/Kontrast/Gamma", 3);
        suwakiLabels[0].setText("Jasność");
        suwakiLabels[1].setText("Kontrast");
        suwakiLabels[2].setText("Gamma");

        suwaki[0].setMinimum(-255);
        suwaki[0].setMaximum(255);
        suwaki[0].setValue(0);

        suwaki[1].setMinimum(-128);
        suwaki[1].setMaximum(127);
        suwaki[1].setValue(0);

        suwaki[2].setMinimum(-9);
        suwaki[2].setMaximum(9);
        suwaki[2].setValue(0);

//        setTabelaWartosciLUT();
        wywolanoKonstruktor = true;
    }

    @Override
    public void suwakiAkcja() {
        if (wywolanoKonstruktor) {
            jasnosc();
            kontrast();
            gamma();
        }
    }

    @Override
    public void setSuwakiValuesLabels() {
        double text;
        int temp;
        for (int i = 0; i < ileSuwakow - 1; i++) {
            text = (suwaki[i].getValue());
            temp = (int) text;
            suwakiValuesLabels[i].setText("" + temp);
        }
        text = suwaki[2].getValue();
        text = wartoscSuwakaGamma(text);
        suwakiValuesLabels[2].setText("" + text);
    }

    public void jasnosc() {
        int dodajJasnosc = suwaki[0].getValue();
        if (dodajJasnosc != 0) {
            for (int x = 0; x < Obraz.image.getWidth(); x++) {
                for (int y = 0; y < Obraz.image.getHeight(); y++) {
                    int rgb = Obraz.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    r += dodajJasnosc;
                    g += dodajJasnosc;
                    b += dodajJasnosc;
                    r = obetnij256(r);
                    g = obetnij256(g);
                    b = obetnij256(b);
                    rgb = jrgb(r, g, b);
                    Obraz.image.setRGB(x, y, rgb);
                }
            }
        }
    }

    public void kontrast() {
        int dodajKontrast = suwaki[1].getValue();
        if (dodajKontrast != 0) {
            for (int x = 0; x < Obraz.image.getWidth(); x++) {
                for (int y = 0; y < Obraz.image.getHeight(); y++) {
                    int rgb = Obraz.image.getRGB(x, y);
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
                    int r2 = obetnij256((int) r);
                    int g2 = obetnij256((int) g);
                    int b2 = obetnij256((int) b);
                    rgb = jrgb(r2, g2, b2);
                    Obraz.image.setRGB(x, y, rgb);
                }
            }
        }
    }

    public void gamma() {
        double dodajGamma = suwaki[2].getValue();
        if (dodajGamma != 0) {
            dodajGamma = wartoscSuwakaGamma(dodajGamma);

            for (int x = 0; x < Obraz.image.getWidth(); x++) {
                for (int y = 0; y < Obraz.image.getHeight(); y++) {
                    int rgb = Obraz.image.getRGB(x, y);
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

                    r = obetnij256((int) rf);
                    g = obetnij256((int) gf);
                    b = obetnij256((int) bf);
                    rgb = jrgb(r, g, b);
                    Obraz.image.setRGB(x, y, rgb);
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
    
    public static int obetnij256(int color) {
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

    /**0
    
     */
   
}