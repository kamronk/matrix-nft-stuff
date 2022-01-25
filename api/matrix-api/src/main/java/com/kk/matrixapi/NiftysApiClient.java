package com.kk.matrixapi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kk.matrixapi.model.json.Nft;

@Component("NiftysApiClient")
@FeignClient(value = "NiftysApiClient", url = "https://niftys.com/api/nfts")
public interface NiftysApiClient {

    @RequestMapping(method = RequestMethod.GET, value = "/{contractAddress}/{tokenId}")
    public Nft getDetails(@PathVariable("contractAddress") String contractAddress, @PathVariable("tokenId") Integer tokenId);

}
