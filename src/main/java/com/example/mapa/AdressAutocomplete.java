package main.java.com.example.mapa;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AdressAutocomplete extends JTextField {

    private HashMap<String,GeoPosition>dict;

    private static final String apiKey = "1e47cf29776640d39b07c06e693a0346"; //Api key para usar API de autocomplete de Geoapify
    private static final String geoapify_url = "https://api.geoapify.com/v1/geocode/autocomplete";

    /* Menú que ofrece sugerencias prediciendo la dirección que el usuario quiere ingresar */
    private JPopupMenu jPopupSugerencias;
    private DefaultListModel<String> listModel;

    public AdressAutocomplete() {
        listModel = new DefaultListModel<>();
        jPopupSugerencias = new JPopupMenu();
        jPopupSugerencias.setFocusable(false);
        dict = new HashMap<>();
        getDocument().addDocumentListener(new sugestionesListener());
    }

    private List<String> getSugestions(String input) throws IOException {

        String encodedInput = URLEncoder.encode(input, "UTF-8");
        List<String> listaSugestiones = new ArrayList<>();
        String request = String.format("%s?text=%s&apiKey=%s" , geoapify_url, encodedInput,apiKey);

        /* Abrir conexion con servidor y hacer un get del input */
        URL url = new URL(request);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        //Leer la respuesta y ponerla como un StringBuilder
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder respuesta = new StringBuilder();
        String lineaRespuesta;

        /* Leer lineas de respuesta y añadirlas al StringBuilder */
        while ((lineaRespuesta= in.readLine())!= null){
            respuesta.append(lineaRespuesta);
        }
        in.close();

        /* Psar la respuesta a JsonObject para tener mayor facilidad a la hora de interactuar con los datos obtenidos */
        JsonObject jsonObject = JsonParser.parseString(respuesta.toString()).getAsJsonObject();

        //Array con todas las features del json
        JsonArray arrayFeatures = jsonObject.getAsJsonArray("features");
        /*
        Ejemplo archivo JSon de features :{
          "features": [
            {
              "properties": {
                "formatted": "1600 Pennsylvania Avenue NW, Washington, DC 20500, USA",
                "country": "USA",
                "state": "District of Columbia",
                "city": "Washington",
                "postcode": "20500",
                "street": "Pennsylvania Avenue NW",
                "housenumber": "1600"

              },
              "geometry": {
                "type": "Point",
                "coordinates": [-77.0365, 38.8977]
              },
              "type": "Feature"
            },
            {
              "properties": {
                "formatted": "221B Baker Street, Marylebone, London NW1 6XE, UK",
                "country": "United Kingdom",
                "state": "England",
                "city": "London",
                "postcode": "NW1 6XE",
                "street": "Baker Street",
                "housenumber": "221B"
              },
              "geometry": {
                "type": "Point",
                "coordinates": [-0.1585557, 51.523767]
              },
              "type": "Feature"
            }

          ],
          "type": "FeatureCollection"
        }
        */

        /* Ten en cuenta que en features  nos da una serie de ubicaciones , de cada una queremos el campo de formatted que contiene la direccion */
        for(int i=0 ; i< arrayFeatures.size();i++){
            JsonObject features = arrayFeatures.get(i).getAsJsonObject();
            JsonObject properties = features.getAsJsonObject("properties");
            String adress = properties.get("formatted").getAsString();

            /* Coger las geocoordenadas de cada sugestion y guardarlas en el hasmap */
            JsonObject geometry = features.getAsJsonObject("geometry");
            JsonArray coordinates = geometry.getAsJsonArray("coordinates");
            GeoPosition geoPosition = new GeoPosition(coordinates.get(0).getAsDouble(),coordinates.get(1).getAsDouble());
            dict.put(adress,geoPosition);

            listaSugestiones.add(adress);
        }
        return listaSugestiones;
    }


    private void mostrarSugerencias(List<String> listaSugerencias){
        /* Quitamos las sugerencias que habia antes en la lista */
        listModel.clear();
        for (String sugestion:listaSugerencias){
            listModel.addElement(sugestion);
        }
        JList jList = new JList<>(listModel);

        /* Permitir la visibilidad del popUp se deje de ver solo cuando el jList pierda el foco, ya que si no se añade esta
        función muchas veces no te permite seleccionar elementos del Jlist ya que se perdió el foco en el JPopUp */
        jList.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                jPopupSugerencias.setVisible(false);
            }
        });

        /* Para seleccionar únicamente un valor */
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String sugestionSeleccionada = (String) jList.getSelectedValue();
                if (sugestionSeleccionada!=null){
                    setText(sugestionSeleccionada);
                    jPopupSugerencias.setVisible(false);
                }
            }
        });

        jPopupSugerencias.removeAll();
        jPopupSugerencias.add(new JScrollPane(jList));
        jPopupSugerencias.revalidate();
        jPopupSugerencias.repaint();

        /* Especificar donde queremos que aparezca el JPopUp en este caso debajo del JTextField */
        jPopupSugerencias.show(this,0,getHeight());
        jPopupSugerencias.setVisible(true);

    }

    /* Esta clase esta constantemente atenta a os cambios producdos en el jTextField (cuando tecleas texto para añadir o quitar)
    asi que cada vez que el usuario añade texto esta clase llama a la Api y le pasa dicho texto y esta nos devolvera un json con posibles
    localizaciones asociada a la que es tecleada (estas funciones estaran dentro de la funcion getSugestions que se encargara de coger listado de
    posibles sugestiones) */

    private  class sugestionesListener implements DocumentListener{

        @Override
        public void insertUpdate(DocumentEvent e) {
            sugestions();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            sugestions();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            sugestions();
        }
        private  void sugestions(){
            String textoJField = getText();
            //Si el texto introducido es menor que 3 no pasamos a la api dicha sugestion
            if (textoJField.length()<3){
                jPopupSugerencias.setVisible(false);
                return;
            }

            /* Si hacemos getSuggestions en el hilo principal la interfaz de usuario se bloquearia hasta que terminase de ejecutarse el
            getSuggestions() , por lo que tendremos que usar una clase parecida a la de un thread */

            /* Usaremos una clase Swing Worker tiene funcionalidad parecida a la de un thread de ejecutar funciones de forma asincrona ,la primcipal ventaja es que teiens
             el codigo mejor estructurado con la funciones doInBackground y done que te permiten realizar
             tareas una vez finalizadas las anteriores, ademas que con el done() se ejecuta directamente el EDT( Event Dispatch Threa) que es el hilo
             principal que maneja la interfaz de swing permitiendolo hacerlo de forma mas segura y sencilla que en un thread */
            SwingWorker<List<String>,Void> worker = new SwingWorker<List<String>, Void>() {

                @Override
                protected List<String> doInBackground() throws Exception {
                    return getSugestions(textoJField);
                }
                @Override
                protected void done() {
                    super.done();
                    try {
                        List<String> listaSugerencias = get();
                        if (!listaSugerencias.isEmpty()) {
                            if (listaSugerencias.get(0).equals(getText())){
                                jPopupSugerencias.setVisible(false);
                            } else {
                                mostrarSugerencias(listaSugerencias);
                            }
                        } else {
                            jPopupSugerencias.setVisible(false);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            worker.execute();
        }
    }

    public HashMap<String, GeoPosition> getMap() {
        return dict;
    }

    public void setMap(HashMap<String, GeoPosition> map) {
        this.dict = map;
    }
}
