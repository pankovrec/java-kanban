package manager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVClient {
    static String apiKey;
    private HttpClient client = HttpClient.newHttpClient();
    private String url;

    public KVClient(String url) {
        this.url = url;
        apiKey = getKey();
    }

    public void setApiKey(String apiKey) {
        KVClient.apiKey = apiKey;
    }

    private String getKey() {
        String responseString = "";
        URI register = URI.create("http://localhost:8078/" + "register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(register)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            responseString = response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка запроса по адресу: '" + register);
        }
        return responseString;
    }

    public void put(String key, String json) {
        URI save = URI.create("http://localhost:8078/" + "save/" + key + "/?API_KEY=" + apiKey);
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(save)
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка сохранения: " + save);
        }
    }

    public String load(String key) {
        String responseString = "";
        URI load = URI.create("http://localhost:8080/" + "load/" + key + "/?API_KEY=" + apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(load)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            responseString = response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка загрузки: " + load);
        }
        return responseString;
    }
}