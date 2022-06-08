package ru.tigran.gatewayproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayProxyService {
	public static void main(String[] args) {
		SpringApplication.run(GatewayProxyService.class, args);
	}

	@Bean
	public RestTemplateBuilder restTemplateBuilder(){
		return new RestTemplateBuilder();
	}
}
