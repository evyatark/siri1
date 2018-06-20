package org.hasadna.bus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class BusApplication {

	/**
	 * This thread-pool is used for threads that do the processing!
	 * (not the retrieval)
	 * @return
	 */
	@Bean(name = "process-response")
	public Executor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor x = new ThreadPoolTaskExecutor();
		x.setCorePoolSize(20);
		x.setMaxPoolSize(50);
		return x;
		//return new ThreadPoolTaskExecutor();
	}

	@Bean(name = "http-retrieve")	// scheduler Threads
	public Executor mythreadPoolTaskExecutor() {
		ThreadPoolTaskExecutor x = new ThreadPoolTaskExecutor();
		x.setCorePoolSize(5);
		x.setMaxPoolSize(6);
		return x;
		//return new ThreadPoolTaskExecutor();
	}

	public static void main(String[] args) {
		SpringApplication.run(BusApplication.class, args);
	}
}
