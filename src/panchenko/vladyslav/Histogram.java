package panchenko.vladyslav;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author Vladyslav
 */
public final class Histogram extends JPanel {

    public static final int sizeHistogram = 257;
    private static double[] normalData = new double[256];
    public static double[] data = new double[256];
    private static double maxP;
    private static double srednia;
    private static double wariancja;
    private static int margines = HistogramDialog.margines;
    public static int sizeHistagram2 = sizeHistogram + 1 + margines;

    public Histogram(int h) {
        super();
        setSize(sizeHistagram2, sizeHistagram2);
        readData(h);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.white);
        g.drawLine(margines, margines, sizeHistagram2 - 1, margines);//horizontal
        g.drawLine(margines, margines, margines, sizeHistagram2 - 1); //vertical
        g.drawLine(sizeHistagram2 - 1, margines, sizeHistagram2 - 1, sizeHistagram2 - 1);
        g.drawLine(margines, sizeHistagram2 - 1, sizeHistagram2 - 1, sizeHistagram2 - 1);

        g.setColor(Color.black);
        for (int i = 0; i < normalData.length; i++) {
            double y = 256 - Math.round(normalData[i] * 256 / maxP);
            if (y != sizeHistogram - 1) {
                g.drawLine(i + 1 + margines, (int) Math.ceil(y) + margines, i + 1 + margines, sizeHistogram - 1 + margines);
            }
        }
        HistogramDialog.sredniaValueLabel.setText("" + Math.round(srednia * 100) / 100.0);
        HistogramDialog.wariancjaValueLabel.setText("" + Math.round(wariancja * 100) / 100.0);
        HistogramDialog.maxValueOfHistogram.setText(maxP + " -");
    }

    public static void readData(int histogram) {
        for (int i = 0; i < normalData.length; i++) {
            normalData[i] = 0;
        }
        if (histogram == 0) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    normalData[r] += 1;
                }
            }
        } else if (histogram == 1) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int g = color.getGreen();
                    normalData[g] += 1;
                }
            }
        } else if (histogram == 2) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int b = color.getBlue();
                    normalData[b] += 1;
                }
            }
        } else if (histogram == 3) {
            double[] red = new double[256];
            double[] green = new double[256];
            double[] blue = new double[256];
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    red[r] += 1;
                    green[g] += 1;
                    blue[b] += 1;
                }
            }
            for (int i = 0; i < normalData.length; i++) {
                normalData[i] = convert(red[i], green[i], blue[i]);
            }
        }
        maxPrawdopodobienstwo();
        wariancja();
    }

    public static double convert(double red, double green, double blue) {
        return 0.2125 * red + 0.7154 * green + 0.0721 * blue;
    }

    private static void maxPrawdopodobienstwo() {
        double ilePixeli = Image.image.getWidth() * Image.image.getHeight();
        maxP = 0;
        srednia = 0;
        for (int i = 0; i < normalData.length; i++) {
            data[i] = normalData[i];
            normalData[i] = normalData[i] / ilePixeli;
            srednia += (i + 1) * normalData[i];
            if (normalData[i] > maxP) {
                maxP = Math.ceil(normalData[i] * 110) / 100.0;
            }
        }
    }

    private static void wariancja() {
        wariancja = 0;
        for (int i = 0; i < normalData.length; i++) {
            wariancja += (i - srednia) * (i - srednia) * normalData[i];
        }
    }
}