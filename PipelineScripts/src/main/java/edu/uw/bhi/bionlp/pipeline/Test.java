


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
        //List<ClinicalNote> notes = da_MIMIC.getNoteData("select ROW_ID, text from NOTEEVENTS where ROW_ID between " + start + " and " + end + ";");
	//List<ClinicalNote> notes = da_MIMIC.getNoteData("select ROW_ID, NOTE_TEXT from CLINICAL_NOTE where ROW_ID between " + start + " and " + end + ";");
	
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
                //da_MIMIC_NLP.insertSentence(noteID, sID, sentence);

		long pStart = System.currentTimeMillis(); // ADDED BY VIKAS
		//List<OutputConcept> processed = da_MIMIC_NLP.matchGUID(gID);
		List<HashPointer> processed = da_MIMIC_NLP.matchGUIDFast(gID);
		//List<HashPointer> processed = new ArrayList<HashPointer>();
		
                if (processed.isEmpty()) { // ADDED BY VIKAS
		    List<UMLSConcept> concepts = m.parseSentenceWithMetamap(sentence); 
		    if (verbose) {
			System.out.println("***************");
		    }
		    int k = 0;
		    //System.out.println("@\t" + noteID + "\t" + concepts.size());		    
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

    // multi processing
    /*public static void processNotesMP(IMetamapParser m, DataAccess da_MIMIC, DataAccess da_MIMIC_NLP, String dbServer, String dbName, String dbTable,  boolean verbose, int nProc, String heapSize, String username, String password) throws Exception {
        /*long total = da_MIMIC.getLongData("select count(*) from NOTEEVENTS;");
        long minID = da_MIMIC.getLongData("select min(ROW_ID) from NOTEEVENTS;");
        long maxID = da_MIMIC.getLongData("select max(ROW_ID) from NOTEEVENTS;");
	long total = da_MIMIC.getLongData("select count(*) from CLINICAL_NOTE;");
        long minID = da_MIMIC.getLongData("select min(ROW_ID) from CLINICAL_NOTE;");
        long maxID = da_MIMIC.getLongData("select max(ROW_ID) from CLINICAL_NOTE;");
	long total = da_MIMIC.getLongData("select count(*) from " + dbTable + ";");
        long minID = da_MIMIC.getLongData("select min(ROW_ID) from " + dbTable + ";");
        long maxID = da_MIMIC.getLongData("select max(ROW_ID) from " + dbTable + ";");
	
        int chunkSize = (int) Math.floorDiv(total, nProc-1); // CHANGED BY VIKAS TO COMMANDLINE PARAMETER
        long numOfChunks = Math.floorDiv(total, chunkSize);
        long remainder = Math.floorMod(total, chunkSize);
        String classPath = System.getProperty("java.class.path");
	List<Process> processes = new ArrayList<Process>(nProc);
	
        for (long cidx = minID; cidx < numOfChunks * chunkSize; cidx += chunkSize) {
            final long startIdx = cidx;
            final long endIdx = cidx + chunkSize - 1;
	    List<String> command = Arrays.asList(new String[]{"java", "-Xmx" + heapSize, "-cp", classPath, "edu.uw.bhi.bionlp.pipeline.Test", dbServer, dbName, dbTable, "mml", "0", verbose ? "1" : "0", "1", heapSize, "" + startIdx, "" + endIdx, username, password});
            System.out.println("processing notes from " + startIdx + " - " + endIdx + " ..." + command);
            ProcessBuilder builder = new ProcessBuilder(command).inheritIO();
            processes.add(builder.start());
        }
        if (remainder > 0) {
            List<String> command = Arrays.asList(new String[]{"java", "-Xmx" + heapSize, "-cp", classPath, "edu.uw.bhi.bionlp.pipeline.Test", dbServer, dbName, dbTable, "mml", "0", verbose ? "1" : "0", "1", heapSize, "" + (numOfChunks * chunkSize), "" + maxID, username, password});
            System.out.println("processing notes from " + numOfChunks * chunkSize + " - " + maxID + " ..." + command);
            ProcessBuilder builder = new ProcessBuilder(command).inheritIO();
	    Process process = builder.start();
	    processes.add(builder.start());
        }

	// Wait for all processes to complete (ADDED BY VIKAS)
	int children = nProc;
	while (children != 0) {
	    children = 0;
	    for (Iterator<Process> iter = processes.iterator(); iter.hasNext();) {
		children += iter.next().isAlive() ? 1 : 0;
	    }
	    //System.out.println(children);
	}
    }*/

  
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String dbServer = (args.length > 0) ? args[0] : "127.0.0.1";
        String nlpDB = (args.length > 1) ? args[1] : "mimic3_test"; //"UW_Note";
	String dbTable = (args.length > 2) ? args[2] : "NOTEEVENTS"; //"CLINICAL_NOTE";
        //boolean useMetaMap = (args.length > 3) && args[3].equalsIgnoreCase("mm");
        //int notesCount = (args.length > 4) ? Integer.parseInt(args[4]) : 1;
        final boolean verbose = (args.length > 3) ? args[3].equalsIgnoreCase("1") : false;
	//int nProc = (args.length > 6) ? Integer.parseInt(args[6]) : 10;
	String heapSize = (args.length > 4) ? args[4] : "4g";
        int startNoteID = (args.length > 5) ? Integer.parseInt(args[5]) : -1;
        int endNoteID = (args.length > 6) ? Integer.parseInt(args[6]) : -1;
	String username = (args.length > 7) ? args[7] : "";
	String password = (args.length > 8) ? args[8] : "";
        //boolean clearDB = (args.length > 9) && args[9].equalsIgnoreCase("clearDB");
	//int TRIGFLAG = 3;
	//int PROCFLAG = 4;
	//int COMPFLAG = 1;
	//int ERRFLAG = 0;
	final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	final Date date = new Date();		
	final DataAccess da_MIMIC = new DataAccess();
	final DataAccess da_MIMIC_NLP = new DataAccess();
	//int flag = 0;
	
        try {
	    if (password.equals("")) {
		da_MIMIC.SetupConnection(dbServer, nlpDB, username); // Corrected by Vikas    
		da_MIMIC_NLP.SetupConnection(dbServer, nlpDB, username);
	    } else {
		da_MIMIC.SetupConnection(dbServer, nlpDB, username, password); // Corrected by Vikas    
		da_MIMIC_NLP.SetupConnection(dbServer, nlpDB, username, password);
	    }
            /*if (clearDB) {
                da_MIMIC_NLP.clearDB();
                return;
		}*/
	    
            //IMetamapParser m = useMetaMap ? new MetamapParser_ITHS() : new MetamapLiteParser();
	    IMetamapParser m = new MetamapLiteParser();
	    
            /*if (notesCount > 0) {
                processNotes(0, 1, m, da_MIMIC, da_MIMIC_NLP, dbTable, verbose);
		} else if (startNoteID > 0) {*/
                processNotes(startNoteID, endNoteID, m, da_MIMIC, da_MIMIC_NLP, dbTable, verbose);
		/*} else {
		// DO OPERATIONS CHECKS/UPDATES HERE BECAUSE WE CARE MOSTLY ABOUT MULTI-PROCESS

		// Process if status = 3 (NOTE_TRANSFER_COMPLETE)
		flag = da_MIMIC_NLP.getDBStatus();
		System.out.println("#### STATUS ####\t" + flag + "\t" + dateFormat.format(date));
		if (flag != TRIGFLAG) {
		    return;
		}

		// Update status to 4 (PROCESSING_NOTES)
		da_MIMIC_NLP.setDBStatus(PROCFLAG, "vpejaver");
		flag = da_MIMIC_NLP.getDBStatus();
		System.out.println("#### STATUS ####\t" + flag + "\t" + dateFormat.format(date));

                processNotesMP(m, da_MIMIC, da_MIMIC_NLP, dbServer, nlpDB, dbTable, verbose, nProc, heapSize, username, password);

		// Update status to 1 (WAITING_FOR_NEW_NOTES)
		da_MIMIC_NLP.setDBStatus(COMPFLAG, "vpejaver");
		flag = da_MIMIC_NLP.getDBStatus();
		System.out.println("#### STATUS ####\t" + flag + "\t" + dateFormat.format(date));
		}*/
        } catch (Exception ex) {
	    //da_MIMIC_NLP.setDBStatus(ERRFLAG, "vpejaver");
	    //flag = da_MIMIC_NLP.getDBStatus();
	    //System.out.println("#### STATUS ####\t" + flag + "\t" + dateFormat.format(date));
            Logger.getLogger("PipelineScripts").log(Level.SEVERE, null, ex);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime / 1000);
    }
}
