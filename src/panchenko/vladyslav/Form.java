package panchenko.vladyslav;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Vladyslav
 */
public final class Form extends JFrame implements ActionListener, ChangeListener {

    public static JPanel panel;
    private JMenuBar menu = new JMenuBar();
    private JMenu plikMenu = new JMenu("File");
    private JMenuItem otworz = new JMenuItem("Open");
    private JMenuItem lena = new JMenuItem("Test");
    private JMenuItem wyjscie = new JMenuItem("Exit");
    private JMenu kolorMenu = new JMenu("Color");
    private JMenuItem jasKonGam = new JMenuItem("Brightness / Contrast / Gamma");
    private JMenuItem histogramy = new JMenuItem("Histogramy");
    private JMenu konwersjaMenu = new JMenu("Konwersja");
    private JMenuItem konwersjaCMYK = new JMenuItem("RGB ↔ CMYK");
    private JMenuItem konwersjaHSL = new JMenuItem("RGB ↔ HSL");
    private JMenuItem konwersjaLab = new JMenuItem("RGB ↔ L*a*b*");
    private JMenuItem konwersjaLuv = new JMenuItem("RGB ↔ L*u*v*");
    private JMenu filtryMenu = new JMenu("Filtry");
    private JMenuItem splot = new JMenuItem("Splot");
    private JMenuItem gauss = new JMenuItem("Splot Gaussa");
    private JMenuItem unsharpmask = new JMenuItem("Splot Unsharp Mask");
    private JMenuItem minimum = new JMenuItem("Minimum");
    private JMenuItem maksimum = new JMenuItem("Maksimum");
    private JMenuItem mediana = new JMenuItem("Mediana");
    private JMenu fourierMenu = new JMenu("Fourier");
    private JMenuItem fourierRealis = new JMenuItem("Realis");
    private JMenuItem fourierImaginalis = new JMenuItem("Imaginalis");
    private JMenuItem fourierSpektrum = new JMenuItem("Spektrum");
    private JMenuItem fourierFaza = new JMenuItem("Faza");
    
    public static BufferedImage imageOrigin;

    public Form() {
        setTitle("POC - Vladyslav Panchenko");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(menu);

        JMenuItem[] plikItems = new JMenuItem[3];
        plikItems[0] = otworz;
        plikItems[1] = lena;
        plikItems[2] = wyjscie;
        dodajDoMenu(plikMenu, plikItems);

        JMenuItem[] kolorItems = new JMenuItem[2];
        kolorItems[0] = jasKonGam;
        kolorItems[1] = histogramy;
        dodajDoMenu(kolorMenu, kolorItems);

        JMenuItem[] konwersjaItems = new JMenuItem[4];
        konwersjaItems[0] = konwersjaCMYK;
        konwersjaItems[1] = konwersjaHSL;
        konwersjaItems[2] = konwersjaLab;
        konwersjaItems[3] = konwersjaLuv;
        dodajDoMenu(konwersjaMenu, konwersjaItems);

        JMenuItem[] filtryItems = new JMenuItem[6];
        filtryItems[0] = splot;
        filtryItems[1] = gauss;
        filtryItems[2] = unsharpmask;
        filtryItems[3] = minimum;
        filtryItems[4] = maksimum;
        filtryItems[5] = mediana;
        dodajDoMenu(filtryMenu, filtryItems);

        JMenuItem[] fourierItems = new JMenuItem[4];
        fourierItems[0] = fourierRealis;
        fourierItems[1] = fourierImaginalis;
        fourierItems[2] = fourierSpektrum;
        fourierItems[3] = fourierFaza;
        dodajDoMenu(fourierMenu, fourierItems);

        panel = new Image();
        add(panel);
    }

    private void dodajDoMenu(JMenu menu, JMenuItem[] pozycje) {
        this.menu.add(menu);
        for(int i = 0; i < pozycje.length; i++) {
            menu.add(pozycje[i]);
            pozycje[i].addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object evt = ae.getSource();
        File file;

        if (evt == otworz) {
            JFileChooser search = new JFileChooser();
            FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
            search.setFileFilter(imageFilter);
            if (search.showOpenDialog(menu) == JFileChooser.APPROVE_OPTION) {
                file = search.getSelectedFile();
                try {
                    Image.imagePath = file.getPath();
                    Image.image = ImageIO.read(file);
                    imageOrigin = duplikujObraz(Image.image);
                    setSize(Form.imageOrigin.getWidth() + 16, Form.imageOrigin.getHeight() + 61);
                    this.repaint();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Сan't read file", "Error", 0);
                }
            }
        } else if (evt == lena) {
            try {
                file = new File("data/lena.bmp");
                Image.imagePath = file.getPath();
                Image.image = ImageIO.read(file);
                imageOrigin = duplikujObraz(Image.image);
                setSize(Form.imageOrigin.getWidth() + 16, Form.imageOrigin.getHeight() + 61);
                this.repaint();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Сan't read file", "Error", 0);
        }
        } else if (evt == wyjscie) {
            dispose();
        } else if (evt == jasKonGam) {
            JasnoscKontrastGamma jkg = new JasnoscKontrastGamma(this);
            jkg.setVisible(true);
        } else if (evt == histogramy) {
            HistogramDialog hist = new HistogramDialog(this);
            hist.setVisible(true);
        } else if (evt == konwersjaCMYK) {
            CMYK CMYKdialog = new CMYK(this);
            CMYKdialog.setVisible(true);
        } else if (evt == konwersjaHSL) {
            HSL HSLdialog = new HSL(this);
            HSLdialog.setVisible(true);
        } else if (evt == konwersjaLab) {
            Lab LabDialog = new Lab(this, 0);
            LabDialog.setVisible(true);
        } else if (evt == konwersjaLuv) {
            Luv LuvDialog = new Luv(this);
            LuvDialog.setVisible(true);
        } else if (evt == splot) {
            FiltrSplot splocik = new FiltrSplot(this);
            splocik.setVisible(true);
        } else if (evt == gauss) {
            FiltrSplotGauss splotGauss = new FiltrSplotGauss(this);
            splotGauss.setVisible(true);
        } else if (evt == unsharpmask) {
            FiltrSplotUnsharpMask splotUM = new FiltrSplotUnsharpMask(this);
            splotUM.setVisible(true);
        } else if (evt == minimum) {
            FiltrMinimum filtrMinimum = new FiltrMinimum(this);
            filtrMinimum.setVisible(true);
        } else if (evt == maksimum) {
            FiltrMaksimum filtrMaksimum = new FiltrMaksimum(this);
            filtrMaksimum.setVisible(true);
        } else if (evt == mediana) {
            FiltrMediana filtrMediana = new FiltrMediana(this);
            filtrMediana.setVisible(true);
        } else if (evt == fourierRealis) {
            FourierDialog fd = new FourierDialog(this, 0);
            fd.setVisible(true);
        } else if (evt == fourierImaginalis) {
            FourierDialog fd = new FourierDialog(this, 1);
            fd.setVisible(true);
        } else if (evt == fourierSpektrum) {
            FourierDialog fd = new FourierDialog(this, 2);
            fd.setVisible(true);
        } else if (evt == fourierFaza) {
            FourierDialog fd = new FourierDialog(this, 3);
            fd.setVisible(true);
        } 
    }
    
    public static void main(String[] args) {
        Form window = new Form();
        window.setVisible(true);
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        this.repaint();
    }

    public static void przywrocObraz() {
        ColorModel model = Form.imageOrigin.getColorModel();
        boolean isAlphaPremultiplied = model.isAlphaPremultiplied();
        WritableRaster r = Form.imageOrigin.copyData(null);
        Image.image = new BufferedImage(model, r, isAlphaPremultiplied, null);
    }

    public static BufferedImage duplikujObraz(BufferedImage image) {
        ColorModel model = image.getColorModel();
        boolean isAlphaPremultiplied = model.isAlphaPremultiplied();
        WritableRaster r = image.copyData(null);
        return new BufferedImage(model, r, isAlphaPremultiplied, null);
    }

    public static void refresh() {
        panel.setSize(Image.image.getWidth() + 16, Image.image.getHeight() + 61);
        panel.repaint();
        panel.revalidate();
    }
}

