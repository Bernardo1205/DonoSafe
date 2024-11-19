package main.java.com.example.mapa;

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
        graphics2D.setColor(Color.RED);
        graphics2D.fillOval(x - 5, y - 5, 10, 10);
    }
}
