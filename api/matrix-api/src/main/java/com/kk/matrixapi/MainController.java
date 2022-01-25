package com.kk.matrixapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.kk.matrixapi.model.json.Nft;
import com.kk.matrixapi.util.Util;

@CrossOrigin
@Controller
public class MainController {

	@Autowired
	NiftysAvatarClient webClient;

	@Autowired
	NiftysApiClient apiClient;
	
	private Nft getPilledNft(int tokenId) {
		Nft returnedNft = null;
		try {
			Nft redAvatarNft = webClient.getTraits(NiftysAvatarClient.RED_ID, tokenId);
			redAvatarNft.contractAddress = Util.RED_CONTRACT;
			returnedNft = redAvatarNft;
		} catch (Exception e) {/*ignore*/}
		if (returnedNft == null) {
			try {
				Nft blueAvatarNft = webClient.getTraits(NiftysAvatarClient.BLUE_ID, tokenId);
				blueAvatarNft.contractAddress = Util.BLUE_CONTRACT;
				returnedNft = blueAvatarNft;
			} catch (Exception e) {/*ignore*/}
		}
		return returnedNft;
	}
}
