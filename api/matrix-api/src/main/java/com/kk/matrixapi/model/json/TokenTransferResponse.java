package com.kk.matrixapi.model.json;

public class TokenTransferResponse {
    private String[] items;
    private String next_page_path;

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    public String getNext_page_path() {
        return next_page_path;
    }

    public void setNext_page_path(String next_page_path) {
        this.next_page_path = next_page_path;
    }
}
