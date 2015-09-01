package com.sifiso.codetribe.minisasslibrary.util;

/**
 * Created by Chris on 2015-02-20.
 */
public class PMException extends Exception {
    public PMException(String message) {
        this.message = message;
    }
    public String message;
}
