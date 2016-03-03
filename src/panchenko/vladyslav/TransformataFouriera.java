package panchenko.vladyslav;

import java.awt.Color;

/**
 *
 * @author Vladyslav
 */
public class TransformataFouriera {

    private int W = Obraz.image.getWidth();
    private int H = Obraz.image.getHeight();
    private Complex[] rIn = new Complex[W * H];
    private Complex[] gIn = new Complex[W * H];
    private Complex[] bIn = new Complex[W * H];
    private Complex[] rOut;
    private Complex[] gOut;
    private Complex[] bOut;

    public void tf(int type) {
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                int rgb = Obraz.image.getRGB(x, y);
                Color color = new Color(rgb, true);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                rIn[H * x + y] = new Complex(r, 0);
                gIn[H * x + y] = new Complex(g, 0);
                bIn[H * x + y] = new Complex(b, 0);
            }
        }
        rOut = FFT.fft(rIn);
        gOut = FFT.fft(gIn);
        bOut = FFT.fft(bIn);

        rOut = zmienCwiartki(rOut);
        gOut = zmienCwiartki(gOut);
        bOut = zmienCwiartki(bOut);
        if (type != 3) {
            for (int i = 0; i < rOut.length; i++) {
                rOut[i] = new Complex(Math.log(Math.abs(rOut[i].re()) + 1), Math.log(Math.abs(rOut[i].im()) + 1));
                gOut[i] = new Complex(Math.log(Math.abs(gOut[i].re()) + 1), Math.log(Math.abs(gOut[i].im()) + 1));
                bOut[i] = new Complex(Math.log(Math.abs(bOut[i].re()) + 1), Math.log(Math.abs(bOut[i].im()) + 1));
            }

            rOut = Fje.przeskalujComplex256(rOut);
            gOut = Fje.przeskalujComplex256(gOut);
            bOut = Fje.przeskalujComplex256(bOut);
        }
        if (type == 0) {
            //realis
            for (int i = 0; i < W * H; i++) {
                int y = i % H;
                int x = i / H;
                int rgb = Fje.jrgb(Fje.obetnij256((int) rOut[i].re()), Fje.obetnij256((int) gOut[i].re()), Fje.obetnij256((int) bOut[i].re()));
                ObrazFourier.image.setRGB(x, y, rgb);
            }
        } else if (type == 1) {
            //imaginalis
            for (int i = 0; i < W * H; i++) {
                int y = i % H;
                int x = i / H;
                int rgb = Fje.jrgb(Fje.obetnij256((int) rOut[i].im()), Fje.obetnij256((int) gOut[i].im()), Fje.obetnij256((int) bOut[i].im()));
                ObrazFourier.image.setRGB(x, y, rgb);
            }
        } else if (type == 2) {
            //spektrum
            for (int i = 0; i < W * H; i++) {
                int y = i % H;
                int x = i / H;
                int rgb = Fje.jrgb(Fje.obetnij256((int) rOut[i].abs()), Fje.obetnij256((int) gOut[i].abs()), Fje.obetnij256((int) bOut[i].abs()));
                ObrazFourier.image.setRGB(x, y, rgb);
            }
        } else if (type == 3) {
            //faza
            double[] red = new double[rOut.length];
            double[] green = new double[gOut.length];
            double[] blue = new double[bOut.length];

            for (int i = 0; i < rOut.length; i++) {
                red[i] = Math.log(Math.abs(rOut[i].arctanSpecial()) + 1);
                green[i] = Math.log(Math.abs(gOut[i].arctanSpecial()) + 1);
                blue[i] = Math.log(Math.abs(bOut[i].arctanSpecial()) + 1);
            }

            red = Fje.przeskaluj256(red);
            green = Fje.przeskaluj256(green);
            blue = Fje.przeskaluj256(blue);

            for (int i = 0; i < W * H; i++) {
                int y = i % H;
                int x = i / H;
                int rgb = Fje.jrgb(Fje.obetnij256((int) red[i]), Fje.obetnij256((int) green[i]), Fje.obetnij256((int) blue[i]));
                ObrazFourier.image.setRGB(x, y, rgb);
            }
        }
    }

    public Complex[] zmienCwiartki(Complex[] tab) {
        int n = tab.length / 4;
        Complex[] tab1 = new Complex[n];
        Complex[] tab2 = new Complex[n];
        Complex[] tab3 = new Complex[n];
        Complex[] tab4 = new Complex[n];
        Complex[] tabr = new Complex[tab.length];
        int a, b, c, d;
        a = b = c = d = 0;

        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                if (x < H / 2) {
                    if (y < W / 2) {
                        //pierwsza ćwiartka
                        tab1[a] = tab[H * x + y];
                        a++;
                    } else {
                        //czwarta ćwiartka
                        tab4[b] = tab[H * x + y];
                        b++;
                    }
                } else {
                    if (y < W / 2) {
                        //druga ćwiartka
                        tab2[d] = tab[H * x + y];
                        d++;
                    } else {
                        //trzecia ćwiartka
                        tab3[c] = tab[H * x + y];
                        c++;
                    }
                }
            }
        }

        a = b = c = d = 0;
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                if (x < H / 2) {
                    if (y < W / 2) {
                        //pierwsza ćwiartka
                        tabr[H * x + y] = tab3[a];
                        a++;
                    } else {
                        //czwarta ćwiartka
                        tabr[H * x + y] = tab2[b];
                        b++;
                    }
                } else {
                    if (y < W / 2) {
                        //druga ćwiartka
                        tabr[H * x + y] = tab4[d];
                        d++;
                    } else {
                        //trzecia ćwiartka
                        tabr[H * x + y] = tab1[c];
                        c++;
                    }
                }
            }
        }
        return tabr;
    }
}