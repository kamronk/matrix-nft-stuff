let seeOptions = document.getElementById("seeOptions");

seeOptions.addEventListener("click", async () => {
	chrome.runtime.openOptionsPage((lastError) => {
	});
});
