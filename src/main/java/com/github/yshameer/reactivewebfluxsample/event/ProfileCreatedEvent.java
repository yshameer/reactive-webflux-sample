package com.github.yshameer.reactivewebfluxsample.event;

import com.github.yshameer.reactivewebfluxsample.entity.Profile;
import org.springframework.context.ApplicationEvent;

public class ProfileCreatedEvent extends ApplicationEvent {

    public ProfileCreatedEvent(Profile source) {
        super(source);
    }
}
