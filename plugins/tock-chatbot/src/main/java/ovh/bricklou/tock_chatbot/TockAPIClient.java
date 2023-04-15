package ovh.bricklou.tock_chatbot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TockAPIClient {
    private final String tockApiUrl;
    private final HttpClient httpClient;

    public TockAPIClient(String tockApiUrl) {
        this.tockApiUrl = tockApiUrl;

        this.httpClient = HttpClient.newHttpClient();
    }

    public String queryApi(String query) throws IOException, InterruptedException {
        // format the body string to match the following object: { "query": "query" }
        Gson gson = new Gson();
        String bodyJson = gson.toJson(Map.of("query", query, "userId", "0"));


        URI uri = URI.create(this.tockApiUrl + "?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                .uri(uri)
                .setHeader("User-Agent", "SLBot - Tock API Plugin")
                .setHeader("Content-Type", "application/json")
                .build();


        HttpResponse<String> httpResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200) {
            throw new IOException("Tock API returned a non-200 status code: " + httpResponse.statusCode());
        }

        JsonElement json = JsonParser.parseString(httpResponse.body());

        var responses = json.getAsJsonObject().get("responses").getAsJsonArray();
        if (responses.size() == 0) {
            return null;
        }

        var response = responses.get(0).getAsJsonObject().get("text").getAsString();

        if (response.equals("dont_understand")) {
            return null;
        }

        return response;
    }
}
