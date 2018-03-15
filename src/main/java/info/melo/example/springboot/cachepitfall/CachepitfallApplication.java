package info.melo.example.springboot.cachepitfall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CachepitfallApplication {

	public static void main(String[] args) {
		SpringApplication.run(CachepitfallApplication.class, args);
	}
}
