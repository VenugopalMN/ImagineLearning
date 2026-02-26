package com.imaginelearning.books.mapper;

import com.imaginelearning.books.dto.api.BookSummary;
import com.imaginelearning.books.dto.openlibrary.OpenLibraryBookDoc;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookMapper {

    public BookSummary toBookSummary(OpenLibraryBookDoc doc) {
        if (doc == null) {
            return null;
        }

        return new BookSummary(
                doc.getKey(),
                doc.getTitle(),
                extractPrimaryAuthor(doc.getAuthorName())
        );
    }

    private String extractPrimaryAuthor(List<String> authors) {
        if (authors == null || authors.isEmpty()) {
            return null;
        }

        return authors.get(0);
    }
}
