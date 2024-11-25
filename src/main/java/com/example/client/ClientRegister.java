package main.java.com.example.client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClientRegister extends JFrame {

    public ClientRegister() {
        setTitle("Creación de Cuenta");
        setSize(350, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font inputFont = new Font("Arial", Font.PLAIN, 12);
        Color backgroundColor = new Color(240, 248, 255);
        Color buttonColor = new Color(0, 120, 215);
        Color buttonTextColor = Color.WHITE;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        TitledBorder title = BorderFactory.createTitledBorder("Crear cuenta");
        title.setTitleJustification(TitledBorder.CENTER);
        mainPanel.setBorder(title);

        JLabel nameLabel = new JLabel("Nombre:");
        nameLabel.setFont(labelFont);
        nameLabel.setBounds(10, 40, 120, 25);
        mainPanel.add(nameLabel);

        JTextField nameField = new JTextField(20);
        nameField.setFont(inputFont);
        nameField.setBounds(170, 40, 150, 25);
        mainPanel.add(nameField);

        JLabel emailLabel = new JLabel("Correo Gmail:");
        emailLabel.setFont(labelFont);
        emailLabel.setBounds(10, 90, 120, 25);
        mainPanel.add(emailLabel);

        JTextField emailField = new JTextField(20);
        emailField.setFont(inputFont);
        emailField.setBounds(170, 90, 150, 25);
        mainPanel.add(emailField);

        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setBounds(10, 140, 120, 25);
        mainPanel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(inputFont);
        passwordField.setBounds(170, 140, 150, 25);
        mainPanel.add(passwordField);

        JLabel passwordLabel2 = new JLabel("Repita la contraseña:");
        passwordLabel2.setFont(labelFont);
        passwordLabel2.setBounds(10, 190, 150, 25);
        mainPanel.add(passwordLabel2);

        JPasswordField passwordField2 = new JPasswordField(20);
        passwordField2.setFont(inputFont);
        passwordField2.setBounds(170, 190, 150, 25);
        mainPanel.add(passwordField2);

        JButton createAccountButton = new JButton("Crear Cuenta");
        createAccountButton.setFont(new Font("Arial", Font.BOLD, 14));
        createAccountButton.setBackground(buttonColor);
        createAccountButton.setForeground(buttonTextColor);
        createAccountButton.setBounds(75, 250, 200, 40);
        mainPanel.add(createAccountButton);

        JLabel statusLabel = new JLabel("");
        statusLabel.setFont(labelFont);
        statusLabel.setBounds(10, 310, 320, 50);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(statusLabel);

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreArchivo = System.getProperty("user.dir") + File.separator + "datos.txt";
                String email = emailField.getText();
                String name = nameField.getText();
                String password = new String(passwordField.getPassword());
                String password2 = new String(passwordField2.getPassword());

                if (!email.endsWith("@gmail.com")) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Por favor ingrese un correo válido.");
                } else if (!password.equals(password2)) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Las contraseñas deben ser iguales.");
                } else if (password.length() < 6) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("<html>La contraseña debe tener al menos 6 <br>caracteres.</html>");
                } else {
                    statusLabel.setForeground(Color.GREEN);
                    statusLabel.setText("Cuenta creada exitosamente.");
                    saveDataToFile(name, email, password, nombreArchivo);
                }
            }
        });
        getRootPane().setDefaultButton(createAccountButton);
    }

    private void saveDataToFile(String name, String email, String password, String nombreArchivo) {
        /* FIXME guardar el usuario en la base de datos */
        String contenido = name + "," + email + "," + password;
        System.out.println(contenido);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo, true))) {
            writer.write(contenido);
            writer.newLine();
            System.out.println("Información guardada correctamente al archivo " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al guardar la información: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientRegister window = new ClientRegister();
            window.setVisible(true);
        });
    }
}
