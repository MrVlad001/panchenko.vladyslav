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

        rOut = changeSectors(rOut);
        gOut = changeSectors(gOut);
        bOut = changeSectors(bOut);
        if (type != 3) {
            for (int i = 0; i < rOut.length; i++) {
                rOut[i] = new Complex(Math.log(Math.abs(rOut[i].re()) + 1), Math.log(Math.abs(rOut[i].im()) + 1));
                gOut[i] = new Complex(Math.log(Math.abs(gOut[i].re()) + 1), Math.log(Math.abs(gOut[i].im()) + 1));
                bOut[i] = new Complex(Math.log(Math.abs(bOut[i].re()) + 1), Math.log(Math.abs(bOut[i].im()) + 1));
            }

            rOut = scanComplex256(rOut);
            gOut = scanComplex256(gOut);
            bOut = scanComplex256(bOut);
        }
        if (type == 0) {
            //realis
            for (int i = 0; i < W * H; i++) {
                int y = i % H;
                int x = i / H;
                int rgb = jrgb(erase256((int) rOut[i].re()), erase256((int) gOut[i].re()), erase256((int) bOut[i].re()));
                ObrazFourier.image.setRGB(x, y, rgb);
            }
        } else if (type == 1) {
            //imaginalis
            for (int i = 0; i < W * H; i++) {
                int y = i % H;
                int x = i / H;
                int rgb = jrgb(erase256((int) rOut[i].im()), erase256((int) gOut[i].im()), erase256((int) bOut[i].im()));
                ObrazFourier.image.setRGB(x, y, rgb);
            }
        } else if (type == 2) {
            //spektrum
            for (int i = 0; i < W * H; i++) {
                int y = i % H;
                int x = i / H;
                int rgb = jrgb(erase256((int) rOut[i].abs()), erase256((int) gOut[i].abs()), erase256((int) bOut[i].abs()));
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

            red = scan256(red);
            green = scan256(green);
            blue = scan256(blue);

            for (int i = 0; i < W * H; i++) {
                int y = i % H;
                int x = i / H;
                int rgb = jrgb(erase256((int) red[i]), erase256((int) green[i]), erase256((int) blue[i]));
                ObrazFourier.image.setRGB(x, y, rgb);
            }
        }
    }
    
    public static int jrgb(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }
    
    public static int erase256(int color) {
        if (color > 255) {
            color = 255;
        } else if (color < 0) {
            color = 0;
        }
        return color;
    }
    
    public static double[] scan256(double[] color) {
        double max = 0, min = 0, tmp;
        for (int i = 0; i < color.length; i++) {
            tmp = color[i];
            max = Math.max(max, tmp);
            min = Math.min(min, tmp);
        }
        double dlugoscPrzedzialu = max - min;
        double k = 255 / dlugoscPrzedzialu;
        double c = k * min;

        for (int i = 0; i < color.length; i++) {
            color[i] = eraseCustom(k * color[i] - c, 0, 255);
        }

        return color;
    }
        
    public static Complex[] scanComplex256(Complex[] color) {
        double max = 0, min = 0, tmp, tmp1;
        for (int i = 0; i < color.length; i++) {
            tmp = color[i].re();
            tmp1 = color[i].im();
            max = Math.max(max, Math.max(tmp, tmp1));
            min = Math.min(min, Math.max(tmp, tmp1));
        }
        double dlugoscPrzedzialu = max - min;
        double k = 255 / dlugoscPrzedzialu;
        double c = k * min;

        for (int i = 0; i < color.length; i++) {
            color[i] = new Complex(eraseCustom(k * color[i].re() - c, 0, 255), eraseCustom(k * color[i].im() - c, 0, 255));
        }

        return color;
    }
    
      public static double eraseCustom(double color, double limitDown, double limitUp) {
        if (color > limitUp) {
            color = limitUp;
        } else if (color < limitDown) {
            color = limitDown;
        }
        return color;
    }
    
    public Complex[] changeSectors(Complex[] array) {
        int n = array.length / 4;
        Complex[] arr1 = new Complex[n];
        Complex[] arr2 = new Complex[n];
        Complex[] arr3 = new Complex[n];
        Complex[] arr4 = new Complex[n];
        Complex[] resultArr = new Complex[array.length];
        int a, b, c, d;
        a = b = c = d = 0;

        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                if (x < H / 2) {
                    if (y < W / 2) {
                        arr1[a] = array[H * x + y];
                        a++;
                    } else {
                        arr4[b] = array[H * x + y];
                        b++;
                    }
                } else {
                    if (y < W / 2) {
                        arr2[d] = array[H * x + y];
                        d++;
                    } else {
                        arr3[c] = array[H * x + y];
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
                        resultArr[H * x + y] = arr3[a];
                        a++;
                    } else {
                        resultArr[H * x + y] = arr2[b];
                        b++;
                    }
                } else {
                    if (y < W / 2) {
                        resultArr[H * x + y] = arr4[d];
                        d++;
                    } else {
                        resultArr[H * x + y] = arr1[c];
                        c++;
                    }
                }
            }
        }
        return resultArr;
    }
}