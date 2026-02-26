package com.imaginelearning.books.service;

import com.imaginelearning.books.client.OpenLibraryClient;
import com.imaginelearning.books.dto.api.BookSummary;
import com.imaginelearning.books.dto.openlibrary.OpenLibraryBookDoc;
import com.imaginelearning.books.dto.openlibrary.OpenLibrarySearchResponse;
import com.imaginelearning.books.mapper.BookMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookSearchServiceTest {

    @Test
    void returnsMappedBooksFromClientResponse() {
        OpenLibraryClient openLibraryClient = mock(OpenLibraryClient.class);
        BookSearchService service = new BookSearchService(openLibraryClient, new BookMapper());

        OpenLibraryBookDoc first = new OpenLibraryBookDoc();
        first.setKey("/works/OL1W");
        first.setTitle("Book One");
        first.setAuthorName(List.of("Author One"));

        OpenLibraryBookDoc second = new OpenLibraryBookDoc();
        second.setKey("/works/OL2W");
        second.setTitle("Book Two");
        second.setAuthorName(List.of("Author Two"));

        OpenLibrarySearchResponse response = new OpenLibrarySearchResponse();
        response.setDocs(List.of(first, second));

        when(openLibraryClient.search("books")).thenReturn(response);

        List<BookSummary> result = service.searchBooks("books");

        assertThat(result).containsExactly(
                new BookSummary("/works/OL1W", "Book One", "Author One"),
                new BookSummary("/works/OL2W", "Book Two", "Author Two")
        );
    }
}
