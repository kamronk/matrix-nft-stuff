$(document).ready(function() {
	console.log('yo');
	
	console.log(ranks);

});

function showRarity(){
	var enteredValues = $('#tokenIds').val();
	var validEntry = true;
	var errorMsg = '';
	if (enteredValues.indexOf(',') == 0){
		enteredValues += ',';
	}
	var entries = [];
	for(var i = 0; i < enteredValues.split(',').length; i++){
		var singleValue = enteredValues.split(',')[i];
		if (singleValue.trim() != '' && isNaN(singleValue.trim())){
			validEntry = false;
			errorMsg = enteredValues.split(',')[i] + ' is not a number';
		} else 
			entries.push(enteredValues.split(',')[i].trim());
	}
	if (validEntry){
		window.location = '#' + entries.join(',');
	} else {
		toastr.error(errorMsg, 'Error');
	}
}

function findAndShow(ids){
	$('#rarityResults').empty();
	var usersRankings = [];
	for(var i = 0; i < ids.split(',').length; i++){
		var id = ids.split(',')[i];
		var ranking = ranks.filter(function (r) {
		  return r.tokenId == id;
		});
		if (ranking){
			usersRankings.push(ranking);
		} else {
			usersRankings.push({rank: 100001, tokenId: id + ' invalid'});
		}
	}
	usersRankings.sort(function(x, y) {
	  if (x[0].rank < y[0].rank) {
		return -1;
	  }
	  if (x[0].rank > y[0].rank) {
		return 1;
	  }
	  return 0;
	});
	for(var i = 0; i < usersRankings.length; i++){
		var ranking = usersRankings[i][0];
		$('#rarityResults').append(`
			<tr>
				<td>${ranking.tokenId}</td>
				<td><strong>${ranking.rarestAttributeValue}</td>
				<td><strong>${ranking.rarestAttributeRatio}</td>
				<td><strong>${ranking.rank}</strong> / ${ranks.length} &nbsp <sup>Score: ${parseFloat("" + ranking.avgRatio).toFixed(5)}</sup></td>
				<td><a class="button small" href="${ranking.image}" target="_blank">Download</a></td>
				<td><a href="https://niftys.com/nft/0x39ceaa47306381b6d79ad46af0f36bc5332386f2/${ranking.tokenId}" target="_blank">Avatar # ${ranking.tokenId}</a></td>
			</tr>
		`);
	}
}


