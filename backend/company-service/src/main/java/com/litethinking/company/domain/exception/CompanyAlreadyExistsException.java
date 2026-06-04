package com.litethinking.company.domain.exception;

public class CompanyAlreadyExistsException extends RuntimeException {

    public CompanyAlreadyExistsException(String message) {
        super(message);
    }
}
