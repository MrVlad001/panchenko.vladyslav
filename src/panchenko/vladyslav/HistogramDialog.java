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
    private final int widthHistDialog = 505;
    private final int heightHistDialog = 600;
    private JPanel sliderPanel = new JPanel(new GridLayout(5, 2, margines / 6, margines / 6));
    private JSlider[] slider = new JSlider[4];
    private JLabel[] sliderLabels = new JLabel[4];
    private int x1 = Histogram.sizeHistagram2 + margines;
    private int x2 = widthHistDialog - x1 - margines;
    private int y1 = margines;
    private int y2 = 20;
    private SavePanel savePanel;
    private String[] sliderText = new String[4];
    private int[] sliderValues = new int[4];
    private int aktywnyHist = -1;
    private double[] daneKulminacyjne = new double[256];
    private double stala = 255.0 / (Image.image.getWidth() * Image.image.getHeight());

    public HistogramDialog(Frame frame) {
        super(frame, "Histogram", false);
        setSize(widthHistDialog, heightHistDialog);
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
        
        sliderText[0] = "a = ";
        sliderText[1] = "b = ";
        sliderText[2] = "c = ";
        sliderText[3] = "d = ";
        sliderValues[0] = 0;
        sliderValues[1] = 255;
        sliderValues[2] = 0;
        sliderValues[3] = 255;
        
        for (int i = 0; i < 2; i++) {
            slider[i] = new JSlider(0, 255, sliderValues[i]);
            sliderPanel.add(slider[i]);
            slider[i].setPaintTicks(true);
            slider[i].addChangeListener(this);
        }
        for (int i = 0; i < 2; i++) {
            sliderLabels[i] = new JLabel(sliderText[i] + sliderValues[i]);
            sliderPanel.add(sliderLabels[i]);
            sliderLabels[i].setHorizontalAlignment(JLabel.CENTER);
            sliderLabels[i].setVerticalAlignment(JLabel.TOP);
        }
        for (int i = 2; i < 4; i++) {
            slider[i] = new JSlider(0, 255, sliderValues[i]);
            sliderPanel.add(slider[i]);
            slider[i].setPaintTicks(true);
            slider[i].addChangeListener(this);
        }
        for (int i = 2; i < 4; i++) {
            sliderLabels[i] = new JLabel(sliderText[i] + sliderValues[i]);
            sliderPanel.add(sliderLabels[i]);
            sliderLabels[i].setHorizontalAlignment(JLabel.CENTER);
            sliderLabels[i].setVerticalAlignment(JLabel.TOP);
        }
        slider[0].setMaximum(254);
        slider[1].setMinimum(1);
        sliderPanel.add(skalujButton);
        add(sliderPanel);
        sliderPanel.setBounds(margines, Histogram.sizeHistagram2 + margines / 2, Histogram.sizeHistagram2 - margines, odstep * 4);
        TitledBorder ramka;
        Border loweredbevel = BorderFactory.createLoweredBevelBorder();
        ramka = BorderFactory.createTitledBorder(loweredbevel, "Skalowanie aktywnego histogramu");
        ramka.setTitlePosition(TitledBorder.ABOVE_TOP);
        sliderPanel.setBorder(ramka);
        y1 += odstep / 2;
        savePanel = new SavePanel(this);
        add(savePanel);
        savePanel.setBounds((widthHistDialog - 300) / 2, y1, 300, y2);
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
            align();
        } else if (evt == skalujButton) {
            scan();
        }
        odswiezHistogram();
        add(histogramPanel);
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        Object evt = ce.getSource();
        if (evt == slider[0]) {
            if (slider[0].getValue() >= slider[1].getValue()) {
                slider[1].setValue(slider[0].getValue() + 1);
            }
        } else if (evt == slider[1]) {
            if (slider[1].getValue() <= slider[0].getValue()) {
                slider[0].setValue(slider[1].getValue() - 1);
            }
        } else if (evt == slider[2]) {
            if (slider[2].getValue() > slider[3].getValue()) {
                slider[3].setValue(slider[2].getValue());
            }
        } else if (evt == slider[3]) {
            if (slider[3].getValue() < slider[2].getValue()) {
                slider[2].setValue(slider[3].getValue());
            }
        }
        for (int i = 0; i < 4; i++) {
            sliderValues[i] = slider[i].getValue();
            sliderLabels[i].setText(sliderText[i] + sliderValues[i]);
        }
    }
    // wyrownac
    private void align() {
        daneKulminacyjne[0] = Histogram.data[0];
        for (int i = 1; i < 256; i++) {
            daneKulminacyjne[i] = Histogram.data[i] + daneKulminacyjne[i - 1];
        }
        if (aktywnyHist == 0) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = erase256((int) (stala * daneKulminacyjne[color.getRed()]));
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
                    int g = erase256((int) (stala * daneKulminacyjne[color.getGreen()]));
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
                    int b = erase256((int) (stala * daneKulminacyjne[color.getBlue()]));
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
                    int gray = erase256((int) (Math.round(Histogram.convert(r, g, b))));
                    double wspolczynnik = (stala * daneKulminacyjne[gray]) / gray;
                    r = erase256((int) (wspolczynnik * r));
                    g = erase256((int) (wspolczynnik * g));
                    b = erase256((int) (wspolczynnik * b));
                    rgb = jrgb(r, g, b);
                    Image.image.setRGB(x, y, rgb);
                }
            }
            odswiezHistogram();
        }
        Form.refresh();
    }

    private void scan() {
        if (aktywnyHist == 0) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    int rgb = Image.image.getRGB(x, y);
                    Color color = new Color(rgb, true);
                    int r = erase256((int) ((color.getRed() - sliderValues[0]) * (sliderValues[3] - sliderValues[2]) / (sliderValues[1] - sliderValues[0]) + sliderValues[2]));
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
                    int g = erase256((int) ((color.getGreen() - sliderValues[0]) * (sliderValues[3] - sliderValues[2]) / (sliderValues[1] - sliderValues[0]) + sliderValues[2]));
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
                    int b = erase256((int) ((color.getBlue() - sliderValues[0]) * (sliderValues[3] - sliderValues[2]) / (sliderValues[1] - sliderValues[0]) + sliderValues[2]));
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
                    double gray = Histogram.convert(r, g, b);
                    double wspolczynnik = ((gray - sliderValues[0]) * (sliderValues[3] - sliderValues[2]) / (sliderValues[1] - sliderValues[0]) + sliderValues[2]) / gray;
                    r = erase256((int) (wspolczynnik * r));
                    g = erase256((int) (wspolczynnik * g));
                    b = erase256((int) (wspolczynnik * b));
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

    public static int erase256(int color) {
        if (color > 255) {
            color = 255;
        } else if (color < 0) {
            color = 0;
        }
        return color;
    }

    public void odswiezHistogram() {
        Histogram.readData(aktywnyHist);
        histogramPanel.repaint();
        histogramPanel.revalidate();
        Form.refresh();
        this.repaint();
    }
    
}