package edu.uw.bhi.bionlp.pipeline;

import edu.uw.bhi.bionlp.data.RadiologyToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: melihay
 * Date: 7/10/15
 * Version: 1.0
 */
public class Tokenizer {

    //TODO: same tokenization needs to be applied at the sentence level!
    public static List<RadiologyToken> getTokensSevereTokenization(String text) {
        List<RadiologyToken> tokenList = new ArrayList<RadiologyToken>();

        String result = text.replaceAll("[!\"#$%&'()*+,-./:;<=>?\\\\@\\[\\]^_`{|}~0-9]", " ");
        String trimresult = result.replaceAll("\\s+", " ").trim();

        if (!trimresult.equals("")) {
            String[] tokens = trimresult.split("\\s+");
            for (int i = 0; i < tokens.length; i++) {
                String contentWord = tokens[i].toLowerCase();
                if (!contentWord.trim().equals("")) {
                    RadiologyToken token = new RadiologyToken();
                    token.setToken(contentWord);
                    //token.setTokenIndex(i);
                    tokenList.add(token);
                } else {
                    System.out.println("We have a token index problem!");
                }
            }
        }
        return tokenList;
    }
}
