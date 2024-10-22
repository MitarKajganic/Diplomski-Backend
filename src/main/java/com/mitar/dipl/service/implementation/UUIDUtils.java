package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.custom.InvalidUUIDException;

import java.util.UUID;

public class UUIDUtils {

    public static UUID parseUUID(String uuidStr) throws InvalidUUIDException {
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDException("Invalid UUID format: " + uuidStr);
        }
    }

}