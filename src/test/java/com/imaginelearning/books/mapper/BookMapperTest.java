package com.imaginelearning.books.mapper;

import com.imaginelearning.books.dto.api.BookSummary;
import com.imaginelearning.books.dto.openlibrary.OpenLibraryBookDoc;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookMapperTest {

    private final BookMapper mapper = new BookMapper();

    @Test
    void mapsOpenLibraryDocToBookSummary() {
        OpenLibraryBookDoc doc = new OpenLibraryBookDoc();
        doc.setKey("/works/OL123W");
        doc.setTitle("The Hobbit");
        doc.setAuthorName(List.of("J.R.R. Tolkien", "Another Author"));

        BookSummary summary = mapper.toBookSummary(doc);

        assertThat(summary.id()).isEqualTo("/works/OL123W");
        assertThat(summary.title()).isEqualTo("The Hobbit");
        assertThat(summary.author()).isEqualTo("J.R.R. Tolkien");
    }

    @Test
    void usesNullWhenNoAuthorProvided() {
        OpenLibraryBookDoc doc = new OpenLibraryBookDoc();
        doc.setKey("/works/OL456W");
        doc.setTitle("Unknown Author Book");

        BookSummary summary = mapper.toBookSummary(doc);

        assertThat(summary.author()).isNull();
    }
}
