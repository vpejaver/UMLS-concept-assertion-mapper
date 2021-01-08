DROP TABLE IF EXISTS `CHECKPOINTS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CHECKPOINTS` (
  `ID` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `NOTEEVENTS_ROW_ID` mediumint(8) unsigned NOT NULL,
  `NO_OF_SENTENCES` mediumint(8) unsigned NOT NULL,
  `START_TIME` bigint(16) unsigned,
  `END_TIME` bigint(16) unsigned,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
