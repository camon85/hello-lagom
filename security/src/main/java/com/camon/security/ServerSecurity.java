package com.camon.security;

import com.lightbend.lagom.javadsl.api.transport.Forbidden;
import com.lightbend.lagom.javadsl.server.HeaderServiceCall;
import com.lightbend.lagom.javadsl.server.ServerServiceCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.UUID;
import java.util.function.Function;

public class ServerSecurity {

  private static final Logger log = LoggerFactory.getLogger(ServerSecurity.class);

  public static <Request, Response> ServerServiceCall<Request, Response> authenticated(
      Function<UUID, ? extends ServerServiceCall<Request, Response>> serviceCall) {
    log.info("# server authenticate");
    return HeaderServiceCall.compose(requestHeader -> {
      if (requestHeader.principal().isPresent()) {
        Principal principal = requestHeader.principal().get();
        if (principal instanceof UserPrincipal) {
          UUID userId = ((UserPrincipal) principal).getUserId();
          log.info("# userId: {}", userId);
          return serviceCall.apply(userId);
        }
      }

      return req -> {
        throw new Forbidden("User not authenticated");
      };
    });
  }

}
