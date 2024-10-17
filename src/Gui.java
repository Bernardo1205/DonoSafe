
import javax.swing.*;
import java.awt.*;

public class Gui extends JFrame {
    public Gui() {
        setSize(300, 500);
        setLayout(new BorderLayout(5, 5));

        JPanel panelAbajo = new JPanel(new GridLayout(1, 3));

        JButton windowAlerts = new JButton("Alertas");
        windowAlerts.setSize(50, 50);
        JButton windowMap = new JButton("Mapa");
        windowMap.setSize(50, 50);
        JButton windowSOS = new JButton("SOS");
        windowSOS.setSize(50, 100);

        panelAbajo.add(windowAlerts);
        panelAbajo.add(windowMap);
        panelAbajo.add(windowSOS);


        add(panelAbajo, BorderLayout.SOUTH);
        panelAbajo.setVisible(true);

    }

    public static void main(String[] args) {
        Gui gui = new Gui();
        gui.setVisible(true);
    }
}