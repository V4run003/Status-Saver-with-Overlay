package com.abc.StatusSaver.Model;

import android.net.Uri;

public class StoryModel {
    private String name;
    private Uri uri;
    private String path;
    private String filename;
    private String viewType;

    public StoryModel(String name, Uri uri, String path, String filename, String viewType) {
        this.name = name;
        this.uri = uri;
        this.path = path;
        this.filename = filename;
        this.viewType = viewType;
    }

    public StoryModel() {
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
