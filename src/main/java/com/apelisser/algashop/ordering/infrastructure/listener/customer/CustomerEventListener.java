package com.apelisser.algashop.ordering.infrastructure.listener.customer;

import com.apelisser.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.apelisser.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventListener {

    private static final Logger log = LoggerFactory.getLogger(CustomerEventListener.class);

    @EventListener
    public void listen(CustomerRegisteredEvent event) {
        log.info("Received CustomerRegisteredEvent [listen 01) {}", event);
    }

    @EventListener
    public void listenSecond(CustomerRegisteredEvent event) {
        log.info("Received CustomerRegisteredEvent [listen 02) {}", event);
    }

    @EventListener
    public void listen(CustomerArchivedEvent event) {
        log.info("Received CustomerArchivedEvent [listen 01) {}", event);
    }

}
