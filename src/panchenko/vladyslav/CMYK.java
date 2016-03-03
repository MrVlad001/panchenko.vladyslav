package panchenko.vladyslav;

import java.awt.Color;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class CMYK extends SuwakiPanel {

    private int szerokoscObrazka = Obraz.image.getWidth();
    private int wysokoscObrazka = Obraz.image.getHeight();
    private double[][] cyjan = new double[szerokoscObrazka][wysokoscObrazka];
    private double[][] magenta = new double[szerokoscObrazka][wysokoscObrazka];
    private double[][] yellow = new double[szerokoscObrazka][wysokoscObrazka];
    private double[][] black = new double[szerokoscObrazka][wysokoscObrazka];

    public CMYK(JFrame parent) {
        super(parent, "Konwersja CMYK", 4);
        suwakiLabels[0].setText("CYJAN");
        suwakiLabels[1].setText("MAGENTA");
        suwakiLabels[2].setText("YELLOW");
        suwakiLabels[3].setText("BLACK");

        for (int i = 0; i < ileSuwakow; i++) {
            suwaki[i].setMinimum(-100);
            suwaki[i].setMaximum(100);
            suwaki[i].setValue(0);
        }
    }

    @Override
    public void suwakiAkcja() {
        konwertujDoCMYK();
        dodajDoCMYK();
    }

    @Override
    public void setSuwakiValuesLabels() {
        double text;
        for (int i = 0; i < ileSuwakow; i++) {
            text = (suwaki[i].getValue()) / 100.0;
            suwakiValuesLabels[i].setText("" + text);
        }
    }

    private void konwertujDoCMYK() {
        double rU, gU, bU, k;
        for (int x = 0; x < Obraz.image.getWidth(); x++) {
            for (int y = 0; y < Obraz.image.getHeight(); y++) {
                int rgb = Obraz.image.getRGB(x, y);
                Color color = new Color(rgb, true);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                rU = 1 - r / 255.0;
                gU = 1 - g / 255.0;
                bU = 1 - b / 255.0;

                k = black[x][y] = Math.min(Math.min(rU, gU), bU);
                cyjan[x][y] = (rU - k) / (1 - k);
                magenta[x][y] = (gU - k) / (1 - k);
                yellow[x][y] = (bU - k) / (1 - k);
            }
        }
    }

    private void dodajDoCMYK() {
        double dodajCyjan = suwaki[0].getValue() / 100.0;
        double dodajMagenta = suwaki[1].getValue() / 100.0;
        double dodajYellow = suwaki[2].getValue() / 100.0;
        double dodajBlack = suwaki[3].getValue() / 100.0;
        double c, m, ye, k;
        int rgb;
        if (dodajCyjan != 0 || dodajMagenta != 0 || dodajYellow != 0 || dodajBlack != 0) {
            for (int x = 0; x < Obraz.image.getWidth(); x++) {
                for (int y = 0; y < Obraz.image.getHeight(); y++) {
                    c = cyjan[x][y] + dodajCyjan;
                    m = magenta[x][y] + dodajMagenta;
                    ye = yellow[x][y] + dodajYellow;
                    k = black[x][y] + dodajBlack;

                    rgb = konwertujDoRGB(c, m, ye, k);
                    Obraz.image.setRGB(x, y, rgb);
                }
            }
        }
    }

    public int konwertujDoRGB(double c, double m, double y, double k) {
        double r = (1 - (c * (1 - k) + k)) * 255.0;
        double g = (1 - (m * (1 - k) + k)) * 255.0;
        double b = (1 - (y * (1 - k) + k)) * 255.0;

        int r1 = Fje.obetnij256((int) Math.round(r));
        int g1 = Fje.obetnij256((int) Math.round(g));
        int b1 = Fje.obetnij256((int) Math.round(b));
        return Fje.jrgb(r1, g1, b1);
    }
}