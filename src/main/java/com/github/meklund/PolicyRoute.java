package com.github.meklund;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.apache.camel.LoggingLevel.*;

/**
 * A simple Camel route that receives a rest-call and logs info before sending it on to provided backend
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class PolicyRoute extends RouteBuilder {

    protected static final Logger logger = LoggerFactory.getLogger(PolicyRoute.class);

    @Override
    public void configure() throws Exception {
        final RouteDefinition from;

        onException(Exception.class).handled(true)
                .log(ERROR, logger, "Error when transferring request from 3scale to backend:${exception.stacktrace}")
                .stop();

        from("netty-http:proxy://0.0.0.0:8083/").id("api_route").to("direct:processRequest");


        from("direct:processRequest")
                .process(PolicyRoute::logDetails)
                .setHeader("BackendDest").method(DestinationBean.class, "getDestination")
                .log(INFO, logger, "Preparing forwarding of request to ${header.BackendDest}")
                .setBody(constant("Camel-added body"))
                .setHeader("ProxyHeaderAdded", constant("SomeClientId"))
                .log("Headers are now - ${headers}")
                .toD("netty-http:${header.BackendDest}")
                .log("The response body was - ${body}")
                .choice()
                .when(simple("${header.CamelHttpResponseCode} == 200"))
                .log(INFO, logger, "Response OK")
                .otherwise()
                .log(WARN, logger, "Response Not OK")
                .end();
    }

    public static void logDetails(final Exchange exchange) {
        final Message message = exchange.getIn();
        logger.info("Message headers: " + message.getHeaders().toString());
        logger.info("Message body: " + message.getBody(String.class));
        String queryparams = message.getHeader("CamelHttpQuery") != null ? message.getHeader("CamelHttpQuery").toString() : "";
        logger.info("Message queryparams: " + queryparams);
    }

}
