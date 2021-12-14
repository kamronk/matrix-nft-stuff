
var keepGoing = true;

var limits = null;

chrome.storage.sync.get([
 'rankToShowGreen',
 'rareityToShowGreen'
], function(limits) {
	limits = limits;
	setRankings(limits);
});

function setRankings(limits) {
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
			if (ranking[0].rank <= limits.rankToShowGreen) {
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
						
					var rankColor = "yellow";
					if (ranking[0].rank <= limits.rankToShowGreen) {
						rankColor = "lightgreen";
					}
					var rareAttrColor = "yellow";
					if (ranking[0].rarestAttributeRatio <= limits.rareityToShowGreen) {
						rareAttrColor = "lightgreen";
					}
					
					var ratioPercentage = ranking[0].rarestAttributeRatio * 100;
					
					if (names[i].innerHTML.indexOf('background-color') > -1){
						names[i].getElementsByClassName('kk-listing-ranking')[0].remove();
						names[i].getElementsByClassName('kk-listing-rarity')[0].remove();
					}
					names[i].innerHTML += ` 
						<p class="kk-listing-ranking" style="background-color: ${rankColor}; text-align: center;">
							<strong>${numberWithCommas(ranking[0].rank)}</strong> / ${numberWithCommas(nftranks.length)}
						</p>
						<p class="kk-listing-rarity" title="${ratioPercentage.toFixed(2)}% of avatars have this trait" style="background-color: ${rareAttrColor}; text-align: center;">
							Rarest Attribute: <strong>${ranking[0].rarestAttributeValue}</strong>
						</p>
					
					`;
				} catch (error){
					console.log('couldnt do ' + names[i]);
				}
			}
		}
		
		if (keepGoing){
			setTimeout(function() {
				setRankings(limits);
			}, 1000);
		}
	});
}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}