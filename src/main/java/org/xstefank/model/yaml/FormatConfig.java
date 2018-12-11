package org.xstefank.model.yaml;

public class FormatConfig {

    private String repository;
    private String statusUrl;
    private Format defaultFormat;
    private Format bugFormat;
    private Format featureFormat;

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getStatusUrl() {
        return statusUrl;
    }

    public void setStatusUrl(String statusUrl) {
        this.statusUrl = statusUrl;
    }

    public Format getDefaultFormat() {
        return defaultFormat;
    }

    public void setDefaultFormat(Format defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    public Format getBugFormat() {
        return bugFormat;
    }

    public void setBugFormat(Format bugFormat) {
        this.bugFormat = bugFormat;
    }

    public Format getFeatureFormat() {
        return featureFormat;
    }

    public void setFeatureFormat(Format featureFormat) {
        this.featureFormat = featureFormat;
    }
}
