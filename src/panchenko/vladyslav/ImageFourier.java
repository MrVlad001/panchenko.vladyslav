package panchenko.vladyslav;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Vladyslav
 */
public class ImageFourier extends JPanel {

    public static BufferedImage image;

    public ImageFourier() {
        super();
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
    // wypelnic
    public static void writeData(int value) {
        int rgb;
        for (int x = 0; x < ImageFourier.image.getWidth(); x++) {
            for (int y = 0; y < ImageFourier.image.getHeight(); y++) {
                rgb = jrgb(value, value, value);
                ImageFourier.image.setRGB(x, y, rgb);
            }
        }
    }
    
    public static int jrgb(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }
}