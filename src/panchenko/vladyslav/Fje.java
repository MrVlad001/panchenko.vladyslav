package panchenko.vladyslav;

public class Fje {

    public static int jrgb(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }

    public static int jred(int rgb) {
        return (byte) ((rgb >> 16) & 0xff);
    }

    public static int jgreen(int rgb) {
        return (byte) ((rgb >> 8) & 0xff);
    }

    public static int jblue(int rgb) {
        return (byte) (rgb & 0xff);
    }

    public static int obetnij256(int color) {
        if (color > 255) {
            color = 255;
        } else if (color < 0) {
            color = 0;
        }
        return color;
    }

    public static double obetnij1(double color) {
        if (color > 1) {
            color = 1.0;
        } else if (color < 0) {
            color = 0.0;
        }
        return color;
    }

    public static int obetnijCustom(int color, int limitDown, int limitUp) {
        if (color > limitUp) {
            color = limitUp;
        } else if (color < limitDown) {
            color = limitDown;
        }
        return color;
    }

    public static double obetnijCustom(double color, double limitDown, double limitUp) {
        if (color > limitUp) {
            color = limitUp;
        } else if (color < limitDown) {
            color = limitDown;
        }
        return color;
    }

    public static byte obetnijDoByte(double color) {
        byte limitUp = 127;
        byte limitDown = -128;
        if (color > limitUp) {
            color = limitUp;
        } else if (color < limitDown) {
            color = limitDown;
        }
        return (byte) color;
    }

    public static float obetnijCustomFloat(float color, float limitDown, float limitUp) {
        if (color > limitUp) {
            color = limitUp;
        } else if (color < limitDown) {
            color = limitDown;
        }
        return color;
    }

    public static byte[] przeskalujDoByte(double[] tab) {
        double max = 0, min = 0, tmp;
        byte[] result = new byte[tab.length];
        for (int i = 0; i < tab.length; i++) {
            tmp = tab[i];

            max = Math.max(max, tmp);
            min = Math.min(min, tmp);
        }
        double dlugoscPrzedzialu = max - min;
        double k = 255 / dlugoscPrzedzialu;
        double c = k * min;

        for (int i = 0; i < tab.length; i++) {
            result[i] = obetnijDoByte(k * tab[i] - c);
        }

        return result;
    }

    public static Complex[] przeskalujComplex256(Complex[] color) {
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
            color[i] = new Complex(obetnijCustom(k * color[i].re() - c, 0, 255), obetnijCustom(k * color[i].im() - c, 0, 255));
        }

        return color;
    }

    public static double[] przeskaluj256(double[] color) {
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
            color[i] = obetnijCustom(k * color[i] - c, 0, 255);
        }

        return color;
    }

    public static double getNumber(String text) {
        double result = 0;
        if (!text.equals("")) {
            text = text.replace(",", ".");
            if (text.equals("-")) {
                result = 0;
            } else {
                result = Double.parseDouble(text);
            }
        }
        return result;
    }

    public static double limitNumber(double number, double limitDown, double limitUp, int decimalNumber) {
        if (number > limitUp) {
            number = limitUp;
        } else if (number < limitDown) {
            number = limitDown;
        } else {
            double tmp = Math.pow(10, decimalNumber);
            number = Math.round(number * tmp) / tmp;
        }
        return number;
    }

    public static double konwertuj1(int color) {
        return obetnij1(color / 255.0);
    }

    public static int konwertuj256(double color) {
        return obetnij256((int) (color * 255.0));
    }

    /**
     * Metoda służąca do pomnożenia dwóch tablic: tab1 i tab2, tablice te można
     * pomnożyć przez siebie pod warunkiem ze ilość kolumn tablicy tab1 jest
     * taka sama jak ilość wierszy tablicy tab2, w przeciwnym wypadku wyrzucany
     * jest wyjątek RuntimeException.
     *
     * @param tab1 pierwsza tablica
     * @param tab2 druga tablica
     * @return tablica będąca iloczynem tablic tab1 i tab2
     */
    public static double[][] wymnozMacierze(double[][] tab1, double[][] tab2) {
        double[][] macierzPomnozona = new double[tab1.length][tab2[0].length];
        if (tab1[0].length == tab2.length) {
            for (int i = 0; i < tab1.length; i++) {//ilosc wierszy tab1
                for (int j = 0; j < tab2[0].length; j++) { //ilosc kolumn tab2
                    double temp = 0;
                    for (int w = 0; w < tab2.length; w++) { //ilosc wierszy tab2
                        temp += tab1[i][w] * tab2[w][j];
                    }
                    macierzPomnozona[i][j] = temp;
                }
            }
        } else {
            throw new RuntimeException("Podane tablice mają niewłasciwe wymiary");
        }
        return macierzPomnozona;
    }

    /**
     * Metoda służąca do pomnożenia dwóch tablic: tab1 i tab2, tablice te można
     * pomnożyć przez siebie pod warunkiem ze ilość kolumn tablicy tab1 jest
     * taka sama jak ilość wierszy tablicy tab2, w przeciwnym wypadku wyrzucany
     * jest wyjątek RuntimeException.
     *
     * @param tab1 pierwsza tablica
     * @param tab2 druga tablica
     * @return tablica będąca iloczynem tablic tab1 i tab2
     */
    public static float[][] wymnozMacierze(float[][] tab1, float[][] tab2) {
        float[][] macierzPomnozona = new float[tab1.length][tab2[0].length];
        if (tab1[0].length == tab2.length) {
            for (int i = 0; i < tab1.length; i++) {//ilosc wierszy tab1
                for (int j = 0; j < tab2[0].length; j++) { //ilosc kolumn tab2
                    double temp = 0;
                    for (int w = 0; w < tab2.length; w++) { //ilosc wierszy tab2
                        temp += tab1[i][w] * tab2[w][j];
                    }
                    macierzPomnozona[i][j] = (float) temp;
                }
            }
        } else {
            throw new RuntimeException("Podane tablice mają niewłasciwe wymiary");
        }
        return macierzPomnozona;
    }

    public static void pokazTablice(byte[][] tab) {
        String result = "_________\n";
        for (int i = 0; i < tab.length; i++) {
            result += "[";
            for (int j = 0; j < tab[0].length; j++) {
                result += tab[i][j] + ",";
            }
            result += "]\n";
        }
        result += "_________";
        System.out.println(result);
    }

    public static void pokazTablice(int[][] tab) {
        String result = "_________\n";
        for (int i = 0; i < tab.length; i++) {
            result += "[";
            for (int j = 0; j < tab[0].length; j++) {
                result += tab[i][j] + ",";
            }
            result += "]\n";
        }
        result += "_________";
        System.out.println(result);
    }

    public static void pokazTablice(float[][] tab) {
        String result = "_________\n";
        for (int i = 0; i < tab.length; i++) {
            result += "[";
            for (int j = 0; j < tab[0].length; j++) {
                result += tab[i][j] + ",";
            }
            result += "]\n";
        }
        result += "_________";
        System.out.println(result);
    }

    public static void pokazTablice(double[][] tab) {
        String result = "_________\n";
        for (int i = 0; i < tab.length; i++) {
            result += "[";
            for (int j = 0; j < tab[0].length; j++) {
                result += tab[i][j] + ",";
            }
            result += "]\n";
        }
        result += "_________";
        System.out.println(result);
    }

    public static void pokazTablice(byte[] tab) {
        String result = "_________\n";
        result += "[";
        for (int i = 0; i < tab.length; i++) {
            result += tab[i] + ",";
        }
        result += "]\n";
        result += "_________";
        System.out.println(result);
    }

    public static void pokazTablice(double[] tab) {
        String result = "_________\n";
        result += "[";
        for (int i = 0; i < tab.length; i++) {
            result += tab[i] + ",";
        }
        result += "]\n";
        result += "_________";
        System.out.println(result);
    }

    public static void maxZtablicy(double[] tab) {
        double tmp = 0;
        for (int i = 0; i < tab.length; i++) {
            tmp = Math.max(tmp, tab[i]);
        }
        System.out.println(tmp);
    }

    public static void maxZtablicy(double[][] tab) {
        double tmp = 0;
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                tmp = Math.max(tmp, tab[i][j]);
            }
        }
        System.out.println(tmp);
    }

    public static void minZtablicy(double[] tab) {
        double tmp = 0;
        for (int i = 0; i < tab.length; i++) {
            tmp = Math.min(tmp, tab[i]);
        }
        System.out.println(tmp);
    }

    public static void minZtablicy(double[][] tab) {
        double tmp = 0;
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                tmp = Math.min(tmp, tab[i][j]);
            }
        }
        System.out.println(tmp);
    }

    public static double sredniaZtablicy(double[][] tab) {
        double tmp = 0;
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                tmp += tab[i][j];
            }
        }
        return tmp / (tab.length * tab[0].length);
    }

    public static float[][] konwertujTablice(byte[][] tab) {
        float[][] result = new float[tab.length][tab[0].length];
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                result[i][j] = tab[i][j];
            }
        }
        return result;
    }

    public static byte[][] konwertujTablice(float[][] tab) {
        byte[][] result = new byte[tab.length][tab[0].length];
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                result[i][j] = obetnijDoByte(tab[i][j]);
            }
        }
        return result;
    }
}