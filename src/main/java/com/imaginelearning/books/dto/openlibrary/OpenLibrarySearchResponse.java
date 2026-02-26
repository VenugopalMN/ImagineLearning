package com.imaginelearning.books.dto.openlibrary;

import java.util.ArrayList;
import java.util.List;

public class OpenLibrarySearchResponse {

    private List<OpenLibraryBookDoc> docs = new ArrayList<>();

    public List<OpenLibraryBookDoc> getDocs() {
        return docs;
    }

    public void setDocs(List<OpenLibraryBookDoc> docs) {
        this.docs = docs;
    }
}
