/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.bhi.bionlp.db;

import com.mysql.cj.jdbc.MysqlDataSource;
import edu.uw.bhi.bionlp.data.ClinicalNote;
import edu.uw.bhi.bionlp.data.OutputConcept;
import edu.uw.bhi.bionlp.data.HashPointer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author wlau
 */
public class DataAccess {

    enum SOURCES {

        MYSQL
    };
    private DataSource db;
    private String username=null;
    private String password=null;
    private String url=null;
    private String dbname=null;
    private String dbServer=null;

    public void SetupConnection() throws SQLException, ClassNotFoundException {
        SetupConnection("127.0.0.1");
    }

    public void SetupConnection(String dbServer) throws ClassNotFoundException { 
        SetupConnection(dbServer,"mimic3_test");
    }

    public void SetupConnection(String dbServer, String dbName) throws ClassNotFoundException {
	SetupConnection(dbServer,dbName,"vpejaver");
    }

    public void SetupConnection(String dbServer, String dbName, String username) throws ClassNotFoundException {
	SetupConnection(dbServer,dbName,username,"vpejaver");
    }

    public void SetupConnection(String dbServer, String dbName, String username, String password) throws ClassNotFoundException {
        db = new MysqlDataSource();
        this.dbServer=dbServer;
        this.url = "jdbc:mysql://" + dbServer + ":3306/" + dbName + "?serverTimezone=PST&max_allowed_packet=536870912";
        this.username=username;
        this.password=password;
	this.dbname=dbName;
        ((MysqlDataSource) db).setUrl(this.url);
        ((MysqlDataSource) db).setUser(this.username);
        ((MysqlDataSource) db).setPassword(this.password); 
        getConnection(db);
    }
    
    public void resetConnection() {
        try {
            Logger.getLogger(DataAccess.class.getName()).log(Level.INFO,"resetting connection .... ");
            Thread.sleep(30000);
            SetupConnection(this.dbServer,this.dbname,this.username,this.password);
            Logger.getLogger(DataAccess.class.getName()).log(Level.INFO,"resetting connection suceessfully.... ");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            resetConnection();
        }
    }

    private void getConnection(DataSource db) {
        try {
            db.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DataAccess.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(); 
        }
    }

    public List<String> getStringData(String sql) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        List<String> results = null;

        try {
            results = jdbcTemplate.query(sql,
                    new RowMapper() {
                        @Override
                        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getString(1);
                        }
                    ;
        } ); 
        } catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" getStringData").log(Level.SEVERE, null, ex); 
            resetConnection();
            results = getStringData(sql);
            Logger.getLogger(DataAccess.class.getName()+" getStringData").log(Level.INFO," RETRY success"); 
        }

        return results;
    }

    public List<ClinicalNote> getNoteData(String sql) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        List<ClinicalNote> results = null;
        try {
            jdbcTemplate.setResultsMapCaseInsensitive(true);
            results = jdbcTemplate.query(sql,
                    new RowMapper() {
                        @Override
                        public ClinicalNote mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return new ClinicalNote(rs.getLong(1), rs.getString(2));
                        }
                    ;
        } ); 
        }
         catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" getNoteData").log(Level.SEVERE, null, ex); 
            resetConnection();
            results = getNoteData(sql);
            Logger.getLogger(DataAccess.class.getName()+" getNoteData").log(Level.INFO," RETRY success"); 
        }

        return results;
    }

    public long getLongData(String sql) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        long result=-1;
        try{
            result=jdbcTemplate.queryForLong(sql);
        }catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" getLongData").log(Level.SEVERE, null, ex); 
            resetConnection();
            result = getLongData(sql);
            Logger.getLogger(DataAccess.class.getName()+" getLongData").log(Level.INFO," RETRY success"); 
        }
        return result;
    }

    public void insertSentence(long noteID, long sentenceID, String sentenceText) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        String sql = "INSERT INTO NOTEEVENTS_SENTENCES (NOTEEVENTS_ROW_ID, SENTENCE_ID, SENTENCE_TEXT) VALUES (?,?,?)";
        try {
            jdbcTemplate.update(sql, new Object[]{noteID, sentenceID, sentenceText});
        } catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" insertSentence").log(Level.SEVERE, null, ex); 
            resetConnection();
            insertSentence(noteID, sentenceID, sentenceText);
            Logger.getLogger(DataAccess.class.getName()+" insertSentence").log(Level.INFO," RETRY success"); 
        }

    }

    public void insertSentence(long noteID, long sentenceID, String guID, String sentenceText, long cS, long cE, long pS, long pE, int flag) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
	String sql = "INSERT INTO NOTEEVENTS_SENTENCES (NOTEEVENTS_ROW_ID, SENTENCE_ID, GUID, SENTENCE_TEXT, CREATE_START, CREATE_END, PROCESSED_START, PROCESSED_END) VALUES (?,?,?,?,?,?,?,?)";
	if (flag == 1) {
	    sql = "INSERT INTO NOTEEVENTS_SENTENCES (NOTEEVENTS_ROW_ID, SENTENCE_ID, GUID, SENTENCE_TEXT, CREATE_START, CREATE_END, PRECOMPUTED_START, PRECOMPUTED_END) VALUES (?,?,?,?,?,?,?,?)";
	}
       	
	try {
            jdbcTemplate.update(sql, new Object[]{noteID, sentenceID, guID, sentenceText, cS, cE, pS, pE});
        } catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" insertSentence").log(Level.SEVERE, null, ex); 
            resetConnection();
            insertSentence(noteID, sentenceID, guID, sentenceText, cS, cE, pS, pE, flag);
            Logger.getLogger(DataAccess.class.getName()+" insertSentence").log(Level.INFO," RETRY success"); 
        }
    }
    
    public void deleteSentences() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        String sql = "truncate table NOTEEVENTS_SENTENCES";
        try {
            jdbcTemplate.update(sql);
        } catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" deleteSentences").log(Level.SEVERE, null, ex); 
            resetConnection();
            deleteSentences(); 
            Logger.getLogger(DataAccess.class.getName()+" deleteSentences").log(Level.INFO," RETRY success"); 
        }
    }

    // ADDED BY VIKAS TO CHECK GUID (uses Wilson's way of doing things)
    public List<OutputConcept> matchGUID(String guid) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        String sql = "select distinct CONCEPT_CUI, PHASE, BEGIN_TOKEN_INDEX, END_TOKEN_INDEX, CONCEPT_STR, SEMANTIC_TYPE, PREDICTION from SENTENCE_CONCEPTS where GUID = '" + guid + "'; ";
        List<OutputConcept> results = jdbcTemplate.query(sql,
                new RowMapper() {
                    @Override
                    public OutputConcept mapRow(ResultSet rs, int rowNum) throws SQLException {
                        OutputConcept uc = new OutputConcept();
                        uc.setCUI(rs.getString(1));
                        uc.setPhrase(rs.getString(2));
                        uc.setBeginTokenIndex(rs.getInt(3));
                        uc.setEndTokenIndex(rs.getInt(4));
                        uc.setConceptName(rs.getString(5));
                        uc.setSemanticTypeLabels(rs.getString(6));
                        uc.setAssertion(rs.getString(7));
                        return uc;
                    }
                  ;
                });
        return results;
    }

    public List<HashPointer> matchGUIDFast(String guid) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        String sql = "select distinct NOTEEVENTS_ROW_ID, SENTENCE_ID from NOTEEVENTS_SENTENCES where GUID = '" + guid + "'; ";
        List<HashPointer> results = jdbcTemplate.query(sql,
                new RowMapper() {
                    @Override
                    public HashPointer mapRow(ResultSet rs, int rowNum) throws SQLException {
			HashPointer hp = new HashPointer();
                        //hp.setID(rs.getLong(1));
                        hp.setRowID(rs.getLong(1));
                        hp.setSentenceID(rs.getLong(2));
                        return hp;
                    }
                  ;
		  });
        return results;
    }

    public void insertPointer(long noteID, long sentenceID, long matchedNoteID, long matchedSentenceID) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
	String sql = "INSERT INTO SENTENCE_HASH_MATCH_POINTER (NOTEEVENTS_ROW_ID, NOTEEVENTS_SENTENCE_ID, MATCHED_ROW_ID, MATCHED_SENTENCE_ID) VALUES (?,?,?,?)";
       	
	try {
            jdbcTemplate.update(sql, new Object[]{noteID, sentenceID, matchedNoteID, matchedSentenceID});
        } catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" insertSentence").log(Level.SEVERE, null, ex); 
            resetConnection();
            insertPointer(noteID, sentenceID, matchedNoteID, matchedSentenceID);
            Logger.getLogger(DataAccess.class.getName()+" insertSentence").log(Level.INFO," RETRY success"); 
        }
    }

    //////////////////////////////////////////////////////////////////
    
   
    public void deleteSentenceConcepts(long noteID, long sentenceID) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        String sql = "delete from SENTENCE_CONCEPTS where NOTEEVENTS_ROW_ID = " + noteID + " and SENTENCE_ID = " + sentenceID + "; ";
        try {
            jdbcTemplate.update(sql);
        } catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" deleteSentenceConcepts").log(Level.SEVERE, null, ex); 
            resetConnection();
            deleteSentenceConcepts(noteID, sentenceID); 
            Logger.getLogger(DataAccess.class.getName()+" deleteSentenceConcepts").log(Level.INFO," RETRY success"); 
        }
    }

    public void insertSentenceConcept(long noteID, int sentenceID, String guID, String CUI, String phase, int beginTokenIndex, int endTokenIndex, String conceptStr, String semType, String prediction) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
//    SimpleJdbcCall jdbcCall = new SimpleJdbcCall(new JdbcTemplate(db)).withProcedureName("sp_insertSentenceConcept");

//    MapSqlParameterSource in = new MapSqlParameterSource();
//    in.addValue("noteId", noteID, java.sql.Types.INTEGER);
//    in.addValue("sentenceID", sentenceID, java.sql.Types.INTEGER);
//    in.addValue("cui", CUI, java.sql.Types.VARCHAR);
//    in.addValue("phase", phase, java.sql.Types.VARCHAR);
//    in.addValue("beginTokenIndex", beginTokenIndex, java.sql.Types.INTEGER);
//    in.addValue("endTokenIndex", endTokenIndex, java.sql.Types.INTEGER);
//    in.addValue("conceptStr", conceptStr, java.sql.Types.VARCHAR);
//    in.addValue("semType", semType, java.sql.Types.VARCHAR);
//    jdbcCall.execute(in);
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
//        jdbcTemplate.setResultsMapCaseInsensitive(true);
//        String sql = "INSERT INTO SENTENCE_CONCEPTS (NOTEEVENTS_ROW_ID, SENTENCE_ID, CONCEPT_CUI, PHASE, BEGIN_TOKEN_INDEX, END_TOKEN_INDEX, CONCEPT_STR, SEMANTIC_TYPE) "
//                + "VALUES (?,?,?,?,?,?,?,?)";
        try {
            jdbcTemplate.update("call sp_insertSentenceConcept(?,?,?,?,?,?,?,?,?,?)", new Object[]{noteID, sentenceID, guID, CUI, phase, beginTokenIndex, endTokenIndex, conceptStr, semType, prediction});
        } catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" insertSentenceConcept").log(Level.SEVERE, null, ex); 
            resetConnection();
            insertSentenceConcept(noteID, sentenceID, guID, CUI, phase, beginTokenIndex, endTokenIndex, conceptStr, semType, prediction);
            Logger.getLogger(DataAccess.class.getName()+" insertSentenceConcept").log(Level.INFO," RETRY success"); 
        }
    }

    public void clearDB() {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        try {
            jdbcTemplate.update("call sp_clearAllData()");
        } catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" clearDB").log(Level.SEVERE, null, ex); 
            resetConnection();
            clearDB(); 
            Logger.getLogger(DataAccess.class.getName()+" clearDB").log(Level.INFO," RETRY success"); 
        }
    }

    public void checkpointing(long noteID, int numSentences, long startTime, long endTime) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        String sql = "INSERT INTO CHECKPOINTS (NOTEEVENTS_ROW_ID, NO_OF_SENTENCES, START_TIME, END_TIME) VALUES (?,?,?,?)";
        try {
            jdbcTemplate.update(sql, new Object[]{noteID, numSentences, startTime, endTime});
        } catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" checkpointing").log(Level.SEVERE, null, ex); 
            resetConnection();
            checkpointing(noteID, numSentences, startTime, endTime);
            Logger.getLogger(DataAccess.class.getName()+" checkpointing").log(Level.INFO," RETRY success"); 
        }
    }

    public int getDBStatus() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
	int status = 0;
	List<String> results = null;
        try {
            String sql = "call sp_getCurrentState";
            results = jdbcTemplate.query(sql,
                    new RowMapper() {
                        @Override
                        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                            //System.out.println(rs.getString(1));
			    return rs.getString(1);
                        };
		    });
        } catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" getDBStatus").log(Level.SEVERE, null, ex); 
            resetConnection();
            getDBStatus();
            Logger.getLogger(DataAccess.class.getName()+" getDBStatus").log(Level.INFO," RETRY success"); 
        }
	status = Integer.parseInt(results.get(0));
	return status;
    }

    public void setDBStatus(int status, String user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
        try {
            jdbcTemplate.update("call sp_updateCurrentState(?,?)", new Object[]{status, user});
        } catch (CannotGetJdbcConnectionException ex) {
            Logger.getLogger(DataAccess.class.getName()+" setDBStatus").log(Level.SEVERE, null, ex); 
            resetConnection();
            setDBStatus(status, user);
            Logger.getLogger(DataAccess.class.getName()+" setDBStatus").log(Level.INFO," RETRY success"); 
        }
    }

}
