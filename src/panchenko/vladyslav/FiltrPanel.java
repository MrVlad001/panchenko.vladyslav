package panchenko.vladyslav;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Vladyslav
 */
public class FiltrPanel extends JDialog implements ChangeListener, ActionListener, KeyListener {

    private final int margines = 15;
    private final int wysokoscLabel = 20;
    private int wysokoscDialog = 360;
    private final int szerokoscDialog = 300;
    private JSlider suwak = new JSlider();
    private JLabel suwakLabels = new JLabel("Rozmiar maski");
    private JLabel suwakValuesLabels = new JLabel();
    private int wysokoscSuwakPanel;
    private int wysokoscPolaPanel = 0;
    protected int ilePol = 1;
    protected JPanel polaPanel;
    protected JLabel[] polaLabels;
    protected JFormattedTextField[] polaFileds;
    protected int x1, y1, x2, y2;
    private JPanel maskaBox = new JPanel();
    private JScrollPane maskaScroll = new JScrollPane();
    private JPanel maskaPanel = new JPanel();
    private int wysokoscMaskaPanel = (szerokoscDialog - 7 * margines);
    protected int nrMaski = 1;
    protected int rozmMaski = 3;
    protected double[][] wartosciMaski;
    protected double[] wartosciMaski2;
    protected double sumaMaska;
    protected JFormattedTextField[][] maska = new JFormattedTextField[1][1];
    private SavePanel savePanel;
    private JButton filtrujButton = new JButton("Filtruj");
    private int x1filtrujButton, y1filtrujButton, x2filtrujButton, y2filtrujButton;
    protected int[][] red = new int[Image.image.getWidth()][Image.image.getHeight()];
    protected int[][] green = new int[Image.image.getWidth()][Image.image.getHeight()];
    protected int[][] blue = new int[Image.image.getWidth()][Image.image.getHeight()];
    private int wymMaski;
    protected boolean maskaNiePusta;

    public FiltrPanel(JFrame parent, String title, int ilePol, int wymMaski) {
        super(parent, title, true);
        this.ilePol = ilePol;
        this.wymMaski = wymMaski;

        x1 = 3 * margines;
        x2 = wysokoscMaskaPanel / 2;
        y1 = margines;
        y2 = wysokoscLabel;
        add(suwakLabels);
        suwakLabels.setBounds(x1, y1, x2, y2);
        suwakLabels.setHorizontalAlignment(JLabel.LEFT);

        x1 += x2;
        add(suwakValuesLabels);
        suwakValuesLabels.setBounds(x1, y1, x2, y2);
        suwakValuesLabels.setHorizontalAlignment(JLabel.RIGHT);

        x1 = 3 * margines;
        y1 += margines + wysokoscLabel;
        x2 = wysokoscMaskaPanel;

        suwak.setMinimum(1);
        suwak.setMaximum(20);
        suwak.setValue(1);
        suwakValuesLabels.setText("" + suwak.getValue());
        add(suwak);

        suwak.setBounds(x1, y1, x2, y2);
        suwak.setPaintTicks(true);
        suwak.setMajorTickSpacing(1);
        suwak.addChangeListener(this);

        wysokoscSuwakPanel = x1 + 2 * margines;

        if (ilePol > 0) {
            polaPanel = new JPanel(new GridLayout(ilePol, 2, margines, margines));
            polaLabels = new JLabel[ilePol];
            polaFileds = new JFormattedTextField[ilePol];
            if (ilePol > 1) {
                wysokoscPolaPanel = (margines + wysokoscLabel) * ilePol;
            } else {
                wysokoscPolaPanel = wysokoscLabel;
            }
            wysokoscDialog += wysokoscPolaPanel + margines + wysokoscSuwakPanel;
            setSize(szerokoscDialog, wysokoscDialog);
            setLayout(null);

            polaPanel.setSize(wysokoscMaskaPanel, wysokoscPolaPanel);
            polaPanel.setBounds(3 * margines, margines + wysokoscSuwakPanel, wysokoscMaskaPanel, wysokoscPolaPanel);
            add(polaPanel);

            for (int i = 0; i < ilePol; i++) {
                polaLabels[i] = new JLabel("Label " + i);
                polaPanel.add(polaLabels[i]);
                polaFileds[i] = new JFormattedTextField();
                polaFileds[i].setHorizontalAlignment(JLabel.RIGHT);
                polaPanel.add(polaFileds[i]);
            }
        }
        y1filtrujButton = wysokoscDialog - 7 * margines - wysokoscLabel;
        x2filtrujButton = wysokoscLabel * 4;
        x1filtrujButton = (szerokoscDialog - x2filtrujButton - margines) / 2;
        y2filtrujButton = wysokoscLabel;

        add(filtrujButton);
        filtrujButton.setBounds(x1filtrujButton, y1filtrujButton, x2filtrujButton, y2filtrujButton);
        filtrujButton.addActionListener(this);

        savePanel = new SavePanel(this);
        add(savePanel);
        savePanel.setBounds(x1filtrujButton - 80, y1filtrujButton + (margines + wysokoscLabel), x2filtrujButton + 160, wysokoscLabel);

        addWindowListener(
                new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                savePanel.anuluj();
            }
        });

        maska[0][0] = new JFormattedTextField();
        wczytajDane();
        rysujMaske();
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        Object evt = ce.getSource();
        if (evt == suwak) {
            int suwakVal = suwak.getValue();
            if (nrMaski != suwakVal) {
                nrMaski = suwakVal;
                rozmMaski = getRozmMaski(nrMaski);
                suwakValuesLabels.setText("" + suwakVal);
                rysujMaske();
                this.repaint();
            }
        }
    }

    protected final void rysujMaske() {
        int powiekszDialog, wysokoscScrolla, wysokoscMaskiTmp = 0;
        int srodekMaski = nrMaski;//bo liczymy od zera
        int powiekszOd = 2;
        int dodajScrollOd = 5;
        int wysokoscKomorki = wysokoscMaskaPanel / getRozmMaski(powiekszOd);
        remove(maskaPanel);
        remove(maskaScroll);
        remove(maskaBox);
        maskaBox = new JPanel(new GridLayout(1, 1));
        maskaPanel = new JPanel(new GridLayout(rozmMaski, rozmMaski));

        if (nrMaski <= powiekszOd) {
            wysokoscScrolla = wysokoscMaskaPanel;
            powiekszDialog = 0;
        } else if (nrMaski > powiekszOd && nrMaski <= dodajScrollOd) {
            wysokoscScrolla = wysokoscKomorki * rozmMaski;
            powiekszDialog = (rozmMaski - getRozmMaski(powiekszOd)) * wysokoscKomorki;
        } else {
            wysokoscMaskiTmp = wysokoscKomorki * rozmMaski;
            wysokoscScrolla = wysokoscKomorki * getRozmMaski(dodajScrollOd);
            powiekszDialog = wysokoscKomorki * (getRozmMaski(dodajScrollOd) - getRozmMaski(powiekszOd));
        }

        setSize(szerokoscDialog + powiekszDialog, wysokoscDialog + powiekszDialog);
        filtrujButton.setBounds(x1filtrujButton + (powiekszDialog / 2), y1filtrujButton + powiekszDialog, x2filtrujButton, y2filtrujButton);
        savePanel.setBounds(x1filtrujButton - 80 + (powiekszDialog / 2), y1filtrujButton + (margines + wysokoscLabel) + powiekszDialog, x2filtrujButton + 160, wysokoscLabel);

        x1 = 3 * margines;
        y1 = 2 * margines + wysokoscPolaPanel + wysokoscSuwakPanel;

        maskaBox.setSize(wysokoscScrolla, wysokoscScrolla);
        maskaBox.setBounds(x1, y1, wysokoscScrolla, wysokoscScrolla);
        add(maskaBox);
        maska = new JFormattedTextField[rozmMaski][rozmMaski];

        for (int i = 0; i < rozmMaski; i++) {
            maska[i] = new JFormattedTextField[rozmMaski];
            for (int y = 0; y < rozmMaski; y++) {
                maska[i][y] = new JFormattedTextField();
                maska[i][y].addKeyListener((KeyListener) this);

                if (i == y && i == srodekMaski) {
                    maska[i][y].setBackground(Color.yellow);
                } else {
                    maska[i][y].setBackground(Color.white);
                }

                maska[i][y].setHorizontalAlignment(JLabel.RIGHT);
                maskaPanel.add(maska[i][y]);
                setMaskaValue(maska[i][y], i, y);
            }
        }

        if (wysokoscMaskiTmp != 0) {
            maskaScroll = new JScrollPane(maskaPanel);
            maskaScroll.setPreferredSize(new Dimension(wysokoscScrolla, wysokoscScrolla));
            maskaPanel.setPreferredSize(new Dimension(wysokoscMaskiTmp, wysokoscMaskiTmp));
            maskaBox.add(maskaScroll);
        } else {
            maskaPanel.setPreferredSize(new Dimension(wysokoscMaskiTmp, wysokoscMaskiTmp));
            maskaBox.add(maskaPanel);
        }

        maskaPanel.revalidate();
        maskaScroll.revalidate();
        maskaBox.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object evt = ae.getSource();
        if (evt == filtrujButton) {
            Form.restoreImage();
            wartosciMaski = new double[rozmMaski][rozmMaski];
            if (wymMaski == 2) {
                setSumaMaski2D();
            } else if (wymMaski == 1) {
                setSumaMaski1D();
            } else {
                sprawdzCzyMaskaNiepusta();
            }
            filtrujButton();
            Form.refresh();
        }
    }

    protected void filtrujButton() {
    }

    private void wczytajDane() {
        int rgb;
        for (int x = 0; x < Image.image.getWidth(); x++) {
            for (int y = 0; y < Image.image.getHeight(); y++) {
                rgb = Image.image.getRGB(x, y);
                Color color = new Color(rgb, true);
                red[x][y] = color.getRed();
                green[x][y] = color.getGreen();
                blue[x][y] = color.getBlue();
            }
        }
    }

    public int odbicieLustrzane(int i, char typ) {
        if (i < 0) {
            i = Math.abs(i);
        } else {
            int x;
            if (typ == 'x') {
                x = Image.image.getWidth();
            } else {
                x = Image.image.getHeight();
            }
            if (i >= x) {
                i = 2 * x - i - 1;
            }
        }
        return i;
    }

    public void setMaskaValue(JFormattedTextField jFormattedTextField, int i, int y) {
        jFormattedTextField.setText("0");
    }

    public int getRozmMaski(int nrMaski) {
        return nrMaski * 2 + 1;
    }

    public int getNrMaski(int rozmMaski) {
        return (rozmMaski - 1) / 2;
    }

    public void setSumaMaski1D() {
        sumaMaska = 0;
        for (int i = 0; i < rozmMaski; i++) {
            wartosciMaski[i] = new double[rozmMaski];
            wartosciMaski[i][nrMaski] = Double.parseDouble(maska[nrMaski][i].getText());
            sumaMaska += wartosciMaski[i][nrMaski];
        }
        if (sumaMaska == 0) {
            sumaMaska = 1;
        }
    }

    public void setSumaMaski2D() {
        sumaMaska = 0;
        for (int i = 0; i < rozmMaski; i++) {
            wartosciMaski[i] = new double[rozmMaski];
            for (int j = 0; j < rozmMaski; j++) {
                wartosciMaski[i][j] = Double.parseDouble(maska[j][i].getText().replace(",", "."));
                sumaMaska += wartosciMaski[i][j];
            }
        }
        if (sumaMaska == 0) {
            sumaMaska = 1;
        }
    }

    private void sprawdzCzyMaskaNiepusta() {
        maskaNiePusta = false;
        double tmp;
        String t;
        for (int i = 0; i < rozmMaski; i++) {
            wartosciMaski[i] = new double[rozmMaski];
            for (int j = 0; j < rozmMaski; j++) {
                t = maska[j][i].getText();
                if (!t.equals("")) {
                    tmp = wartosciMaski[i][j] = Double.parseDouble(t.replace(",", "."));
                } else {
                    tmp = wartosciMaski[i][j] = 0;
                }
                if (!maskaNiePusta && tmp != 0) {
                    maskaNiePusta = true;
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        char c = ke.getKeyChar();
        if ((c < '0' || c > '9') && c != '-' && c != ',' && c != '.') {
            ke.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int klaw = ke.getKeyCode();
        if (klaw == KeyEvent.VK_ENTER) {
            ke.consume();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}