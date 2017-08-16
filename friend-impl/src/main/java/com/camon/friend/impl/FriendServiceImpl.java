package com.camon.friend.impl;

import akka.NotUsed;
import com.camon.friend.api.FriendService;
import com.camon.friend.api.User;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class FriendServiceImpl implements FriendService {

    private final Logger log = LoggerFactory.getLogger(FriendServiceImpl.class);

    private final PersistentEntityRegistry persistentEntities;
    private final CassandraSession db;

    @Inject
    public FriendServiceImpl(PersistentEntityRegistry persistentEntities, CassandraSession db) {
        this.persistentEntities = persistentEntities;
        this.db = db;

        persistentEntities.register(FriendEntity.class);
    }

    @Override
    public ServiceCall<NotUsed, User> getUser(String userId) {
        log.info("# getUser");
        return request ->
            friendEntityRef(userId).ask(new FriendCommand.GetUser()).thenApply(reply -> {
                if (reply.user.isPresent()) {
                    return reply.user.get();
                } else {
                    throw new NotFound("user " + userId + " not found");
                }
            });
    }

    @Override
    public ServiceCall<User, NotUsed> createUser() {
        log.info("# createUser");
        return request -> friendEntityRef(request.userId).ask(new FriendCommand.CreateUser(request))
                .thenApply(ack -> NotUsed.getInstance());
    }

    private PersistentEntityRef<FriendCommand> friendEntityRef(String userId) {
        return persistentEntities.refFor(FriendEntity.class, userId);
    }
}
