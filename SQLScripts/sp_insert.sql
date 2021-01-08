DELIMITER //
DROP PROCEDURE IF EXISTS sp_insertSentenceConcept//

CREATE PROCEDURE sp_insertSentenceConcept(IN noteevents_row_id MEDIUMINT(8),
                              IN sentence_id MEDIUMINT(8),
			      IN guid CHAR(36),
			      IN concept_cui VARCHAR(10),
			      IN phase MEDIUMTEXT,
			      IN begin_token_index MEDIUMINT(8),
			      IN end_token_index MEDIUMINT(8),
			      IN concept_str TINYTEXT,
			      IN semantic_type TINYTEXT,
			      IN prediction TINYTEXT)
BEGIN
	INSERT INTO SENTENCE_CONCEPTS(NOTEEVENTS_ROW_ID, SENTENCE_ID, GUID, CONCEPT_CUI, PHASE, BEGIN_TOKEN_INDEX, END_TOKEN_INDEX, CONCEPT_STR, SEMANTIC_TYPE, PREDICTION)	
	       VALUES (noteevents_row_id, sentence_id, guid, concept_cui, phase, begin_token_index, end_token_index, concept_str, semantic_type, prediction);
END//

DELIMITER ;
