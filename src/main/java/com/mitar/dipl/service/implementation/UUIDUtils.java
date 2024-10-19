package com.mitar.dipl.service.implementation;

import com.mitar.dipl.exception.InvalidUUIDException;

import java.util.UUID;

public class UUIDUtils {

    /**
     * Parses a string into a UUID.
     *
     * @param uuidStr the string to parse
     * @return the parsed UUID
     * @throws InvalidUUIDException if the string is not a valid UUID
     */
    public static UUID parseUUID(String uuidStr) throws InvalidUUIDException {
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDException("Invalid UUID format: " + uuidStr);
        }
    }
}