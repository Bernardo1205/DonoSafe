import javax.swing.*;
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

        frame = new JFrame("Log In");
        frame.setSize(300, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 20, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(50, 90, 80, 25);
        panel.add(loginButton);

        JButton registerButton = new JButton("Crear cuenta");
        registerButton.setBounds(140, 90, 125, 25);
        panel.add(registerButton);

        JLabel statusLabel = new JLabel("");
        statusLabel.setBounds(10, 130, 250, 25);
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
                    statusLabel.setText("Invalid username or password.");
                }
            }
        });

        // Acción del botón de registro
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ClientRegister registerWindow = new ClientRegister(); // Crea la instancia
                registerWindow.setVisible(true);  // Asegúrate de que la ventana de registro sea visible
                frame.dispose();  // Cierra la ventana de login
            }
        });
    }
    private boolean validateLogin(String username, String password){
        return registeredUsers.containsKey(username) && registeredUsers.get(username).equals(password);
    }
}
