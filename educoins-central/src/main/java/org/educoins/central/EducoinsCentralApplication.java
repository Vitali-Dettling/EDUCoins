package org.educoins.central;

import org.educoins.central.domain.Node;
import org.educoins.central.repositories.NodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class EducoinsCentralApplication {

    @Autowired
    private NodesRepository nodesRepository;

    public static void main(String[] args) {
        SpringApplication.run(EducoinsCentralApplication.class, args);


    }

    @PostConstruct
    private void init() {
        try {
            nodesRepository.save(new Node(0, InetAddress.getByName("localhost"), "myPub1"));
            nodesRepository.save(new Node(1, InetAddress.getByName("localhost"), "myPub2"));
            nodesRepository.save(new Node(2, InetAddress.getByName("localhost"), "myPub3"));
            nodesRepository.save(new Node(3, InetAddress.getByName("localhost"), "myPub4"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}