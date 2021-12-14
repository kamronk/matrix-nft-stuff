
chrome.storage.sync.get("rankToShowGreen", ({ rankToShowGreen }) => {
  document.getElementById('rankShowGreen').value = rankToShowGreen;
});

chrome.storage.sync.get("rareityToShowGreen", ({ rareityToShowGreen }) => {
  document.getElementById('rarityShowGreen').value = rareityToShowGreen;
});


document.getElementById('saveButton').addEventListener('click',
    saveShowGreens);
	
function saveShowGreens(){
	var rareityToShowGreen = parseFloat(document.getElementById('rarityShowGreen').value);
	var rankToShowGreen = parseInt(document.getElementById('rankShowGreen').value);
	if (isNaN(rareityToShowGreen) || isNaN(rankToShowGreen)){
		alert('Invalid values');
	} else {
		chrome.storage.sync.set({ rankToShowGreen, rareityToShowGreen });
		alert('Saved');
	}
}
