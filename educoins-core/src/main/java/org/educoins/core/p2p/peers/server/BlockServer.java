package org.educoins.core.p2p.peers.server;

import org.educoins.core.Block;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * The HttpServer serving {@link Block}s.
 * Manages all ingoing requests to the REST-API.
 * Created by typus on 11/5/15.
 */
@SpringBootApplication
@EnableWebMvc
@Configuration
@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class})
public class BlockServer {
    public static final String contentType = "application/json";
    public static final String BLOCKS_RESOURCE_PATH = "/blocks/";
    public static final String BLOCK_HEADERS_RESOURCE_PATH = "/blocks/headers/";

    public BlockServer() {
    }

    public static void main(String[] args) {
        SpringApplication.run(BlockServer.class, args);
    }
}
