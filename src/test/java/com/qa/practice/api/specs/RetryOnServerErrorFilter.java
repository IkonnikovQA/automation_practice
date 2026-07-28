package com.qa.practice.api.specs;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * Minimal retry strategy for unstable public sandboxes. Retries only on 429 and 5xx to avoid
 * masking functional defects.
 */
public class RetryOnServerErrorFilter implements Filter {

  private final int maxAttempts;

  public RetryOnServerErrorFilter(int maxAttempts) {
    this.maxAttempts = Math.max(1, maxAttempts);
  }

  @Override
  public Response filter(
      FilterableRequestSpecification requestSpec,
      FilterableResponseSpecification responseSpec,
      FilterContext ctx) {
    Response response = null;
    RuntimeException lastException = null;
    int attempt = 1;
    while (attempt <= maxAttempts) {
      try {
        response = ctx.next(requestSpec, responseSpec);
        int statusCode = response.statusCode();
        if (!shouldRetry(statusCode) || attempt == maxAttempts) {
          return response;
        }
      } catch (RuntimeException e) {
        lastException = e;
        if (attempt == maxAttempts) {
          throw e;
        }
      }
      attempt++;
      sleepBackoff(attempt);
    }
    if (lastException != null) {
      throw lastException;
    }
    return response;
  }

  private static boolean shouldRetry(int statusCode) {
    return statusCode == 429 || statusCode >= 500;
  }

  private static void sleepBackoff(int attempt) {
    try {
      Thread.sleep(200L * attempt);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
