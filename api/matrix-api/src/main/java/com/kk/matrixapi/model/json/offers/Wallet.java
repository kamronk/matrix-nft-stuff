package com.kk.matrixapi.model.json.offers;

import java.util.Date;

import com.kk.matrixapi.model.json.Account;

public class Wallet{
    public String address;
    public String provider;
    public String network;
    public String chainId;
    public String walletType;
    public String accountId;
    public boolean isActive;
    public boolean isDefault;
    public boolean isClaimed;
    public Date lastToppedUpAt;
    public boolean toppedUpForMatrix;
    public Date createdAt;
    public Date updatedAt;
    public Account account;
}