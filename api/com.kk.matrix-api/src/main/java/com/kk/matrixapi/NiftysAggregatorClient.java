package com.kk.matrixapi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kk.matrixapi.model.json.Nft;

@Component("NiftysAggregatorClient")
@FeignClient(value = "mainwebclient", url = "https://aggregator.api.niftys.com/v1/metadata/contracts/ckw5j7pqf0011g4731pvaj6gm")
public interface NiftysAggregatorClient {

    @RequestMapping(method = RequestMethod.GET, value = "/{tokenId}")
    public Nft getTraits(@PathVariable("tokenId") Integer tokenId);

}
