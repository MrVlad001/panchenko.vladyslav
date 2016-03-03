package panchenko.vladyslav;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Vladyslav
 */
public abstract class SlidersPanel extends JDialog implements ChangeListener {

    private int margines = 30;
    private int heightSlider = 20;
    private int heightDialog;
    private int widthDialog = 500;
    protected int countSlider;
    protected JSlider[] slider;
    protected JLabel[] sliderLabels;
    protected JLabel[] sliderValuesLabels;
    private SavePanel savePanel;
    private int heightSavePanel = margines + heightSlider;
    private boolean constructorFlag = false;

    public SlidersPanel(JFrame parent, String tytulOkna, int countSlider) {
        super(parent, tytulOkna, true);
        this.countSlider = countSlider;
        heightDialog = margines * (countSlider + 1) + heightSlider * (countSlider + 1) + heightSavePanel;
        setSize(widthDialog, heightDialog);
        setLayout(null);
        slider = new JSlider[countSlider];
        sliderLabels = new JLabel[countSlider];
        sliderValuesLabels = new JLabel[countSlider];

        int y = 0;
        for (int i = 0; i < countSlider; i++) {
            y = heightSlider * i + margines * (i + 1);
            slider[i] = new JSlider();
            add(slider[i]);
            slider[i].setBounds(100, y, 300, heightSlider);
            slider[i].setPaintTicks(true);
            slider[i].addChangeListener(this);

            sliderLabels[i] = new JLabel("Suwak" + i);
            add(sliderLabels[i]);
            sliderLabels[i].setBounds(10, y - 7, 80, heightSlider);
            sliderLabels[i].setHorizontalAlignment(JLabel.RIGHT);

            sliderValuesLabels[i] = new JLabel("" + 0);
            add(sliderValuesLabels[i]);
            sliderValuesLabels[i].setBounds(widthDialog - 90, y - 7, 50, heightSlider);
            sliderValuesLabels[i].setHorizontalAlignment(JLabel.RIGHT);
        }
        y += margines + heightSlider;
        savePanel = new SavePanel(this);
        add(savePanel);
        savePanel.setBounds(100, y, 300, heightSlider);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                savePanel.anuluj();
            }
        });
        constructorFlag = true;
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        if (constructorFlag) {
            Form.przywrocObraz();
            setSliderValuesLabels();
            sliderAction();
            Form.refresh();
        }
    }

    public abstract void sliderAction();

    public void setSliderValuesLabels() {
        for (int i = 0; i < countSlider; i++) {
            sliderValuesLabels[i].setText("" + slider[i].getValue());
        }
    }
}