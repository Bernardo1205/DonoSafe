import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class ClientLogin {
    private static HashMap<String, String> registeredUsers = new HashMap<>();
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Client client;

    public ClientLogin(Client client) {
        this.client = client;

        // Simulación de usuarios registrados
        for (int i = 1; i < 10; i++) {
            registeredUsers.put(String.valueOf(i), String.valueOf(i));
        }

        frame = new JFrame("Iniciar Sesión");
        frame.setSize(350, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);


        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font inputFont = new Font("Arial", Font.PLAIN, 12);
        Color backgroundColor = new Color(240, 248, 255);
        Color buttonColor = new Color(0, 120, 215);
        Color buttonTextColor = Color.WHITE;

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        TitledBorder title = BorderFactory.createTitledBorder("Iniciar sesión");
        title.setTitleJustification(TitledBorder.CENTER);
        panel.setBorder(title);

        frame.add(panel);


        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        userLabel.setBounds(10, 40, 120, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setFont(inputFont);
        usernameField.setBounds(170, 40, 150, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setBounds(10, 90, 120, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setFont(inputFont);
        passwordField.setBounds(170, 90, 150, 25);
        panel.add(passwordField);

        // Botones de login y registro
        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(buttonColor);
        loginButton.setForeground(buttonTextColor);
        loginButton.setBounds(75, 150, 200, 40);
        panel.add(loginButton);

        JButton registerButton = new JButton("Crear cuenta");
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

        // Acción del botón de login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (validateLogin(username, password)) {
                    client.openChatWindow(username);
                    frame.dispose();
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Username o contraseña incorrectos.");
                }
            }
        });

        // Acción del botón de registro
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientRegister registerWindow = new ClientRegister();
                registerWindow.setVisible(true);
                frame.dispose();
            }
        });

        frame.getRootPane().setDefaultButton(loginButton);
        frame.setVisible(true);
    }

    private boolean validateLogin(String username, String password) {
        return registeredUsers.containsKey(username) && registeredUsers.get(username).equals(password);
    }
}
