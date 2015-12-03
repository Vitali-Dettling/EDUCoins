package org.educoins.core.utils;


import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URI;

/**
 * A small Rest-client serving default methods.
 * Created by typus on 11/3/15.
 */
public class RestClient<T> {

    private final String APPLICATION_JSON = "application/json";
    private final HttpClient httpClient;
    private final Gson gson;

    public RestClient() {
        httpClient = HttpClientBuilder.create().build();
        gson = new Gson();
    }

    public T get(URI uri, Class clazzOfT) throws IOException {
        HttpGet getRequest = new HttpGet(uri);
        getRequest.addHeader("accept", APPLICATION_JSON);
        HttpResponse response;

        response = httpClient.execute(getRequest);

        int status = response.getStatusLine().getStatusCode();
        if (status != 200) {
            throw new HttpException(String.format("GET-Request to %s failed. Retrieved Exitcode %s",
                    uri.toString(), status), status);
        }

        return extractBody(clazzOfT, response);
    }


    public void put(URI uri, T body) throws IOException {
        HttpPut putRequest = new HttpPut(uri);
        putRequest.addHeader("accept", APPLICATION_JSON);
        putRequest.addHeader("content-type", APPLICATION_JSON);

        putRequest.setEntity(new StringEntity(gson.toJson(body)));

        HttpResponse response = httpClient.execute(putRequest);

        int status = response.getStatusLine().getStatusCode();
        if (status != 200 && status != 201 && status != 204) {
            throw new HttpException(String.format("PUT-Request to %s failed. Retrieved Exitcode %s",
                    uri.toString(), status), status);
        }
    }

    public <U> U put(URI uri, T body, Class<? extends U> responseClass) throws IOException {
        HttpPut putRequest = new HttpPut(uri);
        putRequest.addHeader("accept", APPLICATION_JSON);
        putRequest.addHeader("content-type", APPLICATION_JSON);

        putRequest.setEntity(new StringEntity(gson.toJson(body)));

        HttpResponse response = httpClient.execute(putRequest);

        int status = response.getStatusLine().getStatusCode();
        if (status != 200 && status != 201 && status != 204) {
            throw new HttpException(String.format("PUT-Request to %s failed. Retrieved Exitcode %s",
                    uri.toString(), status), status);
        }
        return extractBody(responseClass, response);
    }


    public void post(URI uri, T body) throws IOException {
        HttpPost postRequest = new HttpPost(uri);
        postRequest.addHeader("accept", APPLICATION_JSON);
        postRequest.addHeader("content-type", APPLICATION_JSON);

        postRequest.setEntity(new StringEntity(gson.toJson(body)));

        HttpResponse response = httpClient.execute(postRequest);

        int status = response.getStatusLine().getStatusCode();
        if (status != 200
                && status != 201
                && status != 204) {
            throw new HttpException(String.format("POST-Request to %s failed. Retrieved Exitcode %s",
                    uri.toString(), status), status);
        }
    }


    public <U> U post(URI uri, T body, Class<? extends U> responseClass) throws IOException {
        HttpPost postRequest = new HttpPost(uri);
        postRequest.addHeader("accept", APPLICATION_JSON);
        postRequest.addHeader("content-type", APPLICATION_JSON);

        postRequest.setEntity(new StringEntity(gson.toJson(body)));

        HttpResponse response = httpClient.execute(postRequest);

        int status = response.getStatusLine().getStatusCode();
        if (status != 200
                && status != 201
                && status != 204) {
            throw new HttpException(String.format("POST-Request to %s failed. Retrieved Exitcode %s",
                    uri.toString(), status), status);
        }
        return extractBody(responseClass, response);
    }

    public void delete(URI uri) throws IOException {
        HttpDelete deleteRequest = new HttpDelete(uri);
        deleteRequest.addHeader("accept", APPLICATION_JSON);
        HttpResponse response;

        response = httpClient.execute(deleteRequest);

        int status = response.getStatusLine().getStatusCode();
        if (status != 200) {
            throw new HttpException(String.format("DELETE-Request to %s failed. Retrieved Exitcode %s",
                    uri.toString(), status), status);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T extractBody(Class clazzOfT, HttpResponse response) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        StringBuilder output = new StringBuilder();
        String outStr;
        // Simply iterate through XML response and show on console.
        while ((outStr = br.readLine()) != null) {
            output.append(outStr);
        }

        return (T) new Gson().fromJson(output.toString(), clazzOfT);
    }

}
