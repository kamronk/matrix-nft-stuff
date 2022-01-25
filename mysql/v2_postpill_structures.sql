CREATE TABLE `attribute` (
  `attributeId` int(11) NOT NULL AUTO_INCREMENT,
  `attributeTypeId` int(11) NOT NULL,
  `attributeValueId` int(11) NOT NULL,
  `nftId` int(11) NOT NULL,
  PRIMARY KEY (`attributeId`),
  KEY `FK_18` (`attributeTypeId`),
  KEY `FK_21` (`nftId`),
  KEY `attribute_attributeValue_fk` (`attributeValueId`),
  CONSTRAINT `FK_16` FOREIGN KEY (`attributeTypeId`) REFERENCES `attributeType` (`attributeTypeId`),
  CONSTRAINT `FK_19` FOREIGN KEY (`nftId`) REFERENCES `nft` (`nftId`),
  CONSTRAINT `attribute_attributeValue_fk` FOREIGN KEY (`attributeValueId`) REFERENCES `attributeValue` (`attributeValueId`)
) ENGINE=InnoDB AUTO_INCREMENT=2822411 DEFAULT CHARSET=utf8;

CREATE TABLE `attributeCounts` (
  `value` mediumtext,
  `attributeTypeId` int(11) NOT NULL DEFAULT '0',
  `name` varchar(500) CHARACTER SET latin1 NOT NULL,
  `numOfThis` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `attributeType` (
  `attributeTypeId` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) NOT NULL,
  `contractAddress` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`attributeTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=latin1;

CREATE TABLE `attributeValue` (
  `attributeValueId` int(11) NOT NULL AUTO_INCREMENT,
  `attributeTypeId` int(11) NOT NULL,
  `value` varchar(500) NOT NULL,
  `scarcityRatio` double DEFAULT NULL,
  PRIMARY KEY (`attributeValueId`),
  KEY `attributeValue_attributeTypeId_key` (`attributeTypeId`),
  CONSTRAINT `attributeValue_attributeTypeId_con` FOREIGN KEY (`attributeTypeId`) REFERENCES `attributeType` (`attributeTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=1583 DEFAULT CHARSET=utf8;

CREATE TABLE `attributeValueTest` (
  `attributeValueId` int(11) NOT NULL DEFAULT '0',
  `attributeTypeId` int(11) NOT NULL,
  `value` varchar(500) NOT NULL,
  `scarcityRatio` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `aviTwinNftIds` (
  `nftId` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `aviPilledTwins` (
  `nftId` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `aviCount` (
  `count` int(11) DEFAULT NULL,
  `aviCount` bigint(21) NOT NULL DEFAULT '0',
  `valueIds` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `aviAttributes` (
  `valueIds` varchar(1000) DEFAULT NULL,
  `nftId` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `fakeIds` (
  `fakeId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `fakeIdsTemp` (
  `fakeId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `nft` (
  `nftId` int(11) NOT NULL AUTO_INCREMENT,
  `tokenId` varchar(45) NOT NULL,
  `likes` int(11) NOT NULL,
  `contractAddress` varchar(45) NOT NULL,
  `tokenIdInt` int(11) DEFAULT NULL,
  `image` varchar(500) DEFAULT NULL,
  `redImage` varchar(500) DEFAULT NULL,
  `blueImage` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`nftId`)
) ENGINE=InnoDB AUTO_INCREMENT=146796 DEFAULT CHARSET=latin1;

CREATE TABLE `provenance` (
  `provenanceId` int(11) NOT NULL AUTO_INCREMENT,
  `nftId` int(11) NOT NULL,
  `fromAddress` varchar(500) NOT NULL,
  `toAddress` varchar(500) NOT NULL,
  `quantity` varchar(500) NOT NULL,
  `blockTime` int(11) NOT NULL,
  `txHash` varchar(500) NOT NULL,
  PRIMARY KEY (`provenanceId`),
  KEY `provenanceNftIdKey` (`nftId`),
  CONSTRAINT `provenanceNftIdConstraint` FOREIGN KEY (`nftId`) REFERENCES `nft` (`nftId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `sale` (
  `saleId` int(11) NOT NULL AUTO_INCREMENT,
  `txId` varchar(500) NOT NULL,
  `nftId` int(11) NOT NULL,
  `amount` double NOT NULL,
  PRIMARY KEY (`saleId`),
  KEY `FK_29` (`nftId`),
  CONSTRAINT `FK_27` FOREIGN KEY (`nftId`) REFERENCES `nft` (`nftId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `scarcity` (
  `scarcityId` int(11) NOT NULL AUTO_INCREMENT,
  `attributeTypeId` int(11) DEFAULT NULL,
  `attributeValue` varchar(500) DEFAULT NULL,
  `attributeTypeName` varchar(500) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `ratio` double DEFAULT NULL,
  PRIMARY KEY (`scarcityId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tempNftRarity` (
  `minRatio` double DEFAULT NULL,
  `attributeName` varchar(500) DEFAULT NULL,
  `nftId` int(11) DEFAULT NULL,
  `ratioSum` double DEFAULT NULL,
  `avgRatio` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tempScarcityBase` (
  `attCount` bigint(21) NOT NULL DEFAULT '0',
  `scarcityRatio` decimal(24,4) DEFAULT NULL,
  `attributeValueId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tempScarcityBlue` (
  `attCount` bigint(21) NOT NULL DEFAULT '0',
  `scarcityRatio` decimal(24,4) DEFAULT NULL,
  `attributeValueId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tempScarcityRed` (
  `attCount` bigint(21) NOT NULL DEFAULT '0',
  `scarcityRatio` decimal(24,4) DEFAULT NULL,
  `attributeValueId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
