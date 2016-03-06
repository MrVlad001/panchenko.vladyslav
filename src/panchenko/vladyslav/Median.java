package panchenko.vladyslav;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class Median extends FilterPanel implements KeyListener {

    private int wypelnijValue = 1;
    private int[] tabelaWartosciR = new int[256];
    private int[] tabelaWartosciG = new int[256];
    private int[] tabelaWartosciB = new int[256];
    private int srodkowaWartoscMaski = 0;
    private int[][] rMaskValue;
    private int[][] gMaskValue;
    private int[][] bMaskValue;
    private int superZnacznik = 0;

    public Median(JFrame parent) {
        super(parent, "Filtr mediana", 1, 0);
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
            int tmp;
            rMaskValue = new int[sizeMask][sizeMask];
            gMaskValue = new int[sizeMask][sizeMask];
            bMaskValue = new int[sizeMask][sizeMask];
            sumMask = 0;
            for (int i = 0; i < sizeMask; i++) {
                rMaskValue[i] = new int[sizeMask];
                gMaskValue[i] = new int[sizeMask];
                bMaskValue[i] = new int[sizeMask];
                for (int j = 0; j < sizeMask; j++) {
                    tmp = (int) limitNumber(valueMask[i][j], 0, 1, 0);
                    valueMask[i][j] = tmp;
                    mask[j][i].setText("" + tmp);
                    sumMask += tmp;
                }
            }
            setStodkowaWartoscMaski();
            if (sumMask == sizeMask * sizeMask) {
                for (int x = 0; x < Image.image.getWidth(); x++) {
                    for (int y = 0; y < Image.image.getHeight(); y++) {
                        if (y == 0) {
                            calculateFirstPixel(x, y);
                        } else {
                            calculatePixelOptimal(x, y);
                        }
                    }
                }
            } else {
                for (int x = 0; x < Image.image.getWidth(); x++) {
                    for (int y = 0; y < Image.image.getHeight(); y++) {
                        calculatePixel(x, y);
                    }
                }
            }
        }
    }

    private void calculateFirstPixel(int x, int y) {
        int r, g, b, m, n, rgb;
        zerujTabeleWartosci();

        for (int i = 0; i < sizeMask; i++) {
            for (int j = 0; j < sizeMask; j++) {
                m = mirrorReflection(x + i - numMask, 'x');
                n = mirrorReflection(y + j - numMask, 'y');
                rMaskValue[i][j] = r = red[m][n];
                gMaskValue[i][j] = g = green[m][n];
                bMaskValue[i][j] = b = blue[m][n];
                tabelaWartosciR[r]++;
                tabelaWartosciG[g]++;
                tabelaWartosciB[b]++;
            }
        }
        rgb = median();
        Image.image.setRGB(x, y, rgb);
        superZnacznik = 0;
    }

    private void calculatePixelOptimal(int x, int y) {
        int r, g, b, m, n, rgb;
        int i = sizeMask - 1;
        n = mirrorReflection(y + i - numMask, 'y');

        for (int j = 0; j < sizeMask; j++) {
            r = rMaskValue[j][superZnacznik];
            g = gMaskValue[j][superZnacznik];
            b = bMaskValue[j][superZnacznik];
            tabelaWartosciR[r]--;
            tabelaWartosciG[g]--;
            tabelaWartosciB[b]--;
            m = mirrorReflection(x + j - numMask, 'x');
            rMaskValue[j][superZnacznik] = r = red[m][n];
            gMaskValue[j][superZnacznik] = g = green[m][n];
            bMaskValue[j][superZnacznik] = b = blue[m][n];
            tabelaWartosciR[r]++;
            tabelaWartosciG[g]++;
            tabelaWartosciB[b]++;
        }
        rgb = median();
        Image.image.setRGB(x, y, rgb);
        superZnacznik++;
        superZnacznik %= sizeMask;
    }

    private void calculatePixel(int x, int y) {
        int r, g, b, m, n, rgb;
        zerujTabeleWartosci();

        for (int i = 0; i < sizeMask; i++) {
            for (int j = 0; j < sizeMask; j++) {
                if (valueMask[i][j] != 0) {
                    m = mirrorReflection(x + i - numMask, 'x');
                    n = mirrorReflection(y + j - numMask, 'y');
                    r = red[m][n];
                    g = green[m][n];
                    b = blue[m][n];
                    tabelaWartosciR[r]++;
                    tabelaWartosciG[g]++;
                    tabelaWartosciB[b]++;
                }
            }
        }
        rgb = median();
        Image.image.setRGB(x, y, rgb);
        superZnacznik = 0;
    }

    private int median() {
        int r, g, b, rgb, tmp = 0;
        int rSrodWartTMP = srodkowaWartoscMaski;
        int gSrodWartTMP = srodkowaWartoscMaski;
        int bSrodWartTMP = srodkowaWartoscMaski;
        r = g = b = -1;

        while (rSrodWartTMP > 0 || gSrodWartTMP > 0 || bSrodWartTMP > 0) {
            rSrodWartTMP -= tabelaWartosciR[tmp];
            gSrodWartTMP -= tabelaWartosciG[tmp];
            bSrodWartTMP -= tabelaWartosciB[tmp];
            if (rSrodWartTMP <= 0 && r == -1) {
                r = tmp;
            }
            if (gSrodWartTMP <= 0 && g == -1) {
                g = tmp;
            }
            if (bSrodWartTMP <= 0 && b == -1) {
                b = tmp;
            }
            tmp++;
        }
        rgb = jrgb(r, g, b);
        return rgb;
    }

    private void zerujTabeleWartosci() {
        for (int i = 0; i < tabelaWartosciR.length; i++) {
            tabelaWartosciR[i] = 0;
            tabelaWartosciG[i] = 0;
            tabelaWartosciB[i] = 0;
        }
    }

    private void setStodkowaWartoscMaski() {
        if (sumMask % 2 == 1) {
            srodkowaWartoscMaski = (int) Math.floor(sumMask / 2.0) + 1;
        } else {
            srodkowaWartoscMaski = (int) Math.floor(sumMask / 2.0);
        }

    }
    
     public static int jrgb(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
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