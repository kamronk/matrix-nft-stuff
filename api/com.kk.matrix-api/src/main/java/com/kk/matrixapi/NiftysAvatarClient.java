package com.kk.matrixapi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kk.matrixapi.model.json.Nft;

@Component("NiftysBaseAvatarClient")
@FeignClient(value = "mainwebclient", url = "https://aggregator.api.niftys.com/v1/metadata/contracts/")
public interface NiftysAvatarClient {

	public static String BASE_ID = "ckw5j7pqf0011g4731pvaj6gm";
	public static String RED_ID = "matrix-red-contract";
	public static String BLUE_ID = "matrix-blue-contract";

    @RequestMapping(method = RequestMethod.GET, value = "/{contractIdentifier}/{tokenId}")
    public Nft getTraits(@PathVariable("contractIdentifier") String contractIdentifier, @PathVariable("tokenId") Integer tokenId);

}
