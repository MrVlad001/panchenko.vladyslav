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
public abstract class SuwakiPanel extends JDialog implements ChangeListener {

    private int margines = 30;
    private int wysokoscSuwaka = 20;
    private int wysokoscDialog;
    private int szerokoscDialog = 500;
    protected int ileSuwakow;
    protected JSlider[] suwaki;
    protected JLabel[] suwakiLabels;
    protected JLabel[] suwakiValuesLabels;
    private SavePanel savePanel;
    private int wysokoscSavePanel = margines + wysokoscSuwaka;
    private boolean wywolanoKonstruktor = false;

    public SuwakiPanel(JFrame parent, String tytulOkna, int ileSuwakow) {
        super(parent, tytulOkna, true);
        this.ileSuwakow = ileSuwakow;
        wysokoscDialog = margines * (ileSuwakow + 1) + wysokoscSuwaka * (ileSuwakow + 1) + wysokoscSavePanel;
        setSize(szerokoscDialog, wysokoscDialog);
        setLayout(null);
        suwaki = new JSlider[ileSuwakow];
        suwakiLabels = new JLabel[ileSuwakow];
        suwakiValuesLabels = new JLabel[ileSuwakow];

        int y = 0;
        for (int i = 0; i < ileSuwakow; i++) {
            y = wysokoscSuwaka * i + margines * (i + 1);
            suwaki[i] = new JSlider();
            add(suwaki[i]);
            suwaki[i].setBounds(100, y, 300, wysokoscSuwaka);
            suwaki[i].setPaintTicks(true);
            suwaki[i].addChangeListener(this);

            suwakiLabels[i] = new JLabel("Suwak" + i);
            add(suwakiLabels[i]);
            suwakiLabels[i].setBounds(10, y - 7, 80, wysokoscSuwaka);
            suwakiLabels[i].setHorizontalAlignment(JLabel.RIGHT);

            suwakiValuesLabels[i] = new JLabel("" + 0);
            add(suwakiValuesLabels[i]);
            suwakiValuesLabels[i].setBounds(szerokoscDialog - 90, y - 7, 50, wysokoscSuwaka);
            suwakiValuesLabels[i].setHorizontalAlignment(JLabel.RIGHT);
        }
        y += margines + wysokoscSuwaka;
        savePanel = new SavePanel(this);
        add(savePanel);
        savePanel.setBounds(100, y, 300, wysokoscSuwaka);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                savePanel.anuluj();
            }
        });
        wywolanoKonstruktor = true;
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        if (wywolanoKonstruktor) {
            Form.przywrocObraz();
            setSuwakiValuesLabels();
            suwakiAkcja();
            Form.refresh();
        }
    }

    public abstract void suwakiAkcja();

    public void setSuwakiValuesLabels() {
        for (int i = 0; i < ileSuwakow; i++) {
            suwakiValuesLabels[i].setText("" + suwaki[i].getValue());
        }
    }
}