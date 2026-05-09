package org.appel.crypto_wallet_manager.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoinAsset> assets = new ArrayList<>();

    public Wallet() {
    }

    public Wallet(List<CoinAsset> assets) {
        setAssets(assets);
    }

    public Long getId() {
        return id;
    }

    public List<CoinAsset> getAssets() {
        return assets;
    }

    public void setAssets(List<CoinAsset> assets) {
        this.assets.clear();
        for (CoinAsset asset : assets) {
            addAsset(asset);
        }
    }

    public void addAsset(CoinAsset asset) {
        assets.add(asset);
        asset.setWallet(this);
    }

    public void removeAsset(CoinAsset asset) {
        assets.remove(asset);
        asset.setWallet(null);
    }
}
