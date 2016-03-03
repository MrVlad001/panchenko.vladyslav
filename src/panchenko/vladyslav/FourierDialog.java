package panchenko.vladyslav;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Vladyslav
 */
public class FourierDialog extends JDialog {

    private JPanel panelek;
    private TransformataFouriera tf = new TransformataFouriera();

    public FourierDialog(JFrame parent, int type) {
        super(parent, "Fourier", false);
        setNaglowek(type);
        setSize(Obraz.image.getWidth() + 16, Obraz.image.getHeight() + 38);
        panelek = new ObrazFourier();
        ObrazFourier.image = Form.duplikujObraz(Obraz.image);
        add(panelek);
        tf.tf(type);
    }

    private void setNaglowek(int type) {
        String title;
        if (type == 0) {
            title = "Fourier - realis";
        } else if (type == 1) {
            title = "Fourier - imaginalis";
        } else if (type == 2) {
            title = "Fourier - spektrum";
        } else {
            title = "Fourier - faza";
        }
        setTitle(title);
    }
}