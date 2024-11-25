package main.java.com.example.mapa;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ValidatorLocalizacion {
    private final String url ="https://api.geoapify.com/v1/geocode/search?text=%s&apiKey=%s";
    private final String apiKey = "1e47cf29776640d39b07c06e693a0346";

    public ValidatorLocalizacion(){

    }
    public GeoPosition validateAdress(String adress) throws IOException {

        String adressEncoded = URLEncoder.encode(adress, StandardCharsets.UTF_8.toString());

        String encodedAdress = adressEncoded.replace(" ", "%20");
        String formatoUrl = String.format(url,encodedAdress,apiKey);

        System.out.println(formatoUrl);
        URL validateUrl = new URL(formatoUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) validateUrl.openConnection();
        httpURLConnection.setRequestMethod("GET");


        /* Leer la respuesta y ponerla como un StringBuilder */
        BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        StringBuilder respuesta = new StringBuilder();
        String lineaRespuesta;

        /* Leer lineas de respuesta y añadirlas al StringBuilder */
        while ((lineaRespuesta= in.readLine())!= null){
            respuesta.append(lineaRespuesta);
        }
        in.close();
        JsonObject jsonObject = JsonParser.parseString(respuesta.toString()).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("features");

        /* Comprobar si no tiene valores en el json array es porque el codigo postal introducido es incorrecto */
        if (jsonArray == null ||jsonArray.size()==0){
            return null;
        }
        if (jsonArray.size()==1){
            System.out.println(jsonArray.toString());
        }

        JsonObject features = jsonArray.get(2).getAsJsonObject();
        JsonObject geometry = features.getAsJsonObject("geometry");
        JsonArray coordinates = geometry.getAsJsonArray("coordinates");
        GeoPosition geoPosition = new GeoPosition(coordinates.get(0).getAsDouble(),coordinates.get(1).getAsDouble());
        return geoPosition;
    }



}
