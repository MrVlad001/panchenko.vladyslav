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
    private JMenu menuFile = new JMenu("File");
    private JMenuItem open = new JMenuItem("Open");
    private JMenuItem lena = new JMenuItem("Test");
    private JMenuItem exit = new JMenuItem("Exit");
    private JMenu colorMenu = new JMenu("Color");
    private JMenuItem BCG = new JMenuItem("Brightness / Contrast / Gamma");
    private JMenuItem histogram = new JMenuItem("Histogramy");
    private JMenu menuConversion = new JMenu("Konwersja");
    private JMenuItem CMYKconversion = new JMenuItem("RGB ↔ CMYK");
    private JMenuItem HSLconversion = new JMenuItem("RGB ↔ HSL");
    private JMenuItem LabConversion = new JMenuItem("RGB ↔ L*a*b*");
    private JMenuItem LuvConversion = new JMenuItem("RGB ↔ L*u*v*");
    private JMenu menuFiltr = new JMenu("Filtry");
    private JMenuItem splot = new JMenuItem("Splot");
    private JMenuItem gauss = new JMenuItem("Splot Gaussa");
    private JMenuItem unsharpmask = new JMenuItem("Splot Unsharp Mask");
    private JMenuItem min = new JMenuItem("Minimum");
    private JMenuItem max = new JMenuItem("Maksimum");
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

        JMenuItem[] fileItems = new JMenuItem[3];
        fileItems[0] = open;
        fileItems[1] = lena;
        fileItems[2] = exit;
        addToMenu(menuFile, fileItems);

        JMenuItem[] colorItems = new JMenuItem[2];
        colorItems[0] = BCG;
        colorItems[1] = histogram;
        addToMenu(colorMenu, colorItems);

        JMenuItem[] conversionItems = new JMenuItem[4];
        conversionItems[0] = CMYKconversion;
        conversionItems[1] = HSLconversion;
        conversionItems[2] = LabConversion;
        conversionItems[3] = LuvConversion;
        addToMenu(menuConversion, conversionItems);

        JMenuItem[] filtrItems = new JMenuItem[6];
        filtrItems[0] = splot;
        filtrItems[1] = gauss;
        filtrItems[2] = unsharpmask;
        filtrItems[3] = min;
        filtrItems[4] = max;
        filtrItems[5] = mediana;
        addToMenu(menuFiltr, filtrItems);

        JMenuItem[] fourierItems = new JMenuItem[4];
        fourierItems[0] = fourierRealis;
        fourierItems[1] = fourierImaginalis;
        fourierItems[2] = fourierSpektrum;
        fourierItems[3] = fourierFaza;
        addToMenu(fourierMenu, fourierItems);

        panel = new Image();
        add(panel);
    }

    private void addToMenu(JMenu menu, JMenuItem[] position) {
        this.menu.add(menu);
        for(int i = 0; i < position.length; i++) {
            menu.add(position[i]);
            position[i].addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object evt = ae.getSource();
        File file;

        if (evt == open) {
            JFileChooser search = new JFileChooser();
            FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
            search.setFileFilter(imageFilter);
            if (search.showOpenDialog(menu) == JFileChooser.APPROVE_OPTION) {
                file = search.getSelectedFile();
                try {
                    Image.imagePath = file.getPath();
                    Image.image = ImageIO.read(file);
                    imageOrigin = duplicateImage(Image.image);
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
                imageOrigin = duplicateImage(Image.image);
                setSize(Form.imageOrigin.getWidth() + 16, Form.imageOrigin.getHeight() + 61);
                this.repaint();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Сan't read file", "Error", 0);
        }
        } else if (evt == exit) {
            dispose();
        } else if (evt == BCG) {
            JasnoscKontrastGamma jkg = new JasnoscKontrastGamma(this);
            jkg.setVisible(true);
        } else if (evt == histogram) {
            HistogramDialog hist = new HistogramDialog(this);
            hist.setVisible(true);
        } else if (evt == CMYKconversion) {
            CMYK CMYKdialog = new CMYK(this);
            CMYKdialog.setVisible(true);
        } else if (evt == HSLconversion) {
            HSL HSLdialog = new HSL(this);
            HSLdialog.setVisible(true);
        } else if (evt == LabConversion) {
            Lab LabDialog = new Lab(this, 0);
            LabDialog.setVisible(true);
        } else if (evt == LuvConversion) {
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
        } else if (evt == min) {
            FiltrMinimum filtrMinimum = new FiltrMinimum(this);
            filtrMinimum.setVisible(true);
        } else if (evt == max) {
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
    // Przywroc obrazek
    public static void restoreImage() {
        ColorModel model = Form.imageOrigin.getColorModel();
        boolean isAlphaPremultiplied = model.isAlphaPremultiplied();
        WritableRaster r = Form.imageOrigin.copyData(null);
        Image.image = new BufferedImage(model, r, isAlphaPremultiplied, null);
    }
    // Dublikuj obrazek
    public static BufferedImage duplicateImage(BufferedImage image) {
        ColorModel model = image.getColorModel();
        boolean isAlphaPremultiplied = model.isAlphaPremultiplied();
        WritableRaster r = image.copyData(null);
        return new BufferedImage(model, r, isAlphaPremultiplied, null);
    }
    // Odśwież
    public static void refresh() {
        panel.setSize(Image.image.getWidth() + 16, Image.image.getHeight() + 61);
        panel.repaint();
        panel.revalidate();
    }
}

