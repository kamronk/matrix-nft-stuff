package com.kk.matrixapi.model.json;

import java.util.Date;

public class CompletedOffer{
    public String id;
    public String price;
    public boolean isSeller;
    public String receiver;
    public Date updatedAt;
    public PaymentToken paymentToken;
    public String transactionHash;
    public Wallet wallet;
}