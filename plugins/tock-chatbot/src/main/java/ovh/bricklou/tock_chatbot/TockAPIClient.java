package ovh.bricklou.tock_chatbot;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TockAPIClient {
    private final String tockApiUrl;
    private final String tockApiToken;
    private final HttpClient httpClient;

    public TockAPIClient(String tockApiUrl, String tockApiToken) {
        this.tockApiUrl = tockApiUrl;
        this.tockApiToken = tockApiToken;

        this.httpClient = HttpClient.newHttpClient();
    }

    public String queryApi(String query) throws IOException, InterruptedException {
        // format the body string to match the following object: { "query": "query" }
        String queryJson = String.format("{ \"query\": \"%s\" }", query);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(queryJson))
                .uri(URI.create(this.tockApiUrl + "?q=" + query))
                .setHeader("User-Agent", "SLBot - Tock API Plugin")
                .setHeader("Content-Type", "application/json")
                .build();


        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            return null;
        }
        return response.body();
    }
}
