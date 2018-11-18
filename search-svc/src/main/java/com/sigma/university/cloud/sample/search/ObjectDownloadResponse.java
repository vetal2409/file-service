package com.sigma.university.cloud.sample.search;

import java.net.URL;

public class ObjectDownloadResponse {

    private URL downloadUrl;

    public URL getDownloadUrl() {
        return downloadUrl;
    }

    public ObjectDownloadResponse setDownloadUrl(URL downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }
}
