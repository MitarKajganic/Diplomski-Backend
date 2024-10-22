package com.mitar.dipl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class DiplomskiProjekatApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiplomskiProjekatApplication.class, args);
	}

}
