package com.hammer.hammer.bid.exception;

public class BidAmountTooLowException extends RuntimeException {
    public BidAmountTooLowException(String message) {
        super(message);
    }
}