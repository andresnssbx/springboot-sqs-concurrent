package com.andresnss.sqsconcurrent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SqsconcurrentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SqsconcurrentApplication.class, args);
	}

}

//10.000 messages (20 threads)
//Inicio: 2024-06-20T00:41:30.735-04:00
//Termino: 2024-06-20T00:42:20.174-04:00  50 sec

//10.000 messages (10 threads)
//Inicio: 	2024-06-20T00:09:07.662-04:00
//Termino: 	2024-06-20T00:10:49.650-04:00	1:40 min

//10.000 messages (1 thread)
//Inicio: 	2024-06-20T00:13:32.679-04:00
//Termino: 	2024-06-20T00:30:12.672-04:00   17 min

