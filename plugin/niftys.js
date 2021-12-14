

chrome.storage.local.get("nftranks", ({ nftranks }) => {
	console.log('ranks ' + nftranks.length);
	setRankings();
});

function setRankings() {
	chrome.storage.local.get("nftranks", ({ nftranks }) => {
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
				if (ranking[0].rank < 50000) {
					color = "lightgreen";
				}
				if (names[i].innerHTML.indexOf('background-color') == -1){
					names[i].innerHTML += ' <p style="background-color: ' + color + '">' + ranking[0].rank + '/' + nftranks.length + '</p>';
				}
			} catch (error){
				console.log('couldnt do ' + names[i]);
			}
		}
		
		setTimeout(setRankings, 1000);
	});
}