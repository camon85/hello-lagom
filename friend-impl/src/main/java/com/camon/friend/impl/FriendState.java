package com.camon.friend.impl;

import com.camon.friend.api.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
public class FriendState implements Jsonable {

    public final Optional<User> user;

    @JsonCreator
    public FriendState(Optional<User> user) {
        this.user = Preconditions.checkNotNull(user, "user");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof FriendState && equalTo((FriendState) another);
    }


    private boolean equalTo(FriendState another) {
        return user.equals(another.user);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + user.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("FriendState").add("user", user).toString();
    }
}
