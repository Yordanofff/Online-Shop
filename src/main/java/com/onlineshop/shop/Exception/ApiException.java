package com.onlineshop.shop.Exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
