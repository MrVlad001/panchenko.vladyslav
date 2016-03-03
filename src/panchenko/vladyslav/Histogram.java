package panchenko.vladyslav;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author Vladyslav
 */
public final class Histogram extends JPanel {

    public static final int rozmiarHist = 257;
    private static double[] daneNormal = new double[256];
    public static double[] dane = new double[256];
    private static double maxP;
    private static double srednia;
    private static double wariancja;
    private static int margines = HistogramDialog.margines;
    public static int histRozmiar = rozmiarHist + 1 + margines;

    public Histogram(int h) {
        super();
        setSize(histRozmiar, histRozmiar);
        wczytajDane(h);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.white);
        g.drawLine(margines, margines, histRozmiar - 1, margines);//pozioma
        g.drawLine(margines, margines, margines, histRozmiar - 1); //pionowa
        g.drawLine(histRozmiar - 1, margines, histRozmiar - 1, histRozmiar - 1);
        g.drawLine(margines, histRozmiar - 1, histRozmiar - 1, histRozmiar - 1);

        g.setColor(Color.black);
        for (int i = 0; i < daneNormal.length; i++) {
            double y = 256 - Math.round(daneNormal[i] * 256 / maxP);
            if (y != rozmiarHist - 1) {
                g.drawLine(i + 1 + margines, (int) Math.ceil(y) + margines, i + 1 + margines, rozmiarHist - 1 + margines);
            }
        }
        HistogramDialog.sredniaValueLabel.setText("" + Math.round(srednia * 100) / 100.0);
        HistogramDialog.wariancjaValueLabel.setText("" + Math.round(wariancja * 100) / 100.0);
        HistogramDialog.maxValueOfHistogram.setText(maxP + " -");
    }

    public static void wczytajDane(int histogram) {
        for (int i = 0; i < daneNormal.length; i++) {
            daneNormal[i] = 0;
        }
        if (histogram == 0) {
            for (int x = 0; x < Obraz.image.getWidth(); x++) {
                for (int y = 0; y < Obraz.image.getHeight(); y++) {
                    int rgb = Obraz.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    daneNormal[r] += 1;
                }
            }
        } else if (histogram == 1) {
            for (int x = 0; x < Obraz.image.getWidth(); x++) {
                for (int y = 0; y < Obraz.image.getHeight(); y++) {
                    int rgb = Obraz.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int g = color.getGreen();
                    daneNormal[g] += 1;
                }
            }
        } else if (histogram == 2) {
            for (int x = 0; x < Obraz.image.getWidth(); x++) {
                for (int y = 0; y < Obraz.image.getHeight(); y++) {
                    int rgb = Obraz.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int b = color.getBlue();
                    daneNormal[b] += 1;
                }
            }
        } else if (histogram == 3) {
            double[] red = new double[256];
            double[] green = new double[256];
            double[] blue = new double[256];
            for (int x = 0; x < Obraz.image.getWidth(); x++) {
                for (int y = 0; y < Obraz.image.getHeight(); y++) {
                    int rgb = Obraz.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    red[r] += 1;
                    green[g] += 1;
                    blue[b] += 1;
                }
            }
            for (int i = 0; i < daneNormal.length; i++) {
                daneNormal[i] = konwertuj(red[i], green[i], blue[i]);
            }
        }
        maxPrawdopodobienstwo();
        wariancja();
    }

    public static double konwertuj(double red, double green, double blue) {
        return 0.2125 * red + 0.7154 * green + 0.0721 * blue;
    }

    private static void maxPrawdopodobienstwo() {
        double ilePixeli = Obraz.image.getWidth() * Obraz.image.getHeight();
        maxP = 0;
        srednia = 0;
        for (int i = 0; i < daneNormal.length; i++) {
            dane[i] = daneNormal[i];
            daneNormal[i] = daneNormal[i] / ilePixeli;
            srednia += (i + 1) * daneNormal[i];
            if (daneNormal[i] > maxP) {
                maxP = Math.ceil(daneNormal[i] * 110) / 100.0;
            }
        }
    }

    private static void wariancja() {
        wariancja = 0;
        for (int i = 0; i < daneNormal.length; i++) {
            wariancja += (i - srednia) * (i - srednia) * daneNormal[i];
        }
    }
}