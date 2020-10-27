package com.github.meklund;

import org.springframework.stereotype.Component;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A bean that extracts values from the incoming header.
 * <p/>
 * Uses <tt>@Component("myBean")</tt> to register this bean with the name <tt>myBean</tt>
 * that we use in the Camel route to lookup this bean.
 */
@Component("DestinationBean")
public class DestinationBean {

    protected static final Logger logger = LoggerFactory.getLogger(DestinationBean.class);

    public String getDestination(Exchange exchange) {
        String backendDestination = "";

        final Message message = exchange.getIn();
        try {
            String scheme = message.getHeader(Exchange.HTTP_SCHEME) != null ? message.getHeader(Exchange.HTTP_SCHEME).toString() : "http";
            String port = message.getHeader(Exchange.HTTP_PORT) != null ? message.getHeader(Exchange.HTTP_PORT).toString() : "80";
            backendDestination =  scheme + "://" + message.getHeader("Host") + ":" + port + "/" + message.getHeader(Exchange.HTTP_PATH);
            logger.info("Backend destination: " + backendDestination);

        } catch(Exception e) {
            logger.error("Error when extracting backen-service url",e);
        }
        return backendDestination;
    }
}

