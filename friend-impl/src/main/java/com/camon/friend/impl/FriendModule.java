package com.camon.friend.impl;

import com.camon.friend.api.FriendService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class FriendModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        bindService(FriendService.class, FriendServiceImpl.class);
    }

}
