package org.appel.crypto_wallet_manager.repository;

import org.appel.crypto_wallet_manager.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
