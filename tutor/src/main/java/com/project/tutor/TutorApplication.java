package com.project.tutor;

import lombok.extern.flogger.Flogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@Flogger
public class TutorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TutorApplication.class, args);
	}

}
