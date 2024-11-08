package main.java.com.example.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class ClientLogin {
    private static HashMap<String, String> registeredUsers = new HashMap<>();
    private static Font font = new Font(Font.DIALOG, Font.PLAIN, 14);
    private static final int WIDTH = 350;
    private static final int HEIGHT = 500;
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Client client;

    public ClientLogin(Client client) {
        this.client = client;
        for (int i=1; i<10; i++) {
            registeredUsers.put(String.valueOf(i), String.valueOf(i));
            //FIXME: registeredUsers should contain the userName and hashed password read from the database
        }

        frame = new JFrame("Log In");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        frame.add(panel, BorderLayout.CENTER);
        placeComponents(panel);
        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel fraseBienvenida = new JLabel("Bienvenido a Donosafe");
        fraseBienvenida.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        fraseBienvenida.setBounds(WIDTH/4-fraseBienvenida.getText().length(), 20, WIDTH, 30);
        panel.add(fraseBienvenida);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(font);
        userLabel.setBounds(10, 70, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setFont(font);
        usernameField.setBounds(100, 70, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(font);
        passwordLabel.setBounds(10, 100, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 100, 165, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(font);
        loginButton.setBounds(100, 160, 80, 25);
        panel.add(loginButton);

        JLabel statusLabel = new JLabel("");
        statusLabel.setBounds(10, 170, 250, 25);
        panel.add(statusLabel);

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
    }

    private boolean validateLogin(String username, String password) {
        return registeredUsers.containsKey(username) && registeredUsers.get(username).equals(password);
    }
}
