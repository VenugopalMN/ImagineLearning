package com.imaginelearning.books.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OpenLibraryBookDoc {

    private String title;
    private String key;

    //Test
    @JsonProperty("author_name")
    private List<String> authorName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getAuthorName() {
        return authorName;
    }

    public void setAuthorName(List<String> authorName) {
        this.authorName = authorName;
    }
}
