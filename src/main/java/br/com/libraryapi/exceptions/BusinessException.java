package br.com.libraryapi.exceptions;

public class BusinessException extends RuntimeException {
    public BusinessException(String s) {
        super(s);
    }
}
