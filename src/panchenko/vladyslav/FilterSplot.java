package panchenko.vladyslav;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class FilterSplot extends FilterPanel {

    private double wypelnijValue = 0;
    private String wypelnijToPrint = "0,0";

    public FilterSplot(JFrame parent) {
        super(parent, "Funkcja splotu", 1, 2);
        fieldLabels[0].setText("Wypełnij");
        fields[0].setText("");
        fields[0].addKeyListener((KeyListener) this);
        printMask();
    }

    @Override
    public void setMaskValue(JFormattedTextField jFormattedTextField, int x, int y) {
        jFormattedTextField.setText(wypelnijToPrint);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        Object evt = ke.getSource();
        String tmp;
        double prev;
        boolean refresh = false;
        if (evt == fields[0]) {
            tmp = fields[0].getText();
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
                    fields[0].setText(wypelnijToPrint);
                }
                printMask();
            }
        }
    }

    @Override
    protected void filterButton() {
        fields[0].setText("");
        for (int x = 0; x < Image.image.getWidth(); x++) {
            for (int y = 0; y < Image.image.getHeight(); y++) {
                calculatePixel(x, y);
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
    // oblicz pixel
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
}