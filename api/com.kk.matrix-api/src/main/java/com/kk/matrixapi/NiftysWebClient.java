package com.kk.matrixapi;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kk.matrixapi.model.json.offers.OffersComposite;

//https://niftys.com/api/offers/nft/0x39ceaa47306381b6d79ad46af0f36bc5332386f2/73702?status=CREATED

@Component("NiftysWebClient")
@FeignClient(value = "niftyswebclient", url = "https://niftys.com/api/offers/nft/0x39ceaa47306381b6d79ad46af0f36bc5332386f2/")
public interface NiftysWebClient {

    @RequestMapping(method = RequestMethod.GET, value = "/{tokenId}?status=CREATED")
    public List<OffersComposite> getOffers(@PathVariable("tokenId") Integer tokenId);
    
}
