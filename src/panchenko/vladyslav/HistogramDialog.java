package panchenko.vladyslav;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Vladyslav
 */
public class HistogramDialog extends JDialog implements ActionListener, ChangeListener {

    private JPanel histogramPanel;
    private JButton redHist = new JButton("Red");
    private JButton greenHist = new JButton("Green");
    private JButton blueHist = new JButton("Blue");
    private JButton grayHist = new JButton("Gray");
    private JButton wyrownajButton = new JButton("Wyrównaj aktywny");
    private JButton skalujButton = new JButton("Skaluj aktywny");
    private JLabel sredniaLabel = new JLabel("Średnia intens.: ");
    public static JLabel sredniaValueLabel = new JLabel();
    private JLabel wariancjaLabel = new JLabel("Wariancja: ");
    public static JLabel wariancjaValueLabel = new JLabel();
    public static JLabel maxValueOfHistogram = new JLabel();
    private final int odstep = 40;
    public static final int margines = 35;
    private final int szerokoscHistDialog = 505;
    private final int wysokoscHistDialog = 600;
    private JPanel suwakiPanel = new JPanel(new GridLayout(5, 2, margines / 6, margines / 6));
    private JSlider[] suwaki = new JSlider[4];
    private JLabel[] suwakiLabels = new JLabel[4];
    private int x1 = Histogram.histRozmiar + margines;
    private int x2 = szerokoscHistDialog - x1 - margines;
    private int y1 = margines;
    private int y2 = 20;
    private SavePanel savePanel;
    private String[] suwakiText = new String[4];
    private int[] suwakiValues = new int[4];
    private int aktywnyHist = -1;
    private double[] daneKulminacyjne = new double[256];
    private double stala = 255.0 / (Image.image.getWidth() * Image.image.getHeight());

    public HistogramDialog(Frame frame) {
        super(frame, "Histogram", false);
        setSize(szerokoscHistDialog, wysokoscHistDialog);
        setLayout(null);
        
        sredniaLabel.setHorizontalAlignment(JLabel.LEFT);
        wariancjaLabel.setHorizontalAlignment(JLabel.LEFT);
        sredniaValueLabel.setHorizontalAlignment(JLabel.RIGHT);
        wariancjaValueLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        y1 += odstep;
        redHist.setBounds(x1, y1, x2, y2);
        y1 += odstep;
        greenHist.setBounds(x1, y1, x2, y2);
        y1 += odstep;
        blueHist.setBounds(x1, y1, x2, y2);
        y1 += odstep;
        grayHist.setBounds(x1, y1, x2, y2);
        y1 += odstep;
        sredniaLabel.setBounds(x1, y1, x2, y2);
        y1 += odstep / 2;
        sredniaValueLabel.setBounds(x1, y1, x2, y2);
        y1 += odstep / 2;
        wariancjaLabel.setBounds(x1, y1, x2, y2);
        y1 += odstep / 2;
        wariancjaValueLabel.setBounds(x1, y1, x2, y2);
        y1 += 4 * odstep - 13;
        wyrownajButton.setBounds(x1, y1, x2, y2);
        y1 += odstep;
        
        redHist.addActionListener(this);
        greenHist.addActionListener(this);
        blueHist.addActionListener(this);
        grayHist.addActionListener(this);
        skalujButton.addActionListener(this);
        wyrownajButton.addActionListener(this);
        skalujButton.setEnabled(false);
        wyrownajButton.setEnabled(false);
        
        add(redHist);
        add(greenHist);
        add(blueHist);
        add(grayHist);
        add(sredniaLabel);
        add(sredniaValueLabel);
        add(wariancjaLabel);
        add(wariancjaValueLabel);
        add(wyrownajButton);
        histogramPanel = new JPanel();
        add(histogramPanel);
        maxValueOfHistogram.setBounds(0, margines - 11, margines, y2);
        add(maxValueOfHistogram);
        
        suwakiText[0] = "a = ";
        suwakiText[1] = "b = ";
        suwakiText[2] = "c = ";
        suwakiText[3] = "d = ";
        suwakiValues[0] = 0;
        suwakiValues[1] = 255;
        suwakiValues[2] = 0;
        suwakiValues[3] = 255;
        
        for (int i = 0; i < 2; i++) {
            suwaki[i] = new JSlider(0, 255, suwakiValues[i]);
            suwakiPanel.add(suwaki[i]);
            suwaki[i].setPaintTicks(true);
            suwaki[i].addChangeListener(this);
        }
        for (int i = 0; i < 2; i++) {
            suwakiLabels[i] = new JLabel(suwakiText[i] + suwakiValues[i]);
            suwakiPanel.add(suwakiLabels[i]);
            suwakiLabels[i].setHorizontalAlignment(JLabel.CENTER);
            suwakiLabels[i].setVerticalAlignment(JLabel.TOP);
        }
        for (int i = 2; i < 4; i++) {
            suwaki[i] = new JSlider(0, 255, suwakiValues[i]);
            suwakiPanel.add(suwaki[i]);
            suwaki[i].setPaintTicks(true);
            suwaki[i].addChangeListener(this);
        }
        for (int i = 2; i < 4; i++) {
            suwakiLabels[i] = new JLabel(suwakiText[i] + suwakiValues[i]);
            suwakiPanel.add(suwakiLabels[i]);
            suwakiLabels[i].setHorizontalAlignment(JLabel.CENTER);
            suwakiLabels[i].setVerticalAlignment(JLabel.TOP);
        }
        suwaki[0].setMaximum(254);
        suwaki[1].setMinimum(1);
        suwakiPanel.add(skalujButton);
        add(suwakiPanel);
        suwakiPanel.setBounds(margines, Histogram.histRozmiar + margines / 2, Histogram.histRozmiar - margines, odstep * 4);
        TitledBorder ramka;
        Border loweredbevel = BorderFactory.createLoweredBevelBorder();
        ramka = BorderFactory.createTitledBorder(loweredbevel, "Skalowanie aktywnego histogramu");
        ramka.setTitlePosition(TitledBorder.ABOVE_TOP);
        suwakiPanel.setBorder(ramka);
        y1 += odstep / 2;
        savePanel = new SavePanel(this);
        add(savePanel);
        savePanel.setBounds((szerokoscHistDialog - 300) / 2, y1, 300, y2);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                savePanel.anuluj();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object evt = ae.getSource();
        if (evt == redHist) {
            remove(histogramPanel);
            histogramPanel = new Histogram(0);
            aktywnyHist = 0;
            skalujButton.setEnabled(true);
            wyrownajButton.setEnabled(true);
        } else if (evt == greenHist) {
            remove(histogramPanel);
            histogramPanel = new Histogram(1);
            aktywnyHist = 1;
            skalujButton.setEnabled(true);
            wyrownajButton.setEnabled(true);
        } else if (evt == blueHist) {
            remove(histogramPanel);
            histogramPanel = new Histogram(2);
            aktywnyHist = 2;
            skalujButton.setEnabled(true);
            wyrownajButton.setEnabled(true);
        } else if (evt == grayHist) {
            remove(histogramPanel);
            histogramPanel = new Histogram(3);
            aktywnyHist = 3;
            skalujButton.setEnabled(true);
            wyrownajButton.setEnabled(true);
        } else if (evt == wyrownajButton) {
            wyrownaj();
        } else if (evt == skalujButton) {
            skaluj();
        }
        odswiezHistogram();
        add(histogramPanel);
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        Object evt = ce.getSource();
        if (evt == suwaki[0]) {
            if (suwaki[0].getValue() >= suwaki[1].getValue()) {
                suwaki[1].setValue(suwaki[0].getValue() + 1);
            }
        } else if (evt == suwaki[1]) {
            if (suwaki[1].getValue() <= suwaki[0].getValue()) {
                suwaki[0].setValue(suwaki[1].getValue() - 1);
            }
        } else if (evt == suwaki[2]) {
            if (suwaki[2].getValue() > suwaki[3].getValue()) {
                suwaki[3].setValue(suwaki[2].getValue());
            }
        } else if (evt == suwaki[3]) {
            if (suwaki[3].getValue() < suwaki[2].getValue()) {
                suwaki[2].setValue(suwaki[3].getValue());
            }
        }
        for (int i = 0; i < 4; i++) {
            suwakiValues[i] = suwaki[i].getValue();
            suwakiLabels[i].setText(suwakiText[i] + suwakiValues[i]);
        }
    }

    private void wyrownaj() {
        daneKulminacyjne[0] = Histogram.dane[0];
        for (int i = 1; i < 256; i++) {
            daneKulminacyjne[i] = Histogram.dane[i] + daneKulminacyjne[i - 1];
        }
        if (aktywnyHist == 0) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = obetnij256((int) (stala * daneKulminacyjne[color.getRed()]));
                    int g = color.getGreen();
                    int b = color.getBlue();
                    rgb = jrgb(r, g, b);
                    Image.image.setRGB(x, y, rgb);
                }
            }
            odswiezHistogram();
        } else if (aktywnyHist == 1) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = obetnij256((int) (stala * daneKulminacyjne[color.getGreen()]));
                    int b = color.getBlue();
                    rgb = jrgb(r, g, b);
                    Image.image.setRGB(x, y, rgb);
                }
            }
            odswiezHistogram();
        } else if (aktywnyHist == 2) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = obetnij256((int) (stala * daneKulminacyjne[color.getBlue()]));
                    rgb = jrgb(r, g, b);
                    Image.image.setRGB(x, y, rgb);
                }
            }
            odswiezHistogram();
        } else if (aktywnyHist == 3) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    int gray = obetnij256((int) (Math.round(Histogram.konwertuj(r, g, b))));
                    double wspolczynnik = (stala * daneKulminacyjne[gray]) / gray;
                    r = obetnij256((int) (wspolczynnik * r));
                    g = obetnij256((int) (wspolczynnik * g));
                    b = obetnij256((int) (wspolczynnik * b));
                    rgb = jrgb(r, g, b);
                    Image.image.setRGB(x, y, rgb);
                }
            }
            odswiezHistogram();
        }
        Form.refresh();
    }

    private void skaluj() {
        if (aktywnyHist == 0) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = obetnij256((int) ((color.getRed() - suwakiValues[0]) * (suwakiValues[3] - suwakiValues[2]) / (suwakiValues[1] - suwakiValues[0]) + suwakiValues[2]));
                    int g = color.getGreen();
                    int b = color.getBlue();
                    rgb = jrgb(r, g, b);
                    Image.image.setRGB(x, y, rgb);
                }
            }
            odswiezHistogram();
        } else if (aktywnyHist == 1) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = obetnij256((int) ((color.getGreen() - suwakiValues[0]) * (suwakiValues[3] - suwakiValues[2]) / (suwakiValues[1] - suwakiValues[0]) + suwakiValues[2]));
                    int b = color.getBlue();
                    rgb = jrgb(r, g, b);
                    Image.image.setRGB(x, y, rgb);
                }
            }
            odswiezHistogram();
        } else if (aktywnyHist == 2) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = obetnij256((int) ((color.getBlue() - suwakiValues[0]) * (suwakiValues[3] - suwakiValues[2]) / (suwakiValues[1] - suwakiValues[0]) + suwakiValues[2]));
                    rgb = jrgb(r, g, b);
                    Image.image.setRGB(x, y, rgb);
                }
            }
            odswiezHistogram();
        } else if (aktywnyHist == 3) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();
                    double gray = Histogram.konwertuj(r, g, b);
                    double wspolczynnik = ((gray - suwakiValues[0]) * (suwakiValues[3] - suwakiValues[2]) / (suwakiValues[1] - suwakiValues[0]) + suwakiValues[2]) / gray;
                    r = obetnij256((int) (wspolczynnik * r));
                    g = obetnij256((int) (wspolczynnik * g));
                    b = obetnij256((int) (wspolczynnik * b));
                    rgb = jrgb(r, g, b);
                    Image.image.setRGB(x, y, rgb);
                }
            }
            odswiezHistogram();
        }
        Form.refresh();
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

    public void odswiezHistogram() {
        Histogram.wczytajDane(aktywnyHist);
        histogramPanel.repaint();
        histogramPanel.revalidate();
        Form.refresh();
        this.repaint();
    }
    
}