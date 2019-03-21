package com.github.phantauth.resource.producer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ExternalCache<T> {

    private final LoadingCache<String,T> cache;
    private final Function<InputStream, T> reader;

    public ExternalCache(final int size, final long ttl, final Function<InputStream, T> reader) {
        this.reader = reader;
        cache = CacheBuilder.newBuilder()
                .maximumSize(size)
                .expireAfterWrite(ttl, TimeUnit.MILLISECONDS)
                .build(new Loader());
    }

    public T get(final String key) throws ExecutionException {
        return cache.get(key);
    }

    private class Loader extends CacheLoader<String, T> {
        final CloseableHttpClient client;

        private Loader() {
            this.client = newClient();
        }

        private CloseableHttpClient newClient() {
            if ( client != null ) {
                return client;
            }

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(3000)
                    .setSocketTimeout(2000)
                    .build();
            return HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .setUserAgent("PhantAuth")
                    .build();
        }

        @Override
        public T load(final String uri) throws Exception {

            try (CloseableHttpResponse response = client.execute(new HttpGet(uri))) {
                // on not supported entity type (ie: fleet, or team), in this case next generator will get chance ????
                if ( response.getStatusLine().getStatusCode() == 404 ) {
                    return null;
                }
                return reader.apply(response.getEntity().getContent());
            }
        }
    }

}
