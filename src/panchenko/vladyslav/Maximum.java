package panchenko.vladyslav;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class Maximum extends FilterPanel implements KeyListener {

    private int wypelnijValue = 1;

    public Maximum(JFrame parent) {
        super(parent, "Filtr maksimum", 1, 0);
        fieldLabels[0].setText("Wypełnij");
        fields[0].setText("");
        fields[0].addKeyListener((KeyListener) this);
        printMask();
    }

    @Override
    public void setMaskValue(JFormattedTextField jFormattedTextField, int x, int y) {
        jFormattedTextField.setText("" + wypelnijValue);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        Object evt = ke.getSource();
        if (evt == fields[0]) {
            String tmp;
            double prev;
            tmp = fields[0].getText();
            if (!tmp.equals("")) {
                prev = getNumber(tmp);
                wypelnijValue = (int) limitNumber(prev, 0, 1, 0);
                if (prev != wypelnijValue) {
                    fields[0].setText("" + wypelnijValue);
                }
            } else {
                wypelnijValue = 0;
            }
            printMask();
        }
    }

    @Override
    protected void filterButton() {
        fields[0].setText("");
        if (notFullMask) {
            int tmp = 0;
            for (int i = 0; i < sizeMask; i++) {
                for (int j = 0; j < sizeMask; j++) {
                    tmp = (int) limitNumber(valueMask[i][j], 0, 1, 0);
                    valueMask[i][j] = tmp;
                    mask[j][i].setText("" + tmp);
                }
            }
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    calculatePixel(x, y);
                }
            }
        } else {
            Image.writeData(255);
        }
    }
    

    private void calculatePixel(int x, int y) {
        int r = 0, g = 0, b = 0, m, n, rgb, maxR = 0, maxG = 0, maxB = 0;
        for (int i = 0; i < sizeMask; i++) {
            for (int j = 0; j < sizeMask; j++) {
                if (valueMask[i][j] != 0) {
                    m = mirrorReflection(x + i - numMask, 'x');
                    n = mirrorReflection(y + j - numMask, 'y');
                    r = red[m][n];
                    g = green[m][n];
                    b = blue[m][n];
                    maxR = Math.max(maxR, r);
                    maxG = Math.max(maxG, g);
                    maxB = Math.max(maxB, b);
                }
            }
        }
        rgb = jrgb(erase256(maxR), erase256(maxG), erase256(maxB));
        Image.image.setRGB(x, y, rgb);
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
}