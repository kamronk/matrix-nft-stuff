package com.kk.matrixapi.model.json.offers;

import java.util.Date;

import com.kk.matrixapi.model.json.PaymentToken;

public class OffersComposite{
    public String id;
    public String signer;
    public String receiver;
    public String nonce;
    public String chainId;
    public String price;
    public String quantity;
    public int expirationDate;
    public String contractInterface;
    public String signature;
    public boolean isSeller;
    public String tokenId;
    public String contractAddress;
    public String status;
    public String paymentTokenId;
    public String paymentTokenChainId;
    public String transactionHash;
    public Date createdAt;
    public Date updatedAt;
    public PaymentToken paymentToken;
    public Wallet wallet;
}
