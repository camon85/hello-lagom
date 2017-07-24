package com.camon.friend.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.time.Instant;
import java.util.Optional;

public interface FriendEvent extends Jsonable, AggregateEvent<FriendEvent> {

    @Override
    default AggregateEventTag<FriendEvent> aggregateTag() {
        return FriendEventTag.INSTANCE;
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    class UserCreated implements FriendEvent {
        public final String userId;
        public final String name;
        public final Instant timestamp;

        public UserCreated(String userId, String name) {
            this(userId, name, Optional.empty());
        }

        @JsonCreator
        private UserCreated(String userId, String name, Optional<Instant> timestamp) {
            this.userId = Preconditions.checkNotNull(userId, "userId");
            this.name = Preconditions.checkNotNull(name, "name");
            this.timestamp = timestamp.orElseGet(() -> Instant.now());
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof UserCreated && equalTo((UserCreated) another);
        }

        private boolean equalTo(UserCreated another) {
            return userId.equals(another.userId) && name.equals(another.name) && timestamp.equals(another.timestamp);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + userId.hashCode();
            h = h * 17 + name.hashCode();
            h = h * 17 + timestamp.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("UserCreated").add("userId", userId).add("name", name)
                    .add("timestamp", timestamp).toString();
        }
    }

}
