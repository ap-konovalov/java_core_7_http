package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.List;

// https://github.com/netology-code/jd-homeworks/blob/master/http/task1/README.md
public class Main {

    private static final String CAT_FACTS_URL = "https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats";

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        HttpGet request = new HttpGet(CAT_FACTS_URL);
        CloseableHttpResponse response = httpClient.execute(request);
        ObjectMapper mapper = new ObjectMapper();
        List<CatFact> catFacts = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
        });
        catFacts.stream().filter(fact -> fact.getUpvotes() > 0)
                .forEach(System.out::println);
        response.close();
        httpClient.close();
    }

    private static CloseableHttpClient getHttpClient() {
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(getCustomRequestConfig())
                .build();
    }

    private static RequestConfig getCustomRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(30000)
                .setRedirectsEnabled(false)
                .build();
    }
}