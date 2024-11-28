package main.java.com.example.mapa;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.awt.geom.Point2D;

public class MyWaipontRender implements WaypointRenderer<Reporte> {

    @Override
    public void paintWaypoint(Graphics2D graphics2D, JXMapViewer jxMapViewer, Reporte waypoint) {
        Point2D point2D = jxMapViewer.getTileFactory().geoToPixel(waypoint.getPosition(),jxMapViewer.getZoom());
        int x = (int) point2D.getX();
        int y = (int) point2D.getY();
        int radio =8;

        /* Guardar estado transparencia */
        Composite composite = graphics2D.getComposite();

        /* Definir color y opacidad del relleno de la circunferencia */
        Color[] colorWaypoint = switch (waypoint.getTipoPeligro()) {
            case "BAJO" -> new Color[]{new Color(26, 255, 0,80), Color.GREEN};
            case "MEDIO" -> new Color[]{new Color(255, 251, 0,80), Color.YELLOW};
            case "ALTO" -> new Color[]{new Color(255,0,9,80), Color.RED};

            default -> throw new IllegalStateException("Unexpected value: " + waypoint.getTipoPeligro());
        };
        /* Color del relleno */
        graphics2D.setColor(colorWaypoint[0]);
        /* Rellenar el círculo */
        graphics2D.fillOval(x-radio,y-radio,radio*2,radio*2);

        /* Restaurar el color para usar otro tono de rojo y dibujar contorno circunferencia */
        graphics2D.setComposite(composite);
        /* Color del contorno */
        graphics2D.setColor(colorWaypoint[1]);
        /* Dibujar el contorno */
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.drawOval(x-radio,y-radio,radio*2,radio*2);
    }
}
