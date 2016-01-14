package org.educoins.core.config;

import com.google.gson.Gson;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Replaces Jackson with Gson.
 * Created by typus on 11/30/15.
 */
@Configuration
@ConditionalOnClass(Gson.class)
@ConditionalOnBean(Gson.class)
public class GsonHttpMessageConverterConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public GsonHttpMessageConverter gsonHttpMessageConverter(Gson gson) {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(gson);
        return converter;
    }

    @Bean
    public HttpMessageConverters customConverters() {
        Collection<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        messageConverters.add(gsonHttpMessageConverter);

        return new HttpMessageConverters(true, messageConverters);
    }

}