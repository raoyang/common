package com.game.solr.elasearch.listener;

import com.game.solr.service.ESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private ESService service;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        service.loadUserAndPut2ES();
    }
}
