package org.educoins.central;

import org.educoins.central.domain.Node;
import org.educoins.central.domain.PeerType;
import org.educoins.central.repositories.NodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.net.URI;

@SpringBootApplication
public class EducoinsCentralApplication {

    @Autowired
    private NodesRepository nodesRepository;

    public static void main(String[] args) {
        SpringApplication.run(EducoinsCentralApplication.class, args);


    }

    @PostConstruct
    private void init() {
        nodesRepository.save(new Node("myPub1", URI.create("localhost:8678"), PeerType.BLOCKCHAIN));
        nodesRepository.save(new Node("myPub2", URI.create("localhost:8679"), PeerType.BLOCKCHAIN));
        nodesRepository.save(new Node("myPub5", URI.create("localhost:8680"), PeerType.MINER));
        nodesRepository.save(new Node("myPub3", URI.create("localhost:8681"), PeerType.MINER));
        nodesRepository.save(new Node("myPub4", URI.create("localhost:8682"), PeerType.REFERENCE));
    }
}