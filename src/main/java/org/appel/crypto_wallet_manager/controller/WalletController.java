package org.appel.crypto_wallet_manager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WalletController {

    @GetMapping("/wallet")
    public String getWallets() throws InterruptedException {
        Thread.sleep(1000);
        return "List of wallets";
    }
}
