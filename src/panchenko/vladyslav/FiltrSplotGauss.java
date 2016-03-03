package panchenko.vladyslav;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class FiltrSplotGauss extends FiltrPanel implements KeyListener {

    private double wspolczynnik = 1;
    private double odchylenie = 1;
    private double[][] redCopy = new double[Obraz.image.getWidth()][Obraz.image.getHeight()];
    private double[][] greenCopy = new double[Obraz.image.getWidth()][Obraz.image.getHeight()];
    private double[][] blueCopy = new double[Obraz.image.getWidth()][Obraz.image.getHeight()];

    public FiltrSplotGauss(JFrame parent) {
        super(parent, "Splot gaussowski", 2, 1);
        polaLabels[0].setText("Odchylenie st.");
        polaLabels[1].setText("Współczynik");
        polaFileds[0].setText("" + wspolczynnik);
        polaFileds[1].setText("" + odchylenie);
        polaFileds[0].addKeyListener((KeyListener) this);
        polaFileds[1].addKeyListener((KeyListener) this);
        rysujMaske();
    }

    @Override
    public void setMaskaValue(JFormattedTextField jFormattedTextField, int x, int y) {
        x = x - nrMaski;
        y = y - nrMaski;
        double value = -(x * x + y * y) / (2.0 * odchylenie * odchylenie);
        value = Math.round(wspolczynnik * Math.pow(Math.E, value) * 100) / 100.0;

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
                rysujMaske();
            }
        } else if (evt == polaFileds[1]) {
            tmp = polaFileds[1].getText();
            if (!tmp.equals("")) {
                wspolczynnik = Fje.getNumber(tmp);
                rysujMaske();
            } else {
                wspolczynnik = 1;
                rysujMaske();
            }
        }
    }

    @Override
    protected void filtrujButton() {
        polaFileds[0].setText("" + odchylenie);
        polaFileds[1].setText("" + wspolczynnik);
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

        rgb = Fje.jrgb(Fje.obetnij256((int) r), Fje.obetnij256((int) g), Fje.obetnij256((int) b));
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

        rgb = Fje.jrgb(Fje.obetnij256((int) r), Fje.obetnij256((int) g), Fje.obetnij256((int) b));
        Obraz.image.setRGB(x, y, rgb);
    }
}