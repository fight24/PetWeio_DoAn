package com.petweio.projectdoan.Model;

import com.google.gson.annotations.SerializedName;

public class Property {
    @SerializedName("id")
    private long id;
    @SerializedName("date")
    private String date;
    @SerializedName("message")
    private String message;
    @SerializedName("topic")
    private String topic;
    @SerializedName("device_id")
    private long device_id;
    public Property(String date, String message, String topic) {
        this.date = date;
        this.message = message;
        this.topic = topic;
    }

    public Property(long id, String date, String message, String topic, long device_id) {
        this.id = id;
        this.date = date;
        this.message = message;
        this.topic = topic;
        this.device_id = device_id;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(long device_id) {
        this.device_id = device_id;
    }

    @Override
    public String toString() {
        return "Property{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", message='" + message + '\'' +
                ", topic='" + topic + '\'' +
                ", device_id=" + device_id +
                '}';
    }
}
