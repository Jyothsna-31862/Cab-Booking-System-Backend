package com.cabbooking.ratingservice.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Invalid Rating Exception Tests")
class InvalidRatingExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        // Given
        String errorMessage = "Score must be between 1 and 5.";

        // When
        InvalidRatingException exception = new InvalidRatingException(errorMessage);

        // Then
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void shouldCreateExceptionWithMessageAndCause() {
        // Given
        String errorMessage = "Invalid rating data";
        Throwable cause = new RuntimeException("Database error");

        // When
        InvalidRatingException exception = new InvalidRatingException(errorMessage, cause);

        // Then
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Should be throwable")
    void shouldBeThrowable() {
        // Given
        String errorMessage = "Test exception";

        // When & Then
        InvalidRatingException exception = assertThrows(
                InvalidRatingException.class,
                () -> {
                    throw new InvalidRatingException(errorMessage);
                }
        );

        assertEquals(errorMessage, exception.getMessage());
    }
}
