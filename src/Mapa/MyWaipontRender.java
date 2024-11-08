package Mapa;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.awt.geom.Point2D;

public class MyWaipontRender implements WaypointRenderer<MyWaypoint> {

    @Override
    public void paintWaypoint(Graphics2D graphics2D, JXMapViewer jxMapViewer, MyWaypoint waypoint) {
        Point2D point2D = jxMapViewer.getTileFactory().geoToPixel(waypoint.getPosition(),jxMapViewer.getZoom());
        int x = (int) point2D.getX();
        int y = (int) point2D.getY();
        int radio =8;

        /* Guardar estado transparencia */
        Composite composite = graphics2D.getComposite();

        /* Definir color y opacidad  del reyeno de la circunferencia*/
        if (waypoint.getPeligrosidad().equals("BAJO")){
            graphics2D.setColor(new Color(26, 255, 0,80));
        } else if (waypoint.getPeligrosidad().equals("MEDIO")) {
            graphics2D.setColor(new Color(255, 251, 0,80));
        }
        else {
            graphics2D.setColor(new Color(255,0,9,80));
        }
        
        graphics2D.fillOval(x-radio,y-radio,radio*2,radio*2);

        /* Restaurar el color ya que usaremos otro tono de rojo y dibujar contorno circunferencia */
       graphics2D.setComposite(composite);
        if (waypoint.getPeligrosidad().equals("BAJO")) {
            graphics2D.setColor(Color.GREEN);
        } else if (waypoint.getPeligrosidad().equals("MEDIO")) {
            graphics2D.setColor(Color.YELLOW);
        }else {
            graphics2D.setColor(Color.RED);
        }
       graphics2D.setStroke(new BasicStroke(2));
       graphics2D.drawOval(x-radio,y-radio,radio*2,radio*2);
    }
}
