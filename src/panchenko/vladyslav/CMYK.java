package panchenko.vladyslav;

import java.awt.Color;
import javax.swing.JFrame;

/**
 *
 * @author Vladyslav
 */
public class CMYK extends SlidersPanel {

    private int widthPicture = Image.image.getWidth();
    private int heightPicture = Image.image.getHeight();
    private double[][] cyjan = new double[widthPicture][heightPicture];
    private double[][] magenta = new double[widthPicture][heightPicture];
    private double[][] yellow = new double[widthPicture][heightPicture];
    private double[][] black = new double[widthPicture][heightPicture];

    public CMYK(JFrame parent) {
        super(parent, "Conversion CMYK", 4);
        sliderLabels[0].setText("C");
        sliderLabels[1].setText("M");
        sliderLabels[2].setText("Y");
        sliderLabels[3].setText("K");

        for (int i = 0; i < countSlider; i++) {
            slider[i].setMinimum(-100);
            slider[i].setMaximum(100);
            slider[i].setValue(0);
        }
    }

    @Override
    public void sliderAction() {
        convertToCMYK();
        addToCMYK();
    }

    @Override
    public void setSliderValuesLabels() {
        double text;
        for (int i = 0; i < countSlider; i++) {
            text = (slider[i].getValue()) / 100.0;
            sliderValuesLabels[i].setText("" + text);
        }
    }

    private void convertToCMYK() {
        double rU, gU, bU, k;
        for (int x = 0; x < Image.image.getWidth(); x++) {
            for (int y = 0; y < Image.image.getHeight(); y++) {
                int rgb = Image.image.getRGB(x, y);
                Color color = new Color(rgb, true);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                rU = 1 - r / 255.0;
                gU = 1 - g / 255.0;
                bU = 1 - b / 255.0;

                k = black[x][y] = Math.min(Math.min(rU, gU), bU);
                cyjan[x][y] = (rU - k) / (1 - k);
                magenta[x][y] = (gU - k) / (1 - k);
                yellow[x][y] = (bU - k) / (1 - k);
            }
        }
    }

    private void addToCMYK() {
        double addCyjan = slider[0].getValue() / 100.0;
        double addMagenta = slider[1].getValue() / 100.0;
        double addYellow = slider[2].getValue() / 100.0;
        double addBlack = slider[3].getValue() / 100.0;
        double c, m, ye, k;
        int rgb;
        if (addCyjan != 0 || addMagenta != 0 || addYellow != 0 || addBlack != 0) {
            for (int x = 0; x < Image.image.getWidth(); x++) {
                for (int y = 0; y < Image.image.getHeight(); y++) {
                    c = cyjan[x][y] + addCyjan;
                    m = magenta[x][y] + addMagenta;
                    ye = yellow[x][y] + addYellow;
                    k = black[x][y] + addBlack;

                    rgb = convertToRGB(c, m, ye, k);
                    Image.image.setRGB(x, y, rgb);
                }
            }
        }
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
    
    public int convertToRGB(double c, double m, double y, double k) {
        double r = (1 - (c * (1 - k) + k)) * 255.0;
        double g = (1 - (m * (1 - k) + k)) * 255.0;
        double b = (1 - (y * (1 - k) + k)) * 255.0;

        int r1 = obetnij256((int)Math.round(r));
        int g1 = obetnij256((int)Math.round(g));
        int b1 = obetnij256((int)Math.round(b));
        return jrgb(r1, g1, b1);
    }
}