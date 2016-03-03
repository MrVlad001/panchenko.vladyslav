package panchenko.vladyslav;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class FiltrSplotUnsharpMask extends FiltrPanel implements KeyListener {

    private double wspolczynnikG = 1;
    private double odchylenie = 1;
    private double wspolczynnikUM = 1;
    private double[][] redCopy = new double[Obraz.image.getWidth()][Obraz.image.getHeight()];
    private double[][] greenCopy = new double[Obraz.image.getWidth()][Obraz.image.getHeight()];
    private double[][] blueCopy = new double[Obraz.image.getWidth()][Obraz.image.getHeight()];

    public FiltrSplotUnsharpMask(JFrame parent) {
        super(parent, "Splot unsharpmask", 3, 1);
        polaLabels[0].setText("Odchylenie st.");
        polaLabels[1].setText("Współczynik");
        polaLabels[2].setText("Współ. użycia");
        polaFileds[0].setText("" + wspolczynnikG);
        polaFileds[1].setText("" + odchylenie);
        polaFileds[2].setText("" + wspolczynnikUM);
        polaFileds[0].addKeyListener((KeyListener) this);
        polaFileds[1].addKeyListener((KeyListener) this);
        rysujMaske();
    }

    @Override
    public void setMaskaValue(JFormattedTextField jFormattedTextField, int x, int y) {
        x = x - nrMaski;
        y = y - nrMaski;
        double value = -(x * x + y * y) / (2.0 * odchylenie * odchylenie);
        value = Math.round(wspolczynnikG * Math.pow(Math.E, value) * 100) / 100.0;
        jFormattedTextField.setText("" + value);
        jFormattedTextField.setEditable(false);
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        Object evt = ke.getSource();
        String tmp;
        if (evt == polaFileds[0]) {
            tmp = polaFileds[0].getText();
            if (!tmp.equals("")) {
                odchylenie = Fje.getNumber(tmp);
                rysujMaske();
            } else {
                odchylenie = 1;
            }
        } else if (evt == polaFileds[1]) {
            tmp = polaFileds[1].getText();
            if (!tmp.equals("")) {
                wspolczynnikG = Fje.getNumber(tmp);
                rysujMaske();
            } else {
                wspolczynnikG = 1;
            }
        }
    }

    @Override
    protected void filtrujButton() {
        polaFileds[0].setText("" + odchylenie);
        polaFileds[1].setText("" + wspolczynnikG);
        wspolczynnikUM = Fje.getNumber(polaFileds[2].getText());
        polaFileds[2].setText("" + wspolczynnikUM);
        for (int x = 0; x < Obraz.image.getWidth(); x++) {
            for (int y = 0; y < Obraz.image.getHeight(); y++) {
                obliczPixelWiersz(x, y);
            }
        }
        for (int x = 0; x < Obraz.image.getWidth(); x++) {
            for (int y = 0; y < Obraz.image.getHeight(); y++) {
                obliczPixelKolumna(x, y);
            }
        }
    }

    private void obliczPixel(int x, int y) {
        double r = 0;
        double g = 0;
        double b = 0;
        int m, n, rgb, r1, g1, b1;
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

        r1 = (int) (wspolczynnikUM * (red[x][y] - ((int) r)));
        g1 = (int) (wspolczynnikUM * (green[x][y] - ((int) g)));
        b1 = (int) (wspolczynnikUM * (blue[x][y] - ((int) b)));

        rgb = Fje.jrgb(Fje.obetnij256(red[x][y] + r1), Fje.obetnij256(green[x][y] + g1), Fje.obetnij256(blue[x][y] + b1));
        Obraz.image.setRGB(x, y, rgb);
    }

    private void obliczPixelWiersz(int x, int y) {
        double r = 0;
        double g = 0;
        double b = 0;
        int m;
        for (int i = 0; i < rozmMaski; i++) {
            m = odbicieLustrzane(x + i - nrMaski, 'x');
            r += red[m][y] * wartosciMaski[i][nrMaski];
            g += green[m][y] * wartosciMaski[i][nrMaski];
            b += blue[m][y] * wartosciMaski[i][nrMaski];
        }
        r /= sumaMaska;
        g /= sumaMaska;
        b /= sumaMaska;

        redCopy[x][y] = r;
        greenCopy[x][y] = g;
        blueCopy[x][y] = b;
    }

    private void obliczPixelKolumna(int x, int y) {
        double r = 0;
        double g = 0;
        double b = 0;
        int m, rgb;
        for (int i = 0; i < rozmMaski; i++) {
            m = odbicieLustrzane(y + i - nrMaski, 'y');
            r += redCopy[x][m] * wartosciMaski[i][nrMaski];
            g += greenCopy[x][m] * wartosciMaski[i][nrMaski];
            b += blueCopy[x][m] * wartosciMaski[i][nrMaski];
        }
        r /= sumaMaska;
        g /= sumaMaska;
        b /= sumaMaska;

        r = (wspolczynnikUM * (red[x][y] - r));
        g = (wspolczynnikUM * (green[x][y] - g));
        b = (wspolczynnikUM * (blue[x][y] - b));

        rgb = Fje.jrgb(Fje.obetnij256(red[x][y] + ((int) r)), Fje.obetnij256(green[x][y] + ((int) g)), Fje.obetnij256(blue[x][y] + ((int) b)));
        Obraz.image.setRGB(x, y, rgb);
    }
}