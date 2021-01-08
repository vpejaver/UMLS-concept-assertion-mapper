package edu.uw.bhi.bionlp.data;

/**
 * Author: vpejaver
 * Date: Aug 19, 2017
 * Time: 03:36:21 PM
 * Version: 1.0
 */

public class OutputConcept extends UMLSConcept {

    private String assertion;

    public String toString() {
	 String str = "";

	 str += "CUI:"+this.getCUI()+"\t"+"PHRASE:"+this.getPhrase()+"\tBeginTokenIndex:"+this.getBeginTokenIndex()+"\tEndTokenIndex:"+this.getEndTokenIndex()+"\tSTR:"+this.getConceptName()+"\tSEMTYPES:"+this.getSemanticTypeLabels()+"\tASSERTION:"+this.getAssertion();

        return str;
    }
    
    public String getAssertion() {
	return assertion;
    }
    
    public void setAssertion(String prediction) {
	this.assertion = prediction;
    }

}

