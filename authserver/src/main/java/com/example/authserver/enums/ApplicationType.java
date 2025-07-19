package com.example.authserver.enums;

import lombok.Getter;

@Getter
public enum ApplicationType {
    WEB_APP("Web App"), SINGLE_PAGE_APP("Single Page App");

    final private String displayName;

    ApplicationType(String displayName) {
        this.displayName = displayName;
    }
}
