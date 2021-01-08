/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.bhi.bionlp.metamap;

import bioc.BioCDocument;
import edu.uw.bhi.bionlp.data.RadiologyToken;
import edu.uw.bhi.bionlp.data.UMLSConcept;
import edu.uw.bhi.bionlp.db.DataAccess;
import static edu.uw.bhi.bionlp.metamap.MetamapParser_ITHS.semanticTypesToBeIncluded;
import edu.uw.bhi.bionlp.pipeline.Tokenizer;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;
import gov.nih.nlm.nls.metamap.document.FreeText;
import gov.nih.nlm.nls.metamap.lite.types.ConceptInfo;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import gov.nih.nlm.nls.ner.MetaMapLite;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author wlau
 */
public class MetamapLiteParser implements IMetamapParser {

    Properties myProperties;
    MetaMapLite metaMapLiteInst;

    public MetamapLiteParser() {
        try {
            myProperties = new Properties();
            MetaMapLite.expandModelsDir(myProperties,
                    "public_mm_lite/data/models");
            MetaMapLite.expandIndexDir(myProperties,
                    "public_mm_lite/data/ivf/strict");
            myProperties.setProperty("metamaplite.excluded.termsfile",
                    "public_mm_lite/data/specialterms.txt");
            //Creating a metamap lite instance:

            metaMapLiteInst = new MetaMapLite(myProperties);

        } catch (Exception ex) {
            Logger.getLogger(MetamapLiteParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Entity> parseSentenceWithMetamap(BioCDocument document) throws Exception {
        return metaMapLiteInst.processDocument(document);
    }

    public List<UMLSConcept> parseSentenceWithMetamap(String sentenceText) throws Exception {
        BioCDocument document = FreeText.instantiateBioCDocument(sentenceText);
        document.setID("1");

        List<UMLSConcept> concepts = new ArrayList<UMLSConcept>();
//        List<RadiologyToken> sentenceTokens = Tokenizer.getTokensSevereTokenization(sentenceText);
	//String pretokenized = sentenceText.trim().replaceAll("\\s+", " ").replace("-", " "); // ADDED BY VIKAS AFTER NIC-WILSON FEEDBACK
        String pretokenized = sentenceText.trim().replaceAll("\\s+", " ");
        String[] sentenceTokens = pretokenized.split("\\s+");
        
        int beginTokenIndex = -1;
        int endTokenIndex = 0;
        int nextTokenIndex = 0;

        List<Entity> resultList = metaMapLiteInst.processDocument(document);
	//System.out.println("@\t\t" + resultList.size());
        for (Entity entity : resultList) {
            String phraseText = entity.getMatchedText();
//            List<RadiologyToken> phraseSevereTokens = Tokenizer.getTokensSevereTokenization(phraseText);
            
            String[] phraseTokens = phraseText.split("\\s+");
//            if (phraseSevereTokens.isEmpty()) {
//                continue;
//            } 

	    //System.out.print("aaaa\t" + phraseText + "\t");
            beginTokenIndex = -1;
            // finds the begin token index of the phrase
	    for (int k = nextTokenIndex; k < sentenceTokens.length; k++) {
                boolean match = true;
                for (int j = 0; match && j<phraseTokens.length && j+k<sentenceTokens.length; j++)
                {
                    String sentenceToken = StringUtils.strip(sentenceTokens[j+k]," [!\"#$%&'()*+,-./:;<=>?\\\\@\\[\\]^_`{|}~0-9]");
		    String phraseToken = StringUtils.strip(phraseTokens[j]," [!\"#$%&'()*+,-./:;<=>?\\\\@\\[\\]^_`{|}~0-9]"); // ADDED BY VIKAS AFTER NIC-WILSON MEETING
//                            sentenceTokens[j+k].replaceAll("[!\"#$%&'()*+,-./:;<=>?\\\\@\\[\\]^_`{|}~0-9]", " ").trim();

                    match = sentenceToken.equalsIgnoreCase(phraseTokens[j]); // MODIFIED BY VIKAS AFTER NIC-WILSON MEETING
		    /*if (match == false) {
			System.out.println(sentenceToken + "\t" + phraseTokens[j]);
			}*/
                }
//                        (sentenceToken.getToken().equals(phraseTokens.get(0).getToken())) {
                if(match){
                    beginTokenIndex = k; //sentenceTokens.indexOf(sentenceToken);
                    endTokenIndex = beginTokenIndex + phraseTokens.length - 1;
                    //nextTokenIndex=endTokenIndex+1;
		    nextTokenIndex = beginTokenIndex; // MODIFIED BY VIKAS AFTER NIC-WILSON MEETING
		    break;
                }
            }
	    
            /*if (beginTokenIndex == -1) {
		System.out.println("bbbb\t-");
                break;
            }
	    System.out.println("bbbb\t" + phraseText);*/
            
            List<String> cuiList = new ArrayList<String>();

            for (Ev ev : entity.getEvSet()) {

                // filter based on semantic types
                boolean flag = true;
                ConceptInfo conceptInfo = ev.getConceptInfo();
                List<String> SemanticTypeList = new ArrayList(conceptInfo.getSemanticTypeSet());
                for (String semType : SemanticTypeList) {
                    flag = semanticTypesToBeIncluded.contains(semType); 
		    //flag = true; // CHANGED BY VIKAS TO INCLUDE ALL SEMANTIC TYPES
                    if (flag) {
                        break;
                    }
                }

                if (flag == false) {
                    continue;
                }

                String semanticTypes = "";
                if (conceptInfo.getSemanticTypeSet().size() == 1) {
                    semanticTypes = SemanticTypeList.get(0);
                } else {
                    for (String semType : SemanticTypeList) {
                        semanticTypes += semType + "|";
                    }
                }

                if (!cuiList.contains(conceptInfo.getCUI()) && flag == true) {
                    UMLSConcept concept = new UMLSConcept();
                    concept.setCUI(conceptInfo.getCUI());
                    concept.setConceptName(conceptInfo.getPreferredName());
                    concept.setPhrase(phraseText);
                    concept.setBeginTokenIndex(beginTokenIndex);
                    concept.setEndTokenIndex(endTokenIndex);
//                    concept.setBeginTokenIndex(ev.getStart());
//                    concept.setEndTokenIndex(ev.getStart()+ev.getLength()-1);

                    concept.setSemanticTypeLabels(semanticTypes);
                    concepts.add(concept);
                }

                //db.insertIntoTable("MetamapConcepts",reportName, sectionId, phraseId, mapEv.getConceptId(), mapEv.getConceptName(), semanticTypes, Math.abs(mapEv.getScore()));
                cuiList.add(conceptInfo.getCUI());
            }
        }

//                System.out.print(ev.getConceptInfo().getCUI() + "|" + entity.getMatchedText());
//                System.out.println();	
        return concepts;
    }

    public static void main(String[] args) {
        boolean testITHS = true;
        MetamapLiteParser m = new MetamapLiteParser();
	String dbTable = args[0];
        try {
            if (testITHS) {
                DataAccess da = new DataAccess();
                da.SetupConnection();
                //List<String> notes = da.getStringData("select text from NOTEEVENTS limit 1;");
		//List<String> notes = da.getStringData("select NOTE_TEXT from CLINICAL_NOTE limit 1;");
                List<String> notes = da.getStringData("select text from " + dbTable + " limit 1;");
//            notes.stream().forEach(n-> {   System.out.println(n); });

                for (String note : notes) {
                    List<UMLSConcept> concepts = m.parseSentenceWithMetamap(note);
                    System.out.println("***************");
                    for (UMLSConcept concept : concepts) {
                        System.out.println(concept.toString());
                    }
                }
            } else {
                BioCDocument document = FreeText.instantiateBioCDocument("only limited history");
                document.setID("1");
                List<BioCDocument> documentList = new ArrayList<BioCDocument>();
                documentList.add(document);
                List<Entity> entityList = m.parseSentenceWithMetamap(document);
                for (Entity entity : entityList) {
                    for (Ev ev : entity.getEvSet()) {
                        for (String semType : ev.getConceptInfo().getSemanticTypeSet()) {
                            System.out.print(ev.getConceptInfo().getCUI() + "|" + entity.getMatchedText() + "|" + semType);
                        }

                        System.out.println();
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(MetamapLiteParser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
