package com.qa.practice.api.specs;

import com.qa.practice.config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

public final class RequestSpecs {

    private RequestSpecs() {
    }

    public static RequestSpecification base() {
        return new RequestSpecBuilder()
                .setBaseUri(Config.apiBaseUrl())
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addFilter(new AllureRestAssured())
                .log(LogDetail.METHOD)
                .log(LogDetail.URI)
                .build();
    }

    public static RequestSpecification withToken(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(base())
                .addCookie("token", token)
                .build();
    }
}
