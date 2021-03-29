package com.selflearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ViKumar
 * 
 * Launcher class 
 * 
 * change server.port in application.properties for desired port
 *
 */
@SpringBootApplication
public class GraphqlPubSubLauncher {

	public static void main(String[] args) {
		SpringApplication.run(GraphqlPubSubLauncher.class, args);
	}
}
