package com.camon.hello.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;

import akka.Done;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.camon.hello.impl.HellolagomEvent.GreetingMessageChanged;

public class HellolagomEntityTest {

  static ActorSystem system;

  @BeforeClass
  public static void setup() {
    system = ActorSystem.create("HellolagomEntityTest");
  }

  @AfterClass
  public static void teardown() {
    JavaTestKit.shutdownActorSystem(system);
    system = null;
  }

  @Test
  public void testHellolagomEntity() {
    PersistentEntityTestDriver<HellolagomCommand, HellolagomEvent, HellolagomState> driver = new PersistentEntityTestDriver<>(system,
        new HellolagomEntity(), "world-1");

    Outcome<HellolagomEvent, HellolagomState> outcome1 = driver.run(new HellolagomCommand.Hello("Alice"));
    assertEquals("Hello, Alice!", outcome1.getReplies().get(0));
    assertEquals(Collections.emptyList(), outcome1.issues());

    Outcome<HellolagomEvent, HellolagomState> outcome2 = driver.run(new HellolagomCommand.UseGreetingMessage("Hi"),
        new HellolagomCommand.Hello("Bob"));
    assertEquals(1, outcome2.events().size());
    assertEquals(new GreetingMessageChanged("world-1", "Hi"), outcome2.events().get(0));
    assertEquals("Hi", outcome2.state().message);
    assertEquals(Done.getInstance(), outcome2.getReplies().get(0));
    assertEquals("Hi, Bob!", outcome2.getReplies().get(1));
    assertEquals(2, outcome2.getReplies().size());
    assertEquals(Collections.emptyList(), outcome2.issues());
  }

}
