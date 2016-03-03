package panchenko.vladyslav;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class FiltrMediana extends FiltrPanel implements KeyListener {

    private int wypelnijValue = 1;
    private int[] tabelaWartosciR = new int[256];
    private int[] tabelaWartosciG = new int[256];
    private int[] tabelaWartosciB = new int[256];
    private int srodkowaWartoscMaski = 0;
    private int[][] rMaskaValue;
    private int[][] gMaskaValue;
    private int[][] bMaskaValue;
    private int superZnacznik = 0;

    public FiltrMediana(JFrame parent) {
        super(parent, "Filtr mediana", 1, 0);
        polaLabels[0].setText("Wypełnij");
        polaFileds[0].setText("");
        polaFileds[0].addKeyListener((KeyListener) this);
        rysujMaske();
    }

    @Override
    public void setMaskaValue(JFormattedTextField jFormattedTextField, int x, int y) {
        jFormattedTextField.setText("" + wypelnijValue);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        Object evt = ke.getSource();
        if (evt == polaFileds[0]) {
            String tmp;
            double prev;
            tmp = polaFileds[0].getText();
            if (!tmp.equals("")) {
                prev = getNumber(tmp);
                wypelnijValue = (int) limitNumber(prev, 0, 1, 0);
                if (prev != wypelnijValue) {
                    polaFileds[0].setText("" + wypelnijValue);
                }
            } else {
                wypelnijValue = 0;
            }
            rysujMaske();
        }
    }

    @Override
    protected void filtrujButton() {
        polaFileds[0].setText("");
        if (maskaNiePusta) {
            int tmp;
            rMaskaValue = new int[rozmMaski][rozmMaski];
            gMaskaValue = new int[rozmMaski][rozmMaski];
            bMaskaValue = new int[rozmMaski][rozmMaski];
            sumaMaska = 0;
            for (int i = 0; i < rozmMaski; i++) {
                rMaskaValue[i] = new int[rozmMaski];
                gMaskaValue[i] = new int[rozmMaski];
                bMaskaValue[i] = new int[rozmMaski];
                for (int j = 0; j < rozmMaski; j++) {
                    tmp = (int) limitNumber(wartosciMaski[i][j], 0, 1, 0);
                    wartosciMaski[i][j] = tmp;
                    maska[j][i].setText("" + tmp);
                    sumaMaska += tmp;
                }
            }
            setStodkowaWartoscMaski();
            if (sumaMaska == rozmMaski * rozmMaski) {
                for (int x = 0; x < Obraz.image.getWidth(); x++) {
                    for (int y = 0; y < Obraz.image.getHeight(); y++) {
                        if (y == 0) {
                            obliczPierwszyPixel(x, y);
                        } else {
                            obliczPixelOptimal(x, y);
                        }
                    }
                }
            } else {
                for (int x = 0; x < Obraz.image.getWidth(); x++) {
                    for (int y = 0; y < Obraz.image.getHeight(); y++) {
                        obliczPixel(x, y);
                    }
                }
            }
        }
    }

    private void obliczPierwszyPixel(int x, int y) {
        int r, g, b, m, n, rgb;
        zerujTabeleWartosci();

        for (int i = 0; i < rozmMaski; i++) {
            for (int j = 0; j < rozmMaski; j++) {
                m = odbicieLustrzane(x + i - nrMaski, 'x');
                n = odbicieLustrzane(y + j - nrMaski, 'y');
                rMaskaValue[i][j] = r = red[m][n];
                gMaskaValue[i][j] = g = green[m][n];
                bMaskaValue[i][j] = b = blue[m][n];
                tabelaWartosciR[r]++;
                tabelaWartosciG[g]++;
                tabelaWartosciB[b]++;
            }
        }
        rgb = mediana();
        Obraz.image.setRGB(x, y, rgb);
        superZnacznik = 0;
    }

    private void obliczPixelOptimal(int x, int y) {
        int r, g, b, m, n, rgb;
        int i = rozmMaski - 1;
        n = odbicieLustrzane(y + i - nrMaski, 'y');

        for (int j = 0; j < rozmMaski; j++) {
            r = rMaskaValue[j][superZnacznik];
            g = gMaskaValue[j][superZnacznik];
            b = bMaskaValue[j][superZnacznik];
            tabelaWartosciR[r]--;
            tabelaWartosciG[g]--;
            tabelaWartosciB[b]--;
            m = odbicieLustrzane(x + j - nrMaski, 'x');
            rMaskaValue[j][superZnacznik] = r = red[m][n];
            gMaskaValue[j][superZnacznik] = g = green[m][n];
            bMaskaValue[j][superZnacznik] = b = blue[m][n];
            tabelaWartosciR[r]++;
            tabelaWartosciG[g]++;
            tabelaWartosciB[b]++;
        }
        rgb = mediana();
        Obraz.image.setRGB(x, y, rgb);
        superZnacznik++;
        superZnacznik %= rozmMaski;
    }

    private void obliczPixel(int x, int y) {
        int r, g, b, m, n, rgb;
        zerujTabeleWartosci();

        for (int i = 0; i < rozmMaski; i++) {
            for (int j = 0; j < rozmMaski; j++) {
                if (wartosciMaski[i][j] != 0) {
                    m = odbicieLustrzane(x + i - nrMaski, 'x');
                    n = odbicieLustrzane(y + j - nrMaski, 'y');
                    r = red[m][n];
                    g = green[m][n];
                    b = blue[m][n];
                    tabelaWartosciR[r]++;
                    tabelaWartosciG[g]++;
                    tabelaWartosciB[b]++;
                }
            }
        }
        rgb = mediana();
        Obraz.image.setRGB(x, y, rgb);
        superZnacznik = 0;
    }

    private int mediana() {
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
        if (sumaMaska % 2 == 1) {
            srodkowaWartoscMaski = (int) Math.floor(sumaMaska / 2.0) + 1;
        } else {
            srodkowaWartoscMaski = (int) Math.floor(sumaMaska / 2.0);
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