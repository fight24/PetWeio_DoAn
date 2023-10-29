package com.petweio.projectdoan.Model;

public class Property {
    private String date;
    private String message;
    private String topic;

    public Property(String date, String message, String topic) {
        this.date = date;
        this.message = message;
        this.topic = topic;
    }

    public Property() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
