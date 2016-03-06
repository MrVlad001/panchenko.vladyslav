package panchenko.vladyslav;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Vladyslav
 */
public class FourierDialog extends JDialog {

    private JPanel panel;
    private Fourier tf = new Fourier();

    public FourierDialog(JFrame parent, int type) {
        super(parent, "Fourier", false);
        setNaglowek(type);
        setSize(Image.image.getWidth() + 16, Image.image.getHeight() + 38);
        panel = new ImageFourier();
        ImageFourier.image = Form.duplicateImage(Image.image);
        add(panel);
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