package com.walmart.moviesapi.events;

public class MovieEvent {

    private String action;
    private Object payload;

    public MovieEvent() {
    }

    public MovieEvent(String action, Object payload) {
        this.action = action;
        this.payload = payload;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
