package ru.yandex.practicum.tasktracker.web.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String apiToken;
    private URI serverUri;
    private final HttpClient httpClient;

    public KVTaskClient(String url) {
        httpClient = HttpClient.newHttpClient();
        serverUri = URI.create(url);
        register();
    }

    public void put(String key, String json) {
        try {
            URI putUri = URI.create(serverUri + "/save/" + key + "?API_TOKEN=" + apiToken);
            final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(body)
                    .uri(putUri)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = httpClient.send(request, handler);

            int status = response.statusCode();

            if (status >= 200 && status <= 299) {
                System.out.println("Ключ и значение сохранены");
            } else {
                System.out.println("Что-то пошло не так. Код ответа: " + status);
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. "
                    + "Проверьте, пожалуйста, URL-адрес и повторите попытку.");
        } catch (IllegalArgumentException e) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        }
    }

    public String load(String key) {
        try {
            URI loadUri = URI.create(serverUri + "/load/" + key + "?API_TOKEN=" + apiToken);

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(loadUri)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = httpClient.send(request, handler);

            int status = response.statusCode();

            if (status >= 200 && status <= 299) {
                System.out.println("Успешно");
            } else {
                System.out.println("Что-то пошло не так. Код ответа: " + status);
                return "";
            }

            return response.body();

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. "
                    + "Проверьте, пожалуйста, URL-адрес и повторите попытку.");
        } catch (IllegalArgumentException e) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        }

        return "";
    }

    private void register() {
        try {
            URI registerUri = URI.create(serverUri + "/register");

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(registerUri)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = httpClient.send(request, handler);

            int status = response.statusCode();

            if (status >= 200 && status <= 299) {
                apiToken = response.body();
                System.out.println("Клиент зарегистрирован. apiToken: " + apiToken);
            } else {
                System.out.println("Что-то пошло не так. Код ответа: " + status);
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. "
                    + "Проверьте, пожалуйста, URL-адрес и повторите попытку.");
        } catch (IllegalArgumentException e) {
            System.out.println("Введённый вами адрес не соответствует формату URL. Попробуйте, пожалуйста, снова.");
        }
    }
}
