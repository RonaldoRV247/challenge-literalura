package com.example.literalura_challenge.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ConsumoAPI {

    public String obtenerDatos(String url) {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("User-Agent", "LiteraluraApp/1.0")
                .build();

        HttpResponse<String> response = null;
        try {
            // Eliminar mensajes técnicos para usuarios comunes
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RuntimeException("Error en la conexion con el servidor de libros");
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo conectar al servidor de libros. Verifique su conexion a Internet.", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("La busqueda fue interrumpida.", e);
        }
    }
}
