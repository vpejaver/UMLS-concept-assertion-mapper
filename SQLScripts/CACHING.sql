DROP TABLE IF EXISTS `NOTEEVENTS_SENTENCES`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NOTEEVENTS_SENTENCES` (
  `ID` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `NOTEEVENTS_ROW_ID` mediumint(8) unsigned NOT NULL,
  `SENTENCE_ID` mediumint(8) unsigned NOT NULL,
  `GUID` char(36) NOT NULL,
  `SENTENCE_TEXT` mediumtext CHARACTER SET utf8,
  `CREATE_START` bigint(16) unsigned,
  `CREATE_END` bigint(16) unsigned,
  `PROCESSED_START` bigint(16) unsigned,
  `PROCESSED_END` bigint(16) unsigned,
  `PRECOMPUTED_START` bigint(16) unsigned,
  `PRECOMPUTED_END` bigint(16) unsigned,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
CREATE INDEX `IDX_NOTEEVENTS_SENTENCES__GUID__NOTEEVENTS_ROW_ID__SENTENCE_ID` ON `NOTEEVENTS_SENTENCES` (
  `GUID` ASC,
  `NOTEEVENTS_ROW_ID` ASC,
  `SENTENCE_ID` ASC
);

DROP TABLE IF EXISTS `SENTENCE_CONCEPTS`;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SENTENCE_CONCEPTS` (
  `ID` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `NOTEEVENTS_ROW_ID` mediumint(8) unsigned NOT NULL,
  `SENTENCE_ID` mediumint(8) unsigned NOT NULL,
  `GUID` char(36) NOT NULL,
  `CONCEPT_CUI` varchar(10) CHARACTER SET utf8,
  `PHASE` mediumtext CHARACTER SET utf8,
  `BEGIN_TOKEN_INDEX` mediumint(9) signed,
  `END_TOKEN_INDEX` mediumint(9) signed,
  `CONCEPT_STR` tinytext CHARACTER SET utf8,
  `SEMANTIC_TYPE` tinytext CHARACTER SET utf8,
  `PREDICTION` tinytext CHARACTER SET utf8,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
