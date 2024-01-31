package com.ite5year.optimisticlock;

public class OptimisticLockException extends RuntimeException {

    public OptimisticLockException(String description) {
        super(description);
    }
}