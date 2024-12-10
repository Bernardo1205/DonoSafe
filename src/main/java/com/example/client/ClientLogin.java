package main.java.com.example.client;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

/**
 * La clase ClientLogin se encarga de renderizar la pantalla de inicio de sesión.
 * Contiene métodos para validar las credenciales del usuario y abrir la ventana
 *
 * @see Client
 * @see ClientRegister
 */
public class ClientLogin {

    private static Font font = new Font(Font.DIALOG, Font.PLAIN, 14);
    private static final int WIDTH = 350;
    private static final int HEIGHT = 550;
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Client client;

    public ClientLogin(Client client) {
        this.client = client;

        frame = new JFrame("Inicio Sesión");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        Color backgroundColor = new Color(240, 248, 255);

        JPanel panel = new JPanel();
        frame.add(panel, BorderLayout.CENTER);
        panel.setLayout(null);
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        TitledBorder title = BorderFactory.createTitledBorder("Bienvenido a Donosafe");
        title.setTitleJustification(TitledBorder.CENTER);
        panel.setBorder(title);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font inputFont = new Font("Arial", Font.PLAIN, 12);
        Color buttonColor = new Color(0, 120, 215);
        Color buttonTextColor = Color.WHITE;


        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setFont(labelFont);
        userLabel.setBounds(10, 40, 120, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setFont(inputFont);
        usernameField.setBounds(170, 40, 150, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setBounds(10, 90, 120, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setFont(inputFont);
        passwordField.setBounds(170, 90, 150, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Inicio Sesión");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(buttonColor);
        loginButton.setForeground(buttonTextColor);
        loginButton.setBounds(75, 150, 200, 40);
        panel.add(loginButton);

        JButton registerButton = new JButton("Registrarse");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(buttonColor);
        registerButton.setForeground(buttonTextColor);
        registerButton.setBounds(75, 210, 200, 40);
        panel.add(registerButton);

        JLabel statusLabel = new JLabel("");
        statusLabel.setFont(labelFont);
        statusLabel.setBounds(10, 280, 320, 50);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(statusLabel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    switch (validateLogin(username, password)) {
                        case 200:
                            /* Si las credenciales son correctas, llama a openChatWindow, la cual inicia la App
                            * y comienza una comunicación formal con el servidor */
                            String[] clientInfo = client.waitForMessage().split(",");
                            client.openChatWindow(clientInfo);
                            frame.dispose();
                            break;
                        case 401:
                            /* Si las credenciales son incorrectas, se lo hace saber al usuario de forma gráfica
                             * mostrándole un campo en color rojo con el siguiente texto */
                            statusLabel.setText("Contraseña o usuario incorrecto.");
                            statusLabel.setForeground(Color.RED);
                            break;
                        case 409:
                            /* Si el usuario ya está conectado, se lo hace saber al usuario de forma gráfica
                             * mostrándole un campo en color rojo con el siguiente texto */
                            statusLabel.setText("Conexión ya establecida.");
                            statusLabel.setForeground(Color.RED);

                            //Envia mensaje al servidor indicando que le pase los datos inicales (mensajes del grupo)
                            client.sendMessageToServer("[Get Content] | " + client.getUsername() + ","+ String.valueOf(client.getId()));
                            break;
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                frame.dispose();
                new ClientRegister(client).setVisible(true);
            }
        });
    }

    /** Se le envía el usuario y contraseña al servidor, el cual devuelve VALID si las credenciales son correctas,
     * INVALID si no lo son y ConnectionError en caso de que el usuario ya esté conectado.
     */
    private int validateLogin(String username, String password) throws IOException, InterruptedException {
        client.sendMessageToServer(username+","+password);
        String serverValidation = client.waitForMessage();

        return Integer.parseInt(serverValidation.substring(6, 9));
    }
}
