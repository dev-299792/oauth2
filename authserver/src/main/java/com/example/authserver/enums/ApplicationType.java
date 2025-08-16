package com.example.authserver.enums;

import lombok.Getter;


/**
 * Defines the type of application.
 * A web application is confidential and requires a client secret.
 * A single page application is public and does not require a client secret.
 *
 */
@Getter
public enum ApplicationType {
    WEB_APP("Web App"), SINGLE_PAGE_APP("Single Page App");

    final private String displayName;

    ApplicationType(String displayName) {
        this.displayName = displayName;
    }
}
