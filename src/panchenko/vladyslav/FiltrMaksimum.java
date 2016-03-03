package panchenko.vladyslav;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class FiltrMaksimum extends FiltrPanel implements KeyListener {

    private int wypelnijValue = 1;

    public FiltrMaksimum(JFrame parent) {
        super(parent, "Filtr maksimum", 1, 0);
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
            int tmp = 0;
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
            Obraz.wypelnij(255);
        }
    }

    private void obliczPixel(int x, int y) {
        int r = 0, g = 0, b = 0, m, n, rgb, maxR = 0, maxG = 0, maxB = 0;
        for (int i = 0; i < rozmMaski; i++) {
            for (int j = 0; j < rozmMaski; j++) {
                if (wartosciMaski[i][j] != 0) {
                    m = odbicieLustrzane(x + i - nrMaski, 'x');
                    n = odbicieLustrzane(y + j - nrMaski, 'y');
                    r = red[m][n];
                    g = green[m][n];
                    b = blue[m][n];
                    maxR = Math.max(maxR, r);
                    maxG = Math.max(maxG, g);
                    maxB = Math.max(maxB, b);
                }
            }
        }
        rgb = Fje.jrgb(Fje.obetnij256(maxR), Fje.obetnij256(maxG), Fje.obetnij256(maxB));
        Obraz.image.setRGB(x, y, rgb);
    }
}