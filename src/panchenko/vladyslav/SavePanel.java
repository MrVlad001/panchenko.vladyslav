package panchenko.vladyslav;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author Vladyslav
 */
public class SavePanel extends JPanel implements ActionListener {

    private JButton ok = new JButton("Ok");
    private JButton cofnij = new JButton("Cofnij");
    private JButton anuluj = new JButton("Anuluj");
    private JDialog parent;

    public SavePanel(JDialog parent) {
        super(new GridLayout(1, 2, 10, 10));
        this.parent = parent;
        add(ok);
        add(cofnij);
        add(anuluj);
        ok.addActionListener(this);
        cofnij.addActionListener(this);
        anuluj.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object evt = ae.getSource();
        if (evt == ok) {
            ok();
        } else if (evt == cofnij) {
            cofnij();
        } else if (evt == anuluj) {
            anuluj();
        }
    }

    public void ok() {
        Form.imageOrigin = Form.duplikujObraz(Obraz.image);
        Form.panel.repaint();
        parent.dispose();

    }

    public void cofnij() {
        Form.przywrocObraz();
        Form.panel.repaint();
    }

    public void anuluj() {
        Form.przywrocObraz();
        Form.panel.repaint();
        parent.dispose();
    }
}