package panchenko.vladyslav;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class FiltrMinimum extends FiltrPanel implements KeyListener {

    private int wypelnijValue = 1;

    public FiltrMinimum(JFrame parent) {
        super(parent, "Filtr minimum", 1, 0);
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
                prev = Fje.getNumber(tmp);
                wypelnijValue = (int) Fje.limitNumber(prev, 0, 1, 0);
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
            for (int i = 0; i < rozmMaski; i++) {
                for (int j = 0; j < rozmMaski; j++) {
                    tmp = (int) Fje.limitNumber(wartosciMaski[i][j], 0, 1, 0);
                    wartosciMaski[i][j] = tmp;
                    maska[j][i].setText("" + tmp);
                }
            }
            for (int x = 0; x < Obraz.image.getWidth(); x++) {
                for (int y = 0; y < Obraz.image.getHeight(); y++) {
                    obliczPixel(x, y);
                }
            }
        } else {
            Obraz.wypelnij(0);
        }
    }

    private void obliczPixel(int x, int y) {
        int r = 0, g = 0, b = 0, m, n, rgb, minR = 255, minG = 255, minB = 255;
        for (int i = 0; i < rozmMaski; i++) {
            for (int j = 0; j < rozmMaski; j++) {
                if (wartosciMaski[i][j] != 0) {
                    m = odbicieLustrzane(x + i - nrMaski, 'x');
                    n = odbicieLustrzane(y + j - nrMaski, 'y');
                    r = red[m][n];
                    g = green[m][n];
                    b = blue[m][n];
                    minR = Math.min(minR, r);
                    minG = Math.min(minG, g);
                    minB = Math.min(minB, b);
                }
            }
        }
        rgb = Fje.jrgb(Fje.obetnij256(minR), Fje.obetnij256(minG), Fje.obetnij256(minB));
        Obraz.image.setRGB(x, y, rgb);
    }
}