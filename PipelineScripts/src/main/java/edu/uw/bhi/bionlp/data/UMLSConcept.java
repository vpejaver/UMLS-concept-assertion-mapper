package edu.uw.bhi.bionlp.data;

import java.util.List;

/**
 * Author: melihay
 * Date: Jun 28, 2012
 * Time: 11:31:09 AM
 * Version: 1.0
 */
public class UMLSConcept {
    private String CUI;
    private String conceptName;
    private String phrase; // phrase the concept identified in
//    private List<UMLSSemanticType> semanticTypes;
    private String semanticTypeLabels;
    private int beginTokenIndex;
    private int endTokenIndex;

    public String toString() {
        String str = "";

        str += "CUI:"+this.getCUI()+"\t"+"PHRASE:"+this.getPhrase()+"\tBeginTokenIndex:"+this.getBeginTokenIndex()+"\tEndTokenIndex:"+this.getEndTokenIndex()+"\tSTR:"+this.getConceptName()+"\tSEMTYPES:"+this.getSemanticTypeLabels();

   //     for (UMLSSemanticType semType : this.getSemanticTypes()) {
   //         str += semType.getSemanticType()+"\t";
   //     }

        return str;
    }

    public String getCUI() {
        return CUI;
    }

    public void setCUI(String CUI) {
        this.CUI = CUI;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }
//
//    public List<UMLSSemanticType> getSemanticTypes() {
//        return semanticTypes;
//    }

//    public void setSemanticTypes(List<UMLSSemanticType> semanticTypes) {
//        this.semanticTypes = semanticTypes;
//    }

    public int getBeginTokenIndex() {
        return beginTokenIndex;
    }

    public void setBeginTokenIndex(int beginTokenIndex) {
        this.beginTokenIndex = beginTokenIndex;
    }

    public int getEndTokenIndex() {
        return endTokenIndex;
    }

    public void setEndTokenIndex(int endTokenIndex) {
        this.endTokenIndex = endTokenIndex;
    }

    public String getSemanticTypeLabels() {
        return semanticTypeLabels;
    }

    public void setSemanticTypeLabels(String semanticTypeLabels) {
        this.semanticTypeLabels = semanticTypeLabels;
    }
}
