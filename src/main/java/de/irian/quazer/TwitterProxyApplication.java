package de.irian.quazer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

@SpringBootApplication
public class TwitterProxyApplication {
	
	@Bean
	public Twitter twitter() {
		// Thread-safe, so we can use it a singleton bean
		return TwitterFactory.getSingleton();
	}

	public static void main(String[] args) {
		SpringApplication.run(TwitterProxyApplication.class, args);
	}
}
