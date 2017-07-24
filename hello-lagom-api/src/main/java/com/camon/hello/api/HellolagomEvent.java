/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.camon.hello.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Preconditions;
import lombok.Value;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = HellolagomEvent.GreetingMessageChanged.class, name = "greeting-message-changed")
})
public interface HellolagomEvent {

  String getName();

  @Value
  final class GreetingMessageChanged implements HellolagomEvent {
    public final String name;
    public final String message;

    @JsonCreator
    public GreetingMessageChanged(String name, String message) {
      this.name = Preconditions.checkNotNull(name, "name");
      this.message = Preconditions.checkNotNull(message, "message");
    }
  }
}
