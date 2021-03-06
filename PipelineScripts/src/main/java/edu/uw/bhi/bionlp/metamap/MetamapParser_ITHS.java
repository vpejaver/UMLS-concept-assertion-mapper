package edu.uw.bhi.bionlp.metamap;

import bioc.BioCDocument;
import edu.uw.bhi.bionlp.data.RadiologyToken;
import edu.uw.bhi.bionlp.data.UMLSConcept;
//import edu.uw.bhi.bionlp.pipeline.FeatureExtractor;
import edu.uw.bhi.bionlp.pipeline.Tokenizer;
import gov.nih.nlm.nls.metamap.*;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.prologbeans.PrologSession;

/**
 * Author: melihay Date: 7/7/15 Version: 1.0
 */
public class MetamapParser_ITHS implements IMetamapParser {

    MetaMapApi api = null;

    public MetamapParser_ITHS() {
        this.api = new MetaMapApiImpl("127.0.0.1");

//        new MetaMapApiTest("127.0.0.1",1795).process("she has HTN.",System.out,null);
    }

    public static void main(String[] args) {
        MetamapParser_ITHS m = new MetamapParser_ITHS();
        try {
            List<UMLSConcept> concepts = m.parseSentenceWithMetamap("She has high blood pressure and diabetes.");
//            List<UMLSConcept> concepts = m.parseSentenceWithMetamap("she has HTN.");
            for (UMLSConcept concept : concepts) {
                System.out.println(concept.toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(MetamapParser_ITHS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    static String[] semanticTypes = {"acab", "anab", "comd", "cgab", "dsyn", "emod", "fndg", "inpo", "mobd", "neop", "patf", "sosy"};
    static Set<String> semanticTypesToBeIncluded = new HashSet(Arrays.asList(semanticTypes));

    public void parseWithMetamap(String sectionText, int sectionBeginIndex) {

        List<Result> resultList = this.api.processCitationsFromString(sectionText);

        int phraseId = 1;

        for (Result result : resultList) {
            try {
                for (Utterance utterance : result.getUtteranceList()) {

                    for (PCM pcm : utterance.getPCMList()) {

                        String phraseText = pcm.getPhrase().getPhraseText();
                        int beginIndex = sectionBeginIndex + pcm.getPhrase().getPosition().getX();
                        int endIndex = beginIndex + pcm.getPhrase().getPosition().getY();

                        //db.insertIntoTable("MetamapPhrases",reportName, sectionId, phraseId, phraseText, beginIndex, endIndex);
                        //System.out.println(phraseText);
                        List<String> cuiList = new ArrayList<String>();

                        for (Mapping map : pcm.getMappingList()) {

                            for (Ev mapEv : map.getEvList()) {

                                String semanticTypes = "";
                                if (mapEv.getSemanticTypes().size() == 1) {
                                    semanticTypes = mapEv.getSemanticTypes().get(0);
                                } else {

                                    for (String semType : mapEv.getSemanticTypes()) {
                                        semanticTypes += semType + "|";
                                    }
                                }

                                if (!cuiList.contains(mapEv.getConceptId())) //System.out.println(mapEv.getConceptName());
                                //db.insertIntoTable("MetamapConcepts",reportName, sectionId, phraseId, mapEv.getConceptId(), mapEv.getConceptName(), semanticTypes, Math.abs(mapEv.getScore()));
                                {
                                    cuiList.add(mapEv.getConceptId());
                                }
                            }
                        }
                        phraseId++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
 
    public List<UMLSConcept> parseSentenceWithMetamap(String sentenceText) throws IOException {

        List<UMLSConcept> concepts = new ArrayList<UMLSConcept>();
        List<RadiologyToken> sentenceTokens = Tokenizer.getTokensSevereTokenization(sentenceText);
        //for (RadiologyToken token : sentenceTokens) {
        //    System.out.println("Sentence"+token.toString());
        // }
        RadiologyToken emptyToken = new RadiologyToken();
        emptyToken.setToken("null");

        List<Result> resultList = this.api.processCitationsFromString(sentenceText);

        int phraseId = 1;

        int beginTokenIndex = -1;
        int endTokenIndex = 0;

        for (Result result : resultList) {
            try {
                for (Utterance utterance : result.getUtteranceList()) {

                    for (PCM pcm : utterance.getPCMList()) {

                        String phraseText = pcm.getPhrase().getPhraseText();
                        List<RadiologyToken> phraseTokens = Tokenizer.getTokensSevereTokenization(phraseText);
                        if (phraseTokens.isEmpty()) {
                            continue;
                        }

                        beginTokenIndex = -1;

                        // finds the begin token index of the phrase
                        for (int k = endTokenIndex; k < sentenceTokens.size(); k++) {
                            RadiologyToken sentenceToken = sentenceTokens.get(k);
                            if (sentenceToken.getToken().equals(phraseTokens.get(0).getToken())) {

                                beginTokenIndex = sentenceTokens.indexOf(sentenceToken) ;
                                endTokenIndex = beginTokenIndex + phraseTokens.size() - 1;

                                //for (int i = beginTokenIndex; i<= endTokenIndex; i++) {
                                //    sentenceTokens.set(i,emptyToken);
                                //}
                                break;
                            }
                        }

                        if (beginTokenIndex == -1) {
                            break;
                        }

                        int beginIndex = pcm.getPhrase().getPosition().getX();
                        int endIndex = beginIndex + pcm.getPhrase().getPosition().getY();

                        //db.insertIntoTable("MetamapPhrases",reportName, sectionId, phraseId, phraseText, beginIndex, endIndex);
                        //System.out.println(phraseText);
                        List<String> cuiList = new ArrayList<String>();

                        for (Mapping map : pcm.getMappingList()) {

                            for (Ev mapEv : map.getEvList()) {

                                // filter based on semantic types
                                boolean flag = true; //CHANGED BY VIKAS TO INCLUDE ALL SEMANTIC TYPES

                                for (String semType : mapEv.getSemanticTypes()) {
                                    flag = semanticTypesToBeIncluded.contains(semType);
				    flag = true; // CHANGED BY VIKAS TO INCLUDE ALL SEMANTIC TYPES
                                    if (flag) {
                                        break;
                                    }
                                    //for (String s : semanticTypesToBeIncluded) {
                                    //    if (s.equals(semType)) {
                                    //        flag = true;
                                    //    }
				    //}
                                }

                                if (flag == false) {
                                    continue;
                                }

                                String semanticTypes = "";
                                if (mapEv.getSemanticTypes().size() == 1) {
                                    semanticTypes = mapEv.getSemanticTypes().get(0);

                                } else {

                                    for (String semType : mapEv.getSemanticTypes()) {
                                        semanticTypes += semType + "|";
                                    }
                                }

                                if (!cuiList.contains(mapEv.getConceptId()) && flag == true) {
                                    UMLSConcept concept = new UMLSConcept();
                                    concept.setCUI(mapEv.getConceptId());
                                    concept.setConceptName(mapEv.getConceptName());
                                    concept.setPhrase(phraseText);
                                    concept.setBeginTokenIndex(beginTokenIndex);
                                    concept.setEndTokenIndex(endTokenIndex);
                                    concept.setSemanticTypeLabels(semanticTypes);
                                    concepts.add(concept);
                                }

                                //db.insertIntoTable("MetamapConcepts",reportName, sectionId, phraseId, mapEv.getConceptId(), mapEv.getConceptName(), semanticTypes, Math.abs(mapEv.getScore()));
                                cuiList.add(mapEv.getConceptId());
                            }
                        }
                        phraseId++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return concepts;
    }

}
