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
public class FilterPanel extends JDialog implements ChangeListener, ActionListener, KeyListener {

    private final int margines = 15;
    private final int heightLabel = 20;
    private int heightDialog = 360;
    private final int widthDialog = 300;
    private JSlider slider = new JSlider();
    private JLabel sliderLabels = new JLabel("Rozmiar maski");
    private JLabel sliderValuesLabels = new JLabel();
    private int heightSliderPanel;
    private int heightFieldPanel = 0;
    protected int countField = 1;
    protected JPanel fieldPanel;
    protected JLabel[] fieldLabels;
    protected JFormattedTextField[] fields;
    protected int x1, y1, x2, y2;
    private JPanel maskBox = new JPanel();
    private JScrollPane maskScroll = new JScrollPane();
    private JPanel maskPanel = new JPanel();
    private int heightMaskPanel = (widthDialog - 7 * margines);
    protected int numMask = 1;
    protected int sizeMask = 3;
    protected double[][] valueMask;
    protected double[] valueMask2;
    protected double sumMask;
    protected JFormattedTextField[][] mask = new JFormattedTextField[1][1];
    private SavePanel savePanel;
    private JButton filterButton = new JButton("Filtruj");
    private int x1filterButton, y1filterButton, x2filterButton, y2filterButton;
    protected int[][] red = new int[Image.image.getWidth()][Image.image.getHeight()];
    protected int[][] green = new int[Image.image.getWidth()][Image.image.getHeight()];
    protected int[][] blue = new int[Image.image.getWidth()][Image.image.getHeight()];
    private int wymiarMask;
    protected boolean notFullMask;

    public FilterPanel(JFrame parent, String title, int countField, int wymiarMask) {
        super(parent, title, true);
        this.countField = countField;
        this.wymiarMask = wymiarMask;

        x1 = 3 * margines;
        x2 = heightMaskPanel / 2;
        y1 = margines;
        y2 = heightLabel;
        add(sliderLabels);
        sliderLabels.setBounds(x1, y1, x2, y2);
        sliderLabels.setHorizontalAlignment(JLabel.LEFT);

        x1 += x2;
        add(sliderValuesLabels);
        sliderValuesLabels.setBounds(x1, y1, x2, y2);
        sliderValuesLabels.setHorizontalAlignment(JLabel.RIGHT);

        x1 = 3 * margines;
        y1 += margines + heightLabel;
        x2 = heightMaskPanel;

        slider.setMinimum(1);
        slider.setMaximum(20);
        slider.setValue(1);
        sliderValuesLabels.setText("" + slider.getValue());
        add(slider);

        slider.setBounds(x1, y1, x2, y2);
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(1);
        slider.addChangeListener(this);

        heightSliderPanel = x1 + 2 * margines;

        if (countField > 0) {
            fieldPanel = new JPanel(new GridLayout(countField, 2, margines, margines));
            fieldLabels = new JLabel[countField];
            fields = new JFormattedTextField[countField];
            if (countField > 1) {
                heightFieldPanel = (margines + heightLabel) * countField;
            } else {
                heightFieldPanel = heightLabel;
            }
            heightDialog += heightFieldPanel + margines + heightSliderPanel;
            setSize(widthDialog, heightDialog);
            setLayout(null);

            fieldPanel.setSize(heightMaskPanel, heightFieldPanel);
            fieldPanel.setBounds(3 * margines, margines + heightSliderPanel, heightMaskPanel, heightFieldPanel);
            add(fieldPanel);

            for (int i = 0; i < countField; i++) {
                fieldLabels[i] = new JLabel("Label " + i);
                fieldPanel.add(fieldLabels[i]);
                fields[i] = new JFormattedTextField();
                fields[i].setHorizontalAlignment(JLabel.RIGHT);
                fieldPanel.add(fields[i]);
            }
        }
        y1filterButton = heightDialog - 7 * margines - heightLabel;
        x2filterButton = heightLabel * 4;
        x1filterButton = (widthDialog - x2filterButton - margines) / 2;
        y2filterButton = heightLabel;

        add(filterButton);
        filterButton.setBounds(x1filterButton, y1filterButton, x2filterButton, y2filterButton);
        filterButton.addActionListener(this);

        savePanel = new SavePanel(this);
        add(savePanel);
        savePanel.setBounds(x1filterButton - 80, y1filterButton + (margines + heightLabel), x2filterButton + 160, heightLabel);

        addWindowListener(
                new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                savePanel.anuluj();
            }
        });

        mask[0][0] = new JFormattedTextField();
        readData();
        printMask();
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        Object evt = ce.getSource();
        if (evt == slider) {
            int sliderValue = slider.getValue();
            if (numMask != sliderValue) {
                numMask = sliderValue;
                sizeMask = getSizeMask(numMask);
                sliderValuesLabels.setText("" + sliderValue);
                printMask();
                this.repaint();
            }
        }
    }

    
    protected final void printMask() {
        int increaseDialog, heightScroll, heightMaskTmp = 0;
        int maskMiddle = numMask;// bo liczymy od zera
        int increaseOd = 2; // powiekszyc od
        int addScrollOd = 5;
        int heightCell = heightMaskPanel / getSizeMask(increaseOd); // wysokosc komorki
        remove(maskPanel);
        remove(maskScroll);
        remove(maskBox);
        maskBox = new JPanel(new GridLayout(1, 1));
        maskPanel = new JPanel(new GridLayout(sizeMask, sizeMask));

        if (numMask <= increaseOd) {
            heightScroll = heightMaskPanel;
            increaseDialog = 0;
        } else if (numMask > increaseOd && numMask <= addScrollOd) {
            heightScroll = heightCell * sizeMask;
            increaseDialog = (sizeMask - getSizeMask(increaseOd)) * heightCell;
        } else {
            heightMaskTmp = heightCell * sizeMask;
            heightScroll = heightCell * getSizeMask(addScrollOd);
            increaseDialog = heightCell * (getSizeMask(addScrollOd) - getSizeMask(increaseOd));
        }

        setSize(widthDialog + increaseDialog, heightDialog + increaseDialog);
        filterButton.setBounds(x1filterButton + (increaseDialog / 2), y1filterButton + increaseDialog, x2filterButton, y2filterButton);
        savePanel.setBounds(x1filterButton - 80 + (increaseDialog / 2), y1filterButton + (margines + heightLabel) + increaseDialog, x2filterButton + 160, heightLabel);

        x1 = 3 * margines;
        y1 = 2 * margines + heightFieldPanel + heightSliderPanel;

        maskBox.setSize(heightScroll, heightScroll);
        maskBox.setBounds(x1, y1, heightScroll, heightScroll);
        add(maskBox);
        mask = new JFormattedTextField[sizeMask][sizeMask];

        for (int i = 0; i < sizeMask; i++) {
            mask[i] = new JFormattedTextField[sizeMask];
            for (int y = 0; y < sizeMask; y++) {
                mask[i][y] = new JFormattedTextField();
                mask[i][y].addKeyListener((KeyListener) this);

                if (i == y && i == maskMiddle) {
                    mask[i][y].setBackground(Color.GREEN);
                } else {
                    mask[i][y].setBackground(Color.white);
                }

                mask[i][y].setHorizontalAlignment(JLabel.RIGHT);
                maskPanel.add(mask[i][y]);
                setMaskValue(mask[i][y], i, y);
            }
        }

        if (heightMaskTmp != 0) {
            maskScroll = new JScrollPane(maskPanel);
            maskScroll.setPreferredSize(new Dimension(heightScroll, heightScroll));
            maskPanel.setPreferredSize(new Dimension(heightMaskTmp, heightMaskTmp));
            maskBox.add(maskScroll);
        } else {
            maskPanel.setPreferredSize(new Dimension(heightMaskTmp, heightMaskTmp));
            maskBox.add(maskPanel);
        }

        maskPanel.revalidate();
        maskScroll.revalidate();
        maskBox.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object evt = ae.getSource();
        if (evt == filterButton) {
            Form.restoreImage();
            valueMask = new double[sizeMask][sizeMask];
            if (wymiarMask == 2) {
                setSumMask2D();
            } else if (wymiarMask == 1) {
                setSumMask1D();
            } else {
                checkIsMaskEmpty();
            }
            filterButton();
            Form.refresh();
        }
    }

    protected void filterButton() {
    }

    private void readData() {
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
    // odbicie lustralne
    public int mirrorReflection(int i, char typ) {
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

    public void setMaskValue(JFormattedTextField jFormattedTextField, int i, int y) {
        jFormattedTextField.setText("0");
    }

    public int getSizeMask(int numMask) {
        return numMask * 2 + 1;
    }

    public int getNumMask(int sizeMask) {
        return (sizeMask - 1) / 2;
    }

    public void setSumMask1D() {
        sumMask = 0;
        for (int i = 0; i < sizeMask; i++) {
            valueMask[i] = new double[sizeMask];
            valueMask[i][numMask] = Double.parseDouble(mask[numMask][i].getText());
            sumMask += valueMask[i][numMask];
        }
        if (sumMask == 0) {
            sumMask = 1;
        }
    }

    public void setSumMask2D() {
        sumMask = 0;
        for (int i = 0; i < sizeMask; i++) {
            valueMask[i] = new double[sizeMask];
            for (int j = 0; j < sizeMask; j++) {
                valueMask[i][j] = Double.parseDouble(mask[j][i].getText().replace(",", "."));
                sumMask += valueMask[i][j];
            }
        }
        if (sumMask == 0) {
            sumMask = 1;
        }
    }

    private void checkIsMaskEmpty() {
        notFullMask = false;
        double tmp;
        String t;
        for (int i = 0; i < sizeMask; i++) {
            valueMask[i] = new double[sizeMask];
            for (int j = 0; j < sizeMask; j++) {
                t = mask[j][i].getText();
                if (!t.equals("")) {
                    tmp = valueMask[i][j] = Double.parseDouble(t.replace(",", "."));
                } else {
                    tmp = valueMask[i][j] = 0;
                }
                if (!notFullMask && tmp != 0) {
                    notFullMask = true;
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
