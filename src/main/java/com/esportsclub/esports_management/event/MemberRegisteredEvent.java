package com.esportsclub.esports_management.event;

import com.esportsclub.esports_management.model.User;
import org.springframework.context.ApplicationEvent;

public class MemberRegisteredEvent extends ApplicationEvent {

    private final User user;

    public MemberRegisteredEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() { return user; }
}
