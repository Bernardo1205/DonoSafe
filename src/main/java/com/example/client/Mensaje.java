package main.java.com.example.client;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mensaje extends JPanel {

    private String mensaje;
    private LocalDateTime hora;
    private String emisor;
    private TipoChat tipo;

    public Mensaje(String emisor, String mensaje, TipoChat tipo) {
        this(emisor, mensaje, LocalDateTime.now(), tipo);
    }

    public Mensaje(String emisor, String mensaje, LocalDateTime hora, TipoChat tipo) {
        this.mensaje = mensaje;
        this.hora = hora;
        this.emisor = emisor;
        this.tipo = tipo;

        setMensajeUI();
        revalidate();
        repaint();
    }

    private void setMensajeUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        /* Formato de la hora del mensaje:
            HH:mm si el mensaje ha sido enviado hoy
            dd/MM/YYYY si el mensaje no se envió hoy */
        String horaFecha = hora.format(hora.isAfter(LocalDate.now().atStartOfDay()) ?
                DateTimeFormatter.ofPattern("HH:mm") : DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm"));
        String mensajeTexto = switch (tipo) {
            case PRIVADO -> mensaje;
            case GRUPO -> emisor + ": " + mensaje;
        };

        /* JLabel centrado que contiene la hora del mensaje en formato hora:minuto */
        Box horaBox = Box.createHorizontalBox();
        JLabel horaLabel = new JLabel(hora.format(DateTimeFormatter.ofPattern("HH:mm")));
        horaLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        add(horaLabel);
        horaBox.add(Box.createHorizontalGlue());
        horaBox.add(horaLabel);
        horaBox.add(Box.createHorizontalGlue());
        add(horaBox);

        /* JLabel que contiene el mensaje */
        JLabel messageLabel = new JLabel(mensajeTexto);
        messageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2), // Contorno externo de color
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        messageLabel.setBackground(new Color(240, 240, 240));
        add(messageLabel);

        /* Box horizontal que posiciona el JLabel del mensaje a la izquierda o derecha en función del emisor */
        Box messageBox = Box.createHorizontalBox();
        if (emisor.equals("You")) {
            messageBox.add(Box.createHorizontalGlue());
            messageBox.add(messageLabel);
        } else {
            messageBox.add(messageLabel);
            messageBox.add(Box.createHorizontalGlue());
        }
        add(messageBox);
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getHora() {
        return hora;
    }

    public void setHora(LocalDateTime hora) {
        this.hora = hora;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }
}
