package panchenko.vladyslav;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class FilterSplotUnsharpMask extends FilterPanel implements KeyListener {

    private double wspolczynnikG = 1;
    private double odchylenie = 1;
    private double wspolczynnikUM = 1;
    private double[][] redCopy = new double[Image.image.getWidth()][Image.image.getHeight()];
    private double[][] greenCopy = new double[Image.image.getWidth()][Image.image.getHeight()];
    private double[][] blueCopy = new double[Image.image.getWidth()][Image.image.getHeight()];

    public FilterSplotUnsharpMask(JFrame parent) {
        super(parent, "Splot unsharpmask", 3, 1);
        fieldLabels[0].setText("Odchylenie st.");
        fieldLabels[1].setText("Współczynik");
        fieldLabels[2].setText("Współ. użycia");
        fields[0].setText("" + wspolczynnikG);
        fields[1].setText("" + odchylenie);
        fields[2].setText("" + wspolczynnikUM);
        fields[0].addKeyListener((KeyListener) this);
        fields[1].addKeyListener((KeyListener) this);
        printMask();
    }

    @Override
    public void setMaskValue(JFormattedTextField jFormattedTextField, int x, int y) {
        x = x - numMask;
        y = y - numMask;
        double value = -(x * x + y * y) / (2.0 * odchylenie * odchylenie);
        value = Math.round(wspolczynnikG * Math.pow(Math.E, value) * 100) / 100.0;
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
            }
        } else if (evt == fields[1]) {
            tmp = fields[1].getText();
            if (!tmp.equals("")) {
                wspolczynnikG = getNumber(tmp);
                printMask();
            } else {
                wspolczynnikG = 1;
            }
        }
    }

    @Override
    protected void filterButton() {
        fields[0].setText("" + odchylenie);
        fields[1].setText("" + wspolczynnikG);
        wspolczynnikUM = getNumber(fields[2].getText());
        fields[2].setText("" + wspolczynnikUM);
        for (int x = 0; x < Image.image.getWidth(); x++) {
            for (int y = 0; y < Image.image.getHeight(); y++) {
                obliczPixelWiersz(x, y);
            }
        }
        for (int x = 0; x < Image.image.getWidth(); x++) {
            for (int y = 0; y < Image.image.getHeight(); y++) {
                obliczPixelKolumna(x, y);
            }
        }
    }

    private void obliczPixel(int x, int y) {
        double r = 0;
        double g = 0;
        double b = 0;
        int m, n, rgb, r1, g1, b1;
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

        r1 = (int) (wspolczynnikUM * (red[x][y] - ((int) r)));
        g1 = (int) (wspolczynnikUM * (green[x][y] - ((int) g)));
        b1 = (int) (wspolczynnikUM * (blue[x][y] - ((int) b)));

        rgb = jrgb(obetnij256(red[x][y] + r1), obetnij256(green[x][y] + g1), obetnij256(blue[x][y] + b1));
        Image.image.setRGB(x, y, rgb);
    }

    private void obliczPixelWiersz(int x, int y) {
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
    
    public static int obetnij256(int color) {
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

    private void obliczPixelKolumna(int x, int y) {
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

        r = (wspolczynnikUM * (red[x][y] - r));
        g = (wspolczynnikUM * (green[x][y] - g));
        b = (wspolczynnikUM * (blue[x][y] - b));

        rgb = jrgb(obetnij256(red[x][y] + ((int) r)), obetnij256(green[x][y] + ((int) g)), obetnij256(blue[x][y] + ((int) b)));
        Image.image.setRGB(x, y, rgb);
    }
}