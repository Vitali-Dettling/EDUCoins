package org.educoins.central;

import org.educoins.central.repositories.NodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class})
@EnableScheduling
@SpringBootApplication
public class EducoinsCentralApplication {

    @Autowired
    private NodesRepository nodesRepository;

    public static void main(String[] args) {
        SpringApplication.run(EducoinsCentralApplication.class, args);
    }

    @PostConstruct
    private void init() {
//        nodesRepository.save(new Node("beefbeefbeefaffeaffeaffe", URI.create("http://localhost:8081"), PeerType.BLOCKCHAIN));
//        nodesRepository.save(new Node("beefbeefbeefaffeaffeaffe", URI.create("http://localhost:8082"), PeerType.REFERENCE));
//        nodesRepository.save(new Node("myPub5", URI.create("localhost:8680"), PeerType.MINER));
//        nodesRepository.save(new Node("myPub3", URI.create("localhost:8681"), PeerType.MINER));
//        nodesRepository.save(new Node("myPub4", URI.create("localhost:8682"), PeerType.REFERENCE));
//        nodesRepository.save(new Node("myPub6", URI.create("localhost:8682"), PeerType.REFERENCE));
//        nodesRepository.save(new Node("myPub7", URI.create("localhost:8682"), PeerType.REFERENCE));
//        nodesRepository.save(new Node("myPub8", URI.create("localhost:8682"), PeerType.REFERENCE));
//        nodesRepository.save(new Node("myPub9", URI.create("localhost:8682"), PeerType.REFERENCE));
//        nodesRepository.save(new Node("myPub10", URI.create("localhost:8682"), PeerType.REFERENCE));
//        nodesRepository.save(new Node("myPub11", URI.create("localhost:8682"), PeerType.REFERENCE));
//        nodesRepository.save(new Node("myPub12", URI.create("localhost:8682"), PeerType.REFERENCE));
//        nodesRepository.save(new Node("myPub13", URI.create("localhost:8682"), PeerType.REFERENCE));
//        nodesRepository.save(new Node("myPub14", URI.create("localhost:8682"), PeerType.REFERENCE));
//        nodesRepository.save(new Node("myPub15", URI.create("localhost:8682"), PeerType.REFERENCE));
//        nodesRepository.save(new Node("myPub16", URI.create("localhost:8682"), PeerType.BLOCKCHAIN));
    }
}