package main.java.com.example.client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDateTime;

public class ClientRegister extends JFrame {

    public ClientRegister() {
        setTitle("Creación de Cuenta");
        setSize(350, 700);
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

        JLabel ageLabel = new JLabel("Edad:");
        ageLabel.setFont(labelFont);
        ageLabel.setBounds(10, 240, 120, 25);
        mainPanel.add(ageLabel);

        JTextField ageField = new JTextField(3);
        ageField.setFont(inputFont);
        ageField.setBounds(170, 240, 150, 25);
        mainPanel.add(ageField);

        JLabel genderLabel = new JLabel("Sexo:");
        genderLabel.setFont(labelFont);
        genderLabel.setBounds(10, 290, 120, 25);
        mainPanel.add(genderLabel);

        String[] genders = {"Masculino", "Femenino", "Otro"};
        JComboBox<String> genderCombo = new JComboBox<>(genders);
        genderCombo.setFont(inputFont);
        genderCombo.setBounds(170, 290, 150, 25);
        mainPanel.add(genderCombo);

        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setFont(labelFont);
        userLabel.setBounds(10, 340, 120, 25);
        mainPanel.add(userLabel);

        JTextField userField = new JTextField(20);
        userField.setFont(inputFont);
        userField.setBounds(170, 340, 150, 25);
        mainPanel.add(userField);

        JLabel descriptionLabel = new JLabel("Descripción:");
        descriptionLabel.setFont(labelFont);
        descriptionLabel.setBounds(10, 390, 120, 25);
        mainPanel.add(descriptionLabel);

        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setFont(inputFont);
        descriptionArea.setBounds(170, 390, 150, 60);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        mainPanel.add(descriptionArea);

        JButton createAccountButton = new JButton("Crear Cuenta");
        createAccountButton.setFont(new Font("Arial", Font.BOLD, 14));
        createAccountButton.setBackground(buttonColor);
        createAccountButton.setForeground(buttonTextColor);
        createAccountButton.setBounds(75, 470, 200, 40);
        mainPanel.add(createAccountButton);

        JLabel statusLabel = new JLabel("");
        statusLabel.setFont(labelFont);
        statusLabel.setBounds(10, 550, 320, 50);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(statusLabel);

        JButton backButton = new JButton("Volver");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(255, 48, 60));
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(75, 520, 200, 40);
        mainPanel.add(backButton);

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String password2 = new String(passwordField2.getPassword()).trim();
                String age = ageField.getText().trim();
                String gender = (String) genderCombo.getSelectedItem();
                String username = userField.getText().trim();
                String description = descriptionArea.getText().trim();

                // Validación de campos obligatorios
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty() ||
                        age.isEmpty() || username.isEmpty() || description.isEmpty()) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Todos los campos son obligatorios.");
                    return;
                }

                // Validación de correo
                if (!email.endsWith("@gmail.com")) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Ingrese un correo válido.");
                    return;
                }

                // Validación de contraseñas
                if (!password.equals(password2)) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Las contraseñas no coinciden.");
                    return;
                }

                // Validación de longitud de contraseña
                if (password.length() < 6) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("La contraseña debe tener al menos 6 caracteres.");
                    return;
                }

                // Validación de edad (opcional)
                try {
                    int parsedAge = Integer.parseInt(age);
                    if (parsedAge <= 0) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Ingrese una edad válida.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("La edad debe ser un número.");
                    return;
                }

                // Si pasa todas las validaciones
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Cuenta creada exitosamente.");

                // Simulación de almacenamiento (puedes reemplazar con código para guardar datos)
                System.out.println("Cuenta creada:");
                System.out.println("Nombre: " + name);
                System.out.println("Correo: " + email);
                System.out.println("Edad: " + age);
                System.out.println("Sexo: " + gender);
                System.out.println("Usuario: " + username);
                System.out.println("Descripción: " + description);
            }
        });


        getRootPane().setDefaultButton(createAccountButton);
    }


}

