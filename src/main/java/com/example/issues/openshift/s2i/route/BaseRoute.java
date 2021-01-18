package com.example.issues.openshift.s2i.route;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;

import java.net.SocketException;
import java.net.SocketTimeoutException;

public abstract class BaseRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        onException(SocketException.class)
                .routeId("errorSocket")
                .logRetryAttempted(true)
                .logRetryStackTrace(true)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .retriesExhaustedLogLevel(LoggingLevel.ERROR)
                .maximumRedeliveries(5);
        onException(SocketTimeoutException.class)
                .routeId("errorSocketTimeout")
                .logRetryAttempted(true)
                .logRetryStackTrace(true)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .retriesExhaustedLogLevel(LoggingLevel.ERROR)
                .maximumRedeliveries(5);
        onException(HttpOperationFailedException.class)
                .routeId("errorHttpOperationFailed")
                .onWhen(simple("${exception.statusCode} >= 500"))
                .logHandled(true)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("${exception.statusCode}"))
                .setBody(simple("${exception.responseBody}"))
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"));
        onException(HttpOperationFailedException.class)
                .routeId("errorHttpOperationFailed")
                .onWhen(simple("${exception.statusCode} >= 400"))
                .logHandled(true)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("${exception.statusCode}"))
                .setBody(simple("${exception.responseBody}"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        getContext().setMessageHistory(true);

        configureRoute();
    }

    protected abstract void configureRoute();
}
