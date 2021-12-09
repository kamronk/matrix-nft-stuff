create database matrix_nfts;

use matrix_nfts;

CREATE TABLE `attributeType`
(
 `attributeTypeId` int NOT NULL AUTO_INCREMENT ,
 `name`            varchar(500) NOT NULL ,

PRIMARY KEY (`attributeTypeId`)
);

CREATE TABLE `nft`
(
 `nftId`           int NOT NULL AUTO_INCREMENT ,
 `tokenId`         varchar(45) NOT NULL ,
 `likes`           int NOT NULL ,
 `contractAddress` varchar(45) NOT NULL ,

PRIMARY KEY (`nftId`)
);

CREATE TABLE `attribute`
(
 `attributeId`     int NOT NULL AUTO_INCREMENT ,
 `value`           varchar(500) NOT NULL ,
 `nftId`           int NOT NULL ,
 `attributeTypeId` int NOT NULL ,

PRIMARY KEY (`attributeId`),
KEY `FK_18` (`attributeTypeId`),
CONSTRAINT `FK_16` FOREIGN KEY `FK_18` (`attributeTypeId`) REFERENCES `attributeType` (`attributeTypeId`),
KEY `FK_21` (`nftId`),
CONSTRAINT `FK_19` FOREIGN KEY `FK_21` (`nftId`) REFERENCES `nft` (`nftId`)
);

CREATE TABLE `sale`
(
 `saleId` int NOT NULL AUTO_INCREMENT ,
 `txId`   varchar(500) NOT NULL ,
 `nftId`  int NOT NULL ,
 `amount` double NOT NULL ,

PRIMARY KEY (`saleId`),
KEY `FK_29` (`nftId`),
CONSTRAINT `FK_27` FOREIGN KEY `FK_29` (`nftId`) REFERENCES `nft` (`nftId`)
);
