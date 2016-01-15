package org.educoins.core.config;

import org.educoins.core.utils.CustomGsonSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Registers Custom Serializer for the Rest Controllers
 */
@Configuration
@EnableWebMvc
public class GsonHttpMessageConverterConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(CustomGsonSerializer.getGson());
        converters.add(converter);
    }
}
