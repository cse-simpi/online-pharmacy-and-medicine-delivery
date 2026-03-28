package com.onlinePharmacy.order.exception;

import com.onlinePharmacy.order.dto.MedicineSuggestion;
import java.util.List;

public class InsufficientStockException extends RuntimeException {
    
    private final List<MedicineSuggestion> suggestions;

    // Updated constructor to take both the error message and the list
    public InsufficientStockException(String message, List<MedicineSuggestion> suggestions) {
        super(message);
        this.suggestions = suggestions;
    }

    public List<MedicineSuggestion> getSuggestions() {
        return suggestions;
    }
}