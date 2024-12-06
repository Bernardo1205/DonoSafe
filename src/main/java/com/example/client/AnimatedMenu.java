package main.java.com.example.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel que aparece al hacer click en el botón de menú. Contiene opciones para
 * mostrar el historial de alertas y los grupos
 */
public class AnimatedMenu extends JPanel {

    private final int MAX_WIDTH = 200;
    private final int HEIGHT = 600;

    private boolean isOpen = false;

    private JLayeredPane layeredPane;

    public AnimatedMenu(JLayeredPane layeredPane) {
        setLayout(null);
        setPreferredSize(new Dimension(200, 600));
        setBackground(new Color(255, 255, 255, 200));

        this.layeredPane = layeredPane;

        /* Creación de los botones del menú */
        /* FIXME Dar funcionalidad a las opciones */
        JButton mostrarHistorial = createOptionButton("Historial");
        mostrarHistorial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Historial");
            }
        });
        add(mostrarHistorial);

        JButton mostrarGrupos = createOptionButton("Grupos");
        mostrarGrupos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Grupos");
            }
        });
        add(mostrarGrupos);

        JButton mostrarUsuarios = createOptionButton("Configuración");
        mostrarUsuarios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Configuración");
            }
        });
        add(mostrarUsuarios);
    }

    /**
     * Crea un botón con el texto pasado como parámetro para que todas las opciones
     * tengan el mismo formato
     */
    private JButton createOptionButton(String text) {
        JButton button = new JButton(text);
        button.setBounds(10, 110 + getComponentCount()*50, 0, 40);
        button.setBackground(new Color(255, 100, 100));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));

        return button;
    }

    /**
     * Abrir o cerrar el menú
     */
    public void toggleMenu() {
        /* Desplazamiento en el eje X */
        int deltaX = (!isOpen ? MAX_WIDTH : -MAX_WIDTH);

        /* Realizar la transición */
        transicion(deltaX, 50);

        isOpen = !isOpen;
    }

    /**
     * Realiza una transición de la posición de los botones del menú a través
     * de un hilo/Timer
     *
     * @param deltaX Desplazamiento en el eje X
     * @param speed  Velocidad de la transición
     */
    public void transicion(int deltaX, int speed) {
        Timer timer = new Timer(10, null);
        timer.addActionListener(new ActionListener() {
            int step = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (step < speed) {
                    /* Mover los botones que se encuentran en el nivel 1 del layeredPane */
                    for (Component component : layeredPane.getComponentsInLayer(1)) {
                        if (component instanceof JButton) {
                            component.setBounds(component.getX() + deltaX / speed, component.getY(), component.getWidth(), component.getHeight());
                        }
                    }

                    /* Expandir el panel */
                    setBounds(0, 0, getWidth() + deltaX/speed, HEIGHT);

                    /* Expandir los componentes del panel */
                    for (Component component : getComponents()) {
                        component.setBounds(getX()+10, component.getY(), getWidth()-20, component.getHeight());
                    }

                    step++;
                } else {
                    timer.stop();
                }
            }
        });
        timer.start();
    }
}
