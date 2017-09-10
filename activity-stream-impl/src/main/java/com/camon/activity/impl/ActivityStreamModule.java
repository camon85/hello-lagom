package com.camon.activity.impl;

import com.camon.chirp.api.ChirpService;

import com.camon.friend.api.FriendService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.camon.activity.api.ActivityStreamService;

public class ActivityStreamModule extends AbstractModule implements ServiceGuiceSupport {

  @Override
  protected void configure() {
    bindService(ActivityStreamService.class, ActivityStreamServiceImpl.class);
    bindClient(FriendService.class);
    bindClient(ChirpService.class);
  }
}
