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
            System.out.println("Conectando a: " + url);
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Respuesta HTTP: " + response.statusCode());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RuntimeException("Error HTTP: " + response.statusCode());
            }
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar a la API. Verifique su conexión a Internet.", e);
        } catch (InterruptedException e) {
            System.err.println("Conexión interrumpida: " + e.getMessage());
            throw new RuntimeException("La conexión fue interrumpida.", e);
        }
    }
}
