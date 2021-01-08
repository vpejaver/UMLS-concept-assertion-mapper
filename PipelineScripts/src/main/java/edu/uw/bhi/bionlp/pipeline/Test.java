


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.bhi.bionlp.pipeline;

import edu.uw.bhi.bionlp.data.ClinicalNote;
import edu.uw.bhi.bionlp.data.UMLSConcept;
import edu.uw.bhi.bionlp.data.OutputConcept;
import edu.uw.bhi.bionlp.data.HashPointer;
import edu.uw.bhi.bionlp.db.DataAccess;
import edu.uw.bhi.bionlp.metamap.IMetamapParser;
import edu.uw.bhi.bionlp.metamap.MetamapLiteParser;
import edu.uw.bhi.bionlp.metamap.MetamapParser_ITHS;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;
//import edu.uw.bhi.bionlp.metamap;
//import edu.uw.bhi.bionlp.Assertion_Classifier.src.name.adibejan.util.IntPair;
//import edu.uw.bhi.bionlp.Assertion_Classifier.src.name.adibejan.util.UnsupportedDataFormatException;
import name.adibejan.util.IntPair;
import name.adibejan.util.UnsupportedDataFormatException;

/**
 *
 * @author wlau
 * @author vpejaver
 */
public class Test {

    public static void processNotes(long start, long end, IMetamapParser m, DataAccess da_MIMIC, DataAccess da_MIMIC_NLP, String dbTable, boolean verbose) throws Exception {
	List<ClinicalNote> notes = da_MIMIC.getNoteData("select ROW_ID, NOTE_TEXT from " + dbTable  + " where ROW_ID between " + start + " and " + end + ";");
	
        for (int nID = 0; nID < notes.size(); nID++) {
            String note = notes.get(nID).getText();
            long noteID = notes.get(nID).getID();
            long startTime = System.currentTimeMillis();
            List<String> sentences = SentenceChunker.getSentences(note);
            for (int sID = 0; sID < sentences.size(); sID++) {
                String sentence = sentences.get(sID);
		long cStart = System.currentTimeMillis(); // ADDED BY VIKAS
		String gID = UUID.nameUUIDFromBytes(sentence.getBytes()).toString(); // ADDED BY VIKAS
		long cEnd = System.currentTimeMillis(); // ADDED BY VIKAS

		long pStart = System.currentTimeMillis(); // ADDED BY VIKAS
		List<HashPointer> processed = da_MIMIC_NLP.matchGUIDFast(gID);
		
                if (processed.isEmpty()) { // ADDED BY VIKAS
		    List<UMLSConcept> concepts = m.parseSentenceWithMetamap(sentence); 
		    if (verbose) {
			System.out.println("***************");
		    }
		    int k = 0;
		    for (UMLSConcept concept : concepts) {
			String prediction = "-";
			try {
			    if (verbose) {
				System.out.print("sentence(" + sentence + ") phrase(" + concept.getPhrase() + ") indices(" + concept.getBeginTokenIndex() + ":" + concept.getEndTokenIndex() + ") concept(" + concept.getConceptName());
			    }
			    if (concept.getBeginTokenIndex() == -1 || concept.getEndTokenIndex() == -1) {
				concept.setBeginTokenIndex(-1);
				concept.setEndTokenIndex(-1);
				prediction = "indeterminate";
			    } else {
				prediction = AssertionClassifier.predict(sentence, new IntPair(concept.getBeginTokenIndex(), concept.getEndTokenIndex()));
			    }
			    if (verbose) {
				System.out.println(") prediction(" + prediction + ")");
			    }
			    k += 1;
			    da_MIMIC_NLP.insertSentenceConcept(noteID, sID, gID, concept.getCUI(), concept.getPhrase(), concept.getBeginTokenIndex(),
				concept.getEndTokenIndex(), concept.getConceptName(), concept.getSemanticTypeLabels(), prediction); // MODIFIED BY VIKAS
			    
			} 
			catch (UnsupportedDataFormatException ex) {
			    //System.err.println("UnsupportedDataFormatException: " + ex.getMessage());
			} catch (NumberFormatException nfe){ 
			    //System.err.println("NumberFormatException: " + nfe.getMessage());
			} catch (Exception all) {
			    ////all.printStackTrace();
			    
			    //System.err.println("Exception: " + all.getMessage());
			    //ignore any exception from Assertion Classification and move on
			}
		    }
		    //System.out.println("@\t" + noteID + "\t" + concepts.size());
		    long pEnd = System.currentTimeMillis(); // ADDED BY VIKAS
		    da_MIMIC_NLP.insertSentence(noteID, sID, gID, sentence, cStart, cEnd, pStart, pEnd, 0); // MOVED AND MODIFIED BY VIKAS
		} else { // ADDED BY VIKAS                                                                                                                                                            
                    if (verbose) {
                        System.out.println("***************");
                    }

		    HashPointer tuple = processed.get(0);
		    //for (HashPointer tuple : processed) {
			try {
			    if (verbose) {
				System.out.println("noteID("+tuple.getRowID()+") sentenceID("+tuple.getSentenceID()+")");
			    }

			    da_MIMIC_NLP.insertPointer(noteID, sID, tuple.getRowID(), tuple.getSentenceID());
			}
			catch (UnsupportedDataFormatException ex) {

                        } catch (NumberFormatException nfe){

                        }
			//}
		    long pEnd = System.currentTimeMillis(); // ADDED BY VIKAS
		    da_MIMIC_NLP.insertSentence(noteID, sID, gID, sentence, cStart, cEnd, pStart, pEnd, 1); // MOVED AND MODIFIED BY VIKAS
		}
	    }
            long endTime = System.currentTimeMillis();
            da_MIMIC_NLP.checkpointing(noteID, sentences.size(), startTime, endTime);
        }
    }
  
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String dbServer = (args.length > 0) ? args[0] : "127.0.0.1";
        String nlpDB = (args.length > 1) ? args[1] : "mimic3_test"; //"UW_Note";
	String dbTable = (args.length > 2) ? args[2] : "NOTEEVENTS"; //"CLINICAL_NOTE";
        final boolean verbose = (args.length > 3) ? args[3].equalsIgnoreCase("1") : false;
	String heapSize = (args.length > 4) ? args[4] : "4g";
        int startNoteID = (args.length > 5) ? Integer.parseInt(args[5]) : -1;
        int endNoteID = (args.length > 6) ? Integer.parseInt(args[6]) : -1;
	String username = (args.length > 7) ? args[7] : "";
	String password = (args.length > 8) ? args[8] : "";
	final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	final Date date = new Date();		
	final DataAccess da_MIMIC = new DataAccess();
	final DataAccess da_MIMIC_NLP = new DataAccess();
	
        try {
	    if (password.equals("")) {
		da_MIMIC.SetupConnection(dbServer, nlpDB, username); // Corrected by Vikas    
		da_MIMIC_NLP.SetupConnection(dbServer, nlpDB, username);
	    } else {
		da_MIMIC.SetupConnection(dbServer, nlpDB, username, password); // Corrected by Vikas    
		da_MIMIC_NLP.SetupConnection(dbServer, nlpDB, username, password);
	    }

	    IMetamapParser m = new MetamapLiteParser();
	    
	    processNotes(startNoteID, endNoteID, m, da_MIMIC, da_MIMIC_NLP, dbTable, verbose);
        } catch (Exception ex) {
            Logger.getLogger("PipelineScripts").log(Level.SEVERE, null, ex);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime / 1000);
    }
}
