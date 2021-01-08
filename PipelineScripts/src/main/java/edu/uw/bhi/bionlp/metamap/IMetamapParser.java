/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.bhi.bionlp.metamap;

import bioc.BioCDocument;
import edu.uw.bhi.bionlp.data.UMLSConcept;
import java.util.List;

/**
 *
 * @author wlau
 */
public interface IMetamapParser {

    List<UMLSConcept> parseSentenceWithMetamap(String sentenceText) throws Exception;
    
}
