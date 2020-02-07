package com.solace.se.samples.azservicebus;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SolaceAzServiceBusApp {

	//private static final Logger logger = LoggerFactory.getLogger(SolaceAzServiceBusApp.class);
	
	public static void main(String[] args) throws IOException {
		SpringApplication.run(SolaceAzServiceBusApp.class, args);
	}
	
}
