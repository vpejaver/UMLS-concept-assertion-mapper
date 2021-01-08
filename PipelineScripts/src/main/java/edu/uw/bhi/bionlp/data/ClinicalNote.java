/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.bhi.bionlp.data;

/**
 *
 * @author wlau
 */
public class ClinicalNote {
    private long ID;
    private String text;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ClinicalNote(long ID, String text) {
        this.ID = ID;
        this.text = text;
    }
    
}
