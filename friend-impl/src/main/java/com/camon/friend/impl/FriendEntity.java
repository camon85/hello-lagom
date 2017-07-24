package com.camon.friend.impl;

import akka.Done;
import com.camon.friend.api.User;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.camon.friend.impl.FriendCommand.*;
import static com.camon.friend.impl.FriendEvent.*;

public class FriendEntity extends PersistentEntity<FriendCommand, FriendEvent, FriendState> {

    @Override
    public Behavior initialBehavior(Optional<FriendState> snapshotState) {
        BehaviorBuilder b = newBehaviorBuilder(snapshotState.orElse(
                new FriendState(Optional.empty())));

        b.setCommandHandler(CreateUser.class, (cmd, ctx) -> {
            if (state().user.isPresent()) {
                ctx.invalidCommand("User " + entityId() + " is already created");
                return ctx.done();
            } else {
                User user = cmd.user;
                List<FriendEvent> events = new ArrayList<>();
                events.add(new UserCreated(user.userId, user.name));
                return ctx.thenPersistAll(events, () -> ctx.reply(Done.getInstance()));
            }
        });

        b.setEventHandler(UserCreated.class,
                evt -> new FriendState(Optional.of(new User(evt.userId, evt.name))));

        b.setReadOnlyCommandHandler(GetUser.class, (cmd, ctx) -> {
            ctx.reply(new GetUserReply(state().user));
        });

        return b.build();
    }

}
