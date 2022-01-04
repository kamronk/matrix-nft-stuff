# matrix-nft-stuff

[https://matrixnftrares.com/](https://matrixnftrares.com/), [https://matrixnftchecker.com](https://matrixnftchecker.com), [https://matrixnftrarechecker.com/](https://matrixnftrarechecker.com/)

Matrix NFT Data and a browsing tool.

You can check the rarity of your attributes and get UHD images of your avatars by using the tool here: [https://matrixnftrarechecker.com/](https://matrixnftrarechecker.com/)

The JSON files here are the complete library of matrix avatars with more data than I've compiled.

Check out my niftys profile here: [https://niftys.com/kamronk](https://niftys.com/kamronk)

## How is the rarity calculated?

<ol>
  <li>Get the total number of other avatars with the same attribute value, for each attribute</li>
  <li>Divide each total by the count of all avatars minted to produce a scarcity ratio</li>
  <li>Sum up all of the scarcity ratios</li>
  <li>Divide by the number of attributes</li>
</ol>

I then just exported it all into a list with the corresponding token ID, and stored it in JSON inside of an HTML file and some other fancy web stuff to make an easy to use rarity checker.

You can see the exact code being used for compilation in the api directory. Specific scheduled method is here: [https://github.com/kamronk/matrix-nft-stuff/blob/main/api/com.kk.matrix-api/src/main/java/com/kk/matrixapi/SpringConfig.java#L37](https://github.com/kamronk/matrix-nft-stuff/blob/main/api/com.kk.matrix-api/src/main/java/com/kk/matrixapi/SpringConfig.java#L37)