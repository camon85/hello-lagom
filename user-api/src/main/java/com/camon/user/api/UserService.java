/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.camon.user.api;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import static com.lightbend.lagom.javadsl.api.Service.*;


public interface UserService extends Service {

    ServiceCall<NotUsed, User> getUser(String userId);

    ServiceCall<User, NotUsed> createUser();

    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named("userservice").withCalls(
                pathCall("/api/users/:userId", this::getUser),
                namedCall("/api/users", this::createUser)
        ).withAutoAcl(true);
        // @formatter:on
    }
}
