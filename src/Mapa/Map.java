package Mapa;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class Map extends JXMapViewer{
    private  Set<MyWaypoint> setWaipoints = new HashSet<>(); //Set para almacenar WayPoints
    private  WaypointPainter waypointPainter; //Render de los Waipoints
    private CompoundPainter compoundPainter ;

    public Map(){

        /* Clase abstarcta que define la estructura de como se ve el mapa (Zoom , tamaño de los tiles...)
        OSM tipo de tiles para renderizar el TileFactory */
        TileFactoryInfo factoryInfo = new OSMTileFactoryInfo();

        /* DefaultTitle class encargada de organozar tiles (pequeños fragmentos del mapa)
        Solicitando los tiles al OSM y poniendolos en el mapa */
        DefaultTileFactory tileFactory  = new DefaultTileFactory(factoryInfo);
        setTileFactory(tileFactory);

        /* Numero de threads que se usaran para cargar los tiles de forma paralala */
        tileFactory.setThreadPoolSize(8);

        /* Geolocalizacion donosti */
        GeoPosition sanSebastian = new GeoPosition(43.2918100, -1.9885100);
        setZoom(6);
        setAddressLocation(sanSebastian);

        /* Controlador para desplazarte */
        PanMouseInputListener inputListener = new PanMouseInputListener(this);

        /* Listener para capturar moviminentos estaticos del
        raton (clic en boton o entrar region de una interfaz) */
        addMouseListener(inputListener);

        /* Listener para interacciones dinamicas (arraster o seguimineto continuo del movimiento),
        casos en los que necesitas desplazar un objeto o hacer un dibujo mientras el ratón se mueve. */
        addMouseMotionListener(inputListener);

        /* Hacer zoom con el raton */
        addMouseWheelListener(new ZoomMouseWheelListenerCenter(this));


        /* Iniciar render de los Waypoints */
        this.waypointPainter =new WaypointPainter<>();

        /* Añadimos nuestro render personalizado para visualizar los waypoints */
        this.waypointPainter.setRenderer(new MyWaipontRender());

        /* Pasar el set de waypoints al render */
        this.waypointPainter.setWaypoints(setWaipoints);

       /* Esta clase sirve para facilitar el renderizado de multiplipes Pointers de distintos tipos.
        Por ejemplo, podrías tener waypoints y rutas sobre el mapa, y el CompoundPainter facilita que
        ambos se muestren sin sobreescribir o interferir entre ellos. */

        this.compoundPainter = new CompoundPainter<>();
        compoundPainter.setPainters(waypointPainter);




    }
    public void añadirWaypoint(Set setWaipoints){

        /* Actualizar los waypoints en el painter y redibujar el mapa */
        waypointPainter.setWaypoints(new HashSet<>(setWaipoints));

        /* Cuando el mapa se redibuja todos los painters que estab en el compondPainter se renderizaran en el mapa */
        compoundPainter.setPainters(waypointPainter);
        setOverlayPainter(compoundPainter);


    }

    public Set<MyWaypoint> getSetWaipoints() {
        return setWaipoints;
    }
}
