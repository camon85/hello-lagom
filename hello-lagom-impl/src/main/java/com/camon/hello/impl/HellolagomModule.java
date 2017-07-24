/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.camon.hello.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.camon.hello.api.HellolagomService;

/**
 * The module that binds the HellolagomService so that it can be served.
 */
public class HellolagomModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    bindService(HellolagomService.class, HellolagomServiceImpl.class);
  }
}
