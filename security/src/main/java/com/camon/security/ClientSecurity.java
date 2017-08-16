package com.camon.security;

import com.lightbend.lagom.javadsl.api.security.ServicePrincipal;
import com.lightbend.lagom.javadsl.api.transport.RequestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class ClientSecurity {

  private static final Logger log = LoggerFactory.getLogger(ClientSecurity.class);

  public static final Function<RequestHeader, RequestHeader> authenticate(UUID userId) {
    log.info("# client authenticate, {}", userId);
    return request -> {
      Optional<ServicePrincipal> service = request.principal()
          .filter(p -> p instanceof ServicePrincipal)
          .map(p -> (ServicePrincipal) p);

      return request.withPrincipal(UserPrincipal.of(userId, service));
    };
  }
}
