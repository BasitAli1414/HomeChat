package com.bashoo.homechat;

public class Request{
    public String display_name;
    public String display_status;
    public String display_thumb_image;

    public Request() {
    }

    public Request(String display_name, String display_status, String display_thumb_image) {
        this.display_name = display_name;
        this.display_status = display_status;
        this.display_thumb_image = display_thumb_image;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getDisplay_status() {
        return display_status;
    }

    public void setDisplay_status(String display_status) {
        this.display_status = display_status;
    }

    public String getDisplay_thumb_image() {
        return display_thumb_image;
    }

    public void setDisplay_thumb_image(String display_thumb_image) {
        this.display_thumb_image = display_thumb_image;
    }
}
