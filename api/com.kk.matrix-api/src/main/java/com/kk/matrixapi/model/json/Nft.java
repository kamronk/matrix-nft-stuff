package com.kk.matrixapi.model.json;

import java.util.List;

public class Nft{
    public int owners;
    public List<Provenance> provenance;
    public int likes;
    public List<Offer> offers;
    public List<CompletedOffer> completedOffers;
    public String contractAddress;
    public String tokenId;
    public String name;
    public String image;
    public Media media;
    public List<NifCreator> creators;
    public Contract contract;
    public List<Attribute> attributes;
    public Object description;
    public SupportedNetwork supportedNetwork;
}
