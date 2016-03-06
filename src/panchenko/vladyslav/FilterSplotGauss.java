package panchenko.vladyslav;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class FilterSplotGauss extends FilterPanel implements KeyListener {

    private double wspolczynnik = 1;
    private double odchylenie = 1;
    private double[][] redCopy = new double[Image.image.getWidth()][Image.image.getHeight()];
    private double[][] greenCopy = new double[Image.image.getWidth()][Image.image.getHeight()];
    private double[][] blueCopy = new double[Image.image.getWidth()][Image.image.getHeight()];

    public FilterSplotGauss(JFrame parent) {
        super(parent, "Splot gaussowski", 2, 1);
        fieldLabels[0].setText("Odchylenie st.");
        fieldLabels[1].setText("Współczynik");
        fields[0].setText("" + wspolczynnik);
        fields[1].setText("" + odchylenie);
        fields[0].addKeyListener((KeyListener) this);
        fields[1].addKeyListener((KeyListener) this);
        printMask();
    }

    @Override
    public void setMaskValue(JFormattedTextField jFormattedTextField, int x, int y) {
        x = x - numMask;
        y = y - numMask;
        double value = -(x * x + y * y) / (2.0 * odchylenie * odchylenie);
        value = Math.round(wspolczynnik * Math.pow(Math.E, value) * 100) / 100.0;

        jFormattedTextField.setText("" + value);
        jFormattedTextField.setEditable(false);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        Object evt = ke.getSource();
        String tmp;
        if (evt == fields[0]) {
            tmp = fields[0].getText();
            if (!tmp.equals("")) {
                odchylenie = getNumber(tmp);
                printMask();
            } else {
                odchylenie = 1;
                printMask();
            }
        } else if (evt == fields[1]) {
            tmp = fields[1].getText();
            if (!tmp.equals("")) {
                wspolczynnik = getNumber(tmp);
                printMask();
            } else {
                wspolczynnik = 1;
                printMask();
            }
        }
    }

    @Override
    protected void filterButton() {
        fields[0].setText("" + odchylenie);
        fields[1].setText("" + wspolczynnik);
        for (int x = 0; x < Image.image.getWidth(); x++) {
            for (int y = 0; y < Image.image.getHeight(); y++) {
                calculatePixelRow(x, y);
            }
        }
        for (int x = 0; x < Image.image.getWidth(); x++) {
            for (int y = 0; y < Image.image.getHeight(); y++) {
                calculatePixelColumn(x, y);
            }
        }
    }

    private void calculatePixel(int x, int y) {
        double r = 0;
        double g = 0;
        double b = 0;
        int m, n, rgb;
        for (int i = 0; i < sizeMask; i++) {
            for (int j = 0; j < sizeMask; j++) {
                m = mirrorReflection(x + i - numMask, 'x');
                n = mirrorReflection(y + j - numMask, 'y');

                r += red[m][n] * valueMask[i][j];
                g += green[m][n] * valueMask[i][j];
                b += blue[m][n] * valueMask[i][j];
            }
        }
        r /= sumMask;
        g /= sumMask;
        b /= sumMask;

        rgb = jrgb(erase256((int) r), erase256((int) g), erase256((int) b));
        Image.image.setRGB(x, y, rgb);
    }
    // oblicz pixel wiersz
    private void calculatePixelRow(int x, int y) {
        double r = 0;
        double g = 0;
        double b = 0;
        int m;
        for (int i = 0; i < sizeMask; i++) {
            m = mirrorReflection(x + i - numMask, 'x');
            r += red[m][y] * valueMask[i][numMask];
            g += green[m][y] * valueMask[i][numMask];
            b += blue[m][y] * valueMask[i][numMask];
        }
        r /= sumMask;
        g /= sumMask;
        b /= sumMask;

        redCopy[x][y] = r;
        greenCopy[x][y] = g;
        blueCopy[x][y] = b;
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
    
      public static double getNumber(String text) {
        double result = 0;
        if (!text.equals("")) {
            text = text.replace(",", ".");
            if (text.equals("-")) {
                result = 0;
            } else {
                result = Double.parseDouble(text);
            }
        }
        return result;
    }
    // oblicz pixel kolumna
    private void calculatePixelColumn(int x, int y) {
        double r = 0;
        double g = 0;
        double b = 0;
        int m, rgb;
        for (int i = 0; i < sizeMask; i++) {
            m = mirrorReflection(y + i - numMask, 'y');
            r += redCopy[x][m] * valueMask[i][numMask];
            g += greenCopy[x][m] * valueMask[i][numMask];
            b += blueCopy[x][m] * valueMask[i][numMask];
        }
        r /= sumMask;
        g /= sumMask;
        b /= sumMask;

        rgb = jrgb(erase256((int) r), erase256((int) g), erase256((int) b));
        Image.image.setRGB(x, y, rgb);
    }   
}