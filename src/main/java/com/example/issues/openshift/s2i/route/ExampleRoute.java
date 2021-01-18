package com.example.issues.openshift.s2i.route;

import org.apache.camel.model.rest.RestParamType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExampleRoute extends BaseRoute {

    @Override
    protected void configureRoute() {
        rest("/input")
                .post()
                .consumes("application/json")
                .param()
                    .name("body")
                    .type(RestParamType.body)
                    .required(true)
                .endParam()
                .to("direct:pipeline");

        from("direct:pipeline")
                .routeId("example_route")
                .log("Payload was ${body}");
    }
}
