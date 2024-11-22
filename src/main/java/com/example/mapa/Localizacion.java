package mapa;
import org.jxmapviewer.viewer.GeoPosition;

public class Localizacion {
    private String adress;
    private GeoPosition geoPosition;

    public Localizacion(){
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public GeoPosition getGeoPosition() {
        return geoPosition;
    }

    public void setGeoPosition(GeoPosition geoPosition) {
        this.geoPosition = geoPosition;
    }
}
