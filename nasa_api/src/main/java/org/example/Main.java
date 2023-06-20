package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URL;

public class Main {
    private static final String NASA_URL = "https://api.nasa.gov/planetary/apod?api_key=rwCqv1pF6v6mpM8PRXOUMo3x2egXjLe66WZ7UYmw";

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        HttpGet request = new HttpGet(NASA_URL);
        CloseableHttpResponse response = httpClient.execute(request);
        NasaResponse nasaResponse = new ObjectMapper().readValue(response.getEntity().getContent(), new TypeReference<>() {
        });
        URL downloadContentUrl = new URL(nasaResponse.getUrl());
        byte[] buffer = getContent(httpClient, downloadContentUrl);
        String fileName = FilenameUtils.getName(downloadContentUrl.getPath());
        writeContentToFile(buffer, fileName);
        response.close();
        httpClient.close();
    }

    private static byte[] getContent(CloseableHttpClient httpClient, URL downloadContentUrl) throws IOException {
        HttpGet getContentRequest = new HttpGet(downloadContentUrl.toString());
        CloseableHttpResponse contentResponse = httpClient.execute(getContentRequest);
        byte[] buffer = contentResponse.getEntity().getContent().readAllBytes();
        contentResponse.close();
        return buffer;
    }

    private static void writeContentToFile(byte[] buffer, String fileName) throws IOException {
        File targetFile = new File("nasa_api/content/" + fileName);
        OutputStream outputStream = new FileOutputStream(targetFile);
        outputStream.write(buffer);
        outputStream.close();
    }

    private static CloseableHttpClient getHttpClient() {
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(getCustomRequestConfig())
                .build();
    }

    private static RequestConfig getCustomRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(10000)
                .setSocketTimeout(30000)
                .setRedirectsEnabled(false)
                .build();
    }
}