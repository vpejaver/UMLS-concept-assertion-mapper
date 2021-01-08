package edu.uw.bhi.bionlp.data;

/**
 * Author: melihay
 * Date: Sep 28, 2011
 * Time: 4:58:45 PM
 * Version: 1.0
 */
public class RadiologyToken {
    String token;
    String POS;
    int tokenIndex = -1;

    public String toString() {
        return this.token+"/"+this.POS;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPOS() {
        return POS;
    }

    public void setPOS(String POS) {
        this.POS = POS;
    }

    public int getTokenIndex() {
        return tokenIndex;
    }

    public void setTokenIndex(int tokenIndex) {
        this.tokenIndex = tokenIndex;
    }
}
