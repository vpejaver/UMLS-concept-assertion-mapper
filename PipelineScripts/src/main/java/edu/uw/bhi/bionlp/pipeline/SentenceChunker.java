/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.bhi.bionlp.pipeline;

import java.io.FileInputStream; 
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

/**
 *
 * @author wlau
 */
public class SentenceChunker {

    public static List<String> getSentences(String text) throws Exception{
        InputStream modelIn =  SentenceChunker.class.getClassLoader().getResourceAsStream("en-sent.bin");//new FileInputStream("en-sent.bin");
        try{
            SentenceModel model = new SentenceModel(modelIn);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model); 
            return Arrays.asList(sentenceDetector.sentDetect(text));
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
