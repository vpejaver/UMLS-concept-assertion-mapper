DROP TABLE IF EXISTS `SENTENCE_HASH_MATCH_POINTER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SENTENCE_HASH_MATCH_POINTER` (
  `ID` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `NOTEEVENTS_ROW_ID` mediumint(8) unsigned NOT NULL,
  `NOTEEVENTS_SENTENCE_ID` mediumint(8) unsigned NOT NULL,
  `MATCHED_ROW_ID` mediumint(8) unsigned NOT NULL,
  `MATCHED_SENTENCE_ID` mediumint(8) unsigned NOT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
