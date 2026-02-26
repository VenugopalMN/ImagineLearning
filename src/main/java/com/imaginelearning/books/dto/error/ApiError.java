package com.imaginelearning.books.dto.error;

import java.time.Instant;

public record ApiError(String code, String message, Instant timestamp) {
}
