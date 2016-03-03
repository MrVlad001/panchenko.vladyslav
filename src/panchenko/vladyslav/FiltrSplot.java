package panchenko.vladyslav;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class FiltrSplot extends FiltrPanel {

    private double wypelnijValue = 0;
    private String wypelnijToPrint = "0,0";

    public FiltrSplot(JFrame parent) {
        super(parent, "Funkcja splotu", 1, 2);
        polaLabels[0].setText("Wypełnij");
        polaFileds[0].setText("");
        polaFileds[0].addKeyListener((KeyListener) this);
        rysujMaske();
    }

    @Override
    public void setMaskaValue(JFormattedTextField jFormattedTextField, int x, int y) {
        jFormattedTextField.setText(wypelnijToPrint);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        Object evt = ke.getSource();
        String tmp;
        double prev;
        boolean refresh = false;
        if (evt == polaFileds[0]) {
            tmp = polaFileds[0].getText();
            if (tmp.contains(".") && tmp.substring(tmp.lastIndexOf(".")).length() > 1) {
                refresh = true;
            }
            if (!tmp.equals("") && !tmp.equals("-")) {
                prev = getNumber(tmp);
                wypelnijValue = limitNumber(prev, -1000, 1000, 1);
                tmp = "" + wypelnijValue;
                wypelnijToPrint = tmp.replace(".", ",");
                if (prev != wypelnijValue || refresh) {
                    tmp = "" + wypelnijValue;
                    wypelnijToPrint = tmp.replace(".", ",");
                    polaFileds[0].setText(wypelnijToPrint);
                }
                rysujMaske();
            }
        }
    }

    @Override
    protected void filtrujButton() {
        polaFileds[0].setText("");
        for (int x = 0; x < Obraz.image.getWidth(); x++) {
            for (int y = 0; y < Obraz.image.getHeight(); y++) {
                obliczPixel(x, y);
            }
        }
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
    
    public static double limitNumber(double number, double limitDown, double limitUp, int decimalNumber) {
        if (number > limitUp) {
            number = limitUp;
        } else if (number < limitDown) {
            number = limitDown;
        } else {
            double tmp = Math.pow(10, decimalNumber);
            number = Math.round(number * tmp) / tmp;
        }
        return number;
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

    private void obliczPixel(int x, int y) {
        double r = 0;
        double g = 0;
        double b = 0;
        int m, n, rgb;
        for (int i = 0; i < rozmMaski; i++) {
            for (int j = 0; j < rozmMaski; j++) {
                m = odbicieLustrzane(x + i - nrMaski, 'x');
                n = odbicieLustrzane(y + j - nrMaski, 'y');

                r += red[m][n] * wartosciMaski[i][j];
                g += green[m][n] * wartosciMaski[i][j];
                b += blue[m][n] * wartosciMaski[i][j];
            }
        }
        r /= sumaMaska;
        g /= sumaMaska;
        b /= sumaMaska;

        rgb = jrgb(obetnij256((int) r), obetnij256((int) g), obetnij256((int) b));
        Obraz.image.setRGB(x, y, rgb);
    }
}