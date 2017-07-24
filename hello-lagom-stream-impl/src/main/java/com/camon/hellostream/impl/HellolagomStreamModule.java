/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.camon.hellostream.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.camon.hello.api.HellolagomService;
import com.camon.hellostream.api.HellolagomStreamService;

/**
 * The module that binds the HellolagomStreamService so that it can be served.
 */
public class HellolagomStreamModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    // Bind the HellolagomStreamService service
    bindService(HellolagomStreamService.class, HellolagomStreamServiceImpl.class);
    // Bind the HellolagomService client
    bindClient(HellolagomService.class);
    // Bind the subscriber eagerly to ensure it starts up
    bind(HellolagomStreamSubscriber.class).asEagerSingleton();
  }
}
