package com.esportsclub.esports_management.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MemberRegisteredListener {

    @EventListener
    public void onMemberRegistered(MemberRegisteredEvent event) {
        System.out.println("[Observer] New member registered: "
                + event.getUser().getUsername());
        System.out.println("[Observer] Role: "
                + event.getUser().getRole());
    }
}
