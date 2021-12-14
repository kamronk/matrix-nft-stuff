
var keepGoing = true;

chrome.storage.local.get("nftranks", ({ nftranks }) => {
	console.log('ranks ' + nftranks.length);
	setRankings();
	
	browser.runtime.connect().onDisconnect.addListener(function() {
		// clean up when content script gets disconnected
		keepGoing = false;
	});

});

function setRankings() {
	chrome.storage.local.get("nftranks", ({ nftranks }) => {
		if (document.getElementsByClassName('box-title').length){
			//details page
			if (document.getElementsByClassName('kk-rarity-info').length > 0){
				document.getElementsByClassName('kk-rarity-info')[0].remove();
			}
			
			var avatarTitle = document.getElementsByClassName('nft-header')[0].getElementsByClassName('title')[0].innerText;

			var thisId;
			if (avatarTitle.split('#').length > 1){
				thisId = parseInt(avatarTitle.split('#')[1].trim());
			} else {
				thisId = parseInt(avatarTitle.trim().split(' ')[2].trim());
			}
			
			var ranking = nftranks.filter(function (r) {
				  return r.tokenId == thisId;
				});
			var color = "yellow";
			if (ranking[0].rank <= 10000) {
				color = "lightgreen";
			}
			document.getElementsByClassName('nft-content')[0].getElementsByClassName('box')[0].insertAdjacentHTML('afterend',
				`<div class="box kk-rarity-info">
				   <div class="inner">
					  <div class="box-header">
						 <div class="box-title">Rarity</div>
					  </div>
					  <div class="markets">
						<ul class="items">
						   <li>
							  <div class="inner">
									<div style="background-color: ${color}; text-align: center;" class="avatar css-vurnku">
									<a target="_blank" href="https://matrixnftrarechecker.com/#${thisId}">
									   <div class="avatar-content css-vurnku">
										  <div id="mnrc-ranking" class="avatar-content">&nbsp;<strong>${numberWithCommas(ranking[0].rank)}</strong> &nbsp;&nbsp;/ ${numberWithCommas(nftranks.length)} MatrixNftRareChecker.com</div>
									   </div>
									</a>
								 </div>
								 <div class="actions"></div>
							  </div>
						   </li>
						   </li>
						</ul>
					  </div>
				   </div>
				</div>`
			);
		} else {
			//listings page
			var names = document.getElementsByClassName('nft-name');
			
			for(var i = 0; i < names.length; i++){
				try {
					var thisId;
					if (names[i].innerText.split('#').length > 1){
						thisId = parseInt(names[i].innerText.split('#')[1].trim());
					} else {
						thisId = parseInt(names[i].innerText.trim().split(' ')[2].trim());
					}
					
					var ranking = nftranks.filter(function (r) {
						  return r.tokenId == thisId;
						});
					var color = "yellow";
					if (ranking[0].rank <= 10000) {
						color = "lightgreen";
					}
					if (names[i].innerHTML.indexOf('background-color') == -1){
						names[i].innerHTML += ' <p style="background-color: ' + color + '; text-align: center;"><strong>' + numberWithCommas(ranking[0].rank) + '</strong> / ' + numberWithCommas(nftranks.length) + '</p>';
					}
				} catch (error){
					console.log('couldnt do ' + names[i]);
				}
			}
		}
		
		if (keepGoing){
			setTimeout(setRankings, 1000);
		}
	});
}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}