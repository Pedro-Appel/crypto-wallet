package org.appel.crypto_wallet_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableFeignClients(basePackages = "org.appel.crypto_wallet_manager.client")
@EnableJpaRepositories(basePackages = "org.appel.crypto_wallet_manager.repository")
public class CryptoWalletManagerApplication {

	static void main(String[] args) {
		SpringApplication.run(CryptoWalletManagerApplication.class, args);
	}

}
