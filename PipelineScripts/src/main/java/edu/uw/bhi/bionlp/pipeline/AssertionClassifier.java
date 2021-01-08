/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.bhi.bionlp.pipeline;

import edu.uw.bhi.uwassert.AssertionClassification;
import java.io.IOException;
import static java.lang.System.out;
import name.adibejan.util.IntPair;

/**
 *
 * @author wlau
 */
public class AssertionClassifier {
    static {
        System.setProperty("CONFIGFILE",
                    "assert-resources/assertcls.properties");
	System.setProperty("ASSERTRESOURCES",
                    "assert-resources");
	System.setProperty("LIBLINEAR_PATH",
	"assert-resources/liblinear-1.93");
    }
    public static String predict (String sentence, IntPair oConceptBoundaries) throws IOException {
	return AssertionClassification.predictMP(sentence,  oConceptBoundaries);  
    }
    
    public static void main(String[] args) {
    String s1 = "The patient, will-be discharged now with a final diagnosis of no acute asthmatic bronchitis with chronic obstructive pulmonary disease .";    
    out.println("s1 = "+AssertionClassification.predict(s1, new IntPair(13, 15)));
    
    String s2 = "The patient, will-be discharged now with a final diagnosis of acute asthmatic bronchitis with chronic obstructive pulmonary disease.";
    //out.println("s2 = "+AssertionClassification.predict(s2, new IntPair(12, 14)));
    out.println("s2 = "+AssertionClassification.predict(s2, 12, 14));
    
    String s3 = "He reports severe dyspnea on exertion.";
    out.println("s3 = "+AssertionClassification.predict(s3, new IntPair(3, 3)));
    
    String s4 = "Blunting of left CPA likely effusion.";
    out.println("s4 = "+AssertionClassification.predict(s4, new IntPair(5, 5)));    

    String s5 = "Father has dyspnea.";
    //out.println("s5 = "+AssertionClassification.predict(s5, new IntPair(2, 2)));
    out.println("s5 = "+AssertionClassification.predict(s5, 2, 2));
  }
}
