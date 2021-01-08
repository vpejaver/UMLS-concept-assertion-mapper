package edu.uw.bhi.bionlp.data;

import java.util.List;

/**
 * Author: vpejaver
 * Date: Nov 25, 2017
 * Time: 12:24:17 PM
 * Version: 1.0
 */
public class HashPointer {
    //private long ID;
    private long rowID;
    private long sentenceID;

    public String toString() {
        String str = "";

        str += "ROWID:"+this.getRowID()+"\tSentenceID:"+this.getSentenceID();
        return str;
    }

    /*public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
	}*/

    public long getRowID() {
        return rowID;
    }

    public void setRowID(long rowID) {
        this.rowID = rowID;
    }

    public long getSentenceID() {
        return sentenceID;
    }

    public void setSentenceID(long sentenceID) {
        this.sentenceID = sentenceID;
    }
}
