package panchenko.vladyslav;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Vladyslav
 */
public class ObrazFourier extends JPanel {

    public static BufferedImage image;

    public ObrazFourier() {
        super();
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    public static void wypelnij(int wartosc) {
        int rgb;
        for (int x = 0; x < ObrazFourier.image.getWidth(); x++) {
            for (int y = 0; y < ObrazFourier.image.getHeight(); y++) {
                rgb = jrgb(wartosc, wartosc, wartosc);
                ObrazFourier.image.setRGB(x, y, rgb);
            }
        }
    }
    
    public static int jrgb(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }
}