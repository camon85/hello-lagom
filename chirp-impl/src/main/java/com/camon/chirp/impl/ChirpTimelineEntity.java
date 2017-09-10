package com.camon.chirp.impl;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import javax.inject.Inject;
import java.util.Optional;

class ChirpTimelineEntity extends PersistentEntity<ChirpTimelineCommand, ChirpTimelineEvent, NotUsed> {
    private final ChirpTopic topic;

    @Inject
    ChirpTimelineEntity(ChirpTopic topic) {
        this.topic = topic;
    }

    @Override
    public Behavior initialBehavior(Optional<NotUsed> snapshotState) {
        BehaviorBuilder b = newBehaviorBuilder(NotUsed.getInstance());
        b.setCommandHandler(ChirpTimelineCommand.AddChirp.class, this::addChirp);
        b.setEventHandler(ChirpTimelineEvent.ChirpAdded.class, evt -> state());
        return b.build();
    }

    private Persist addChirp(ChirpTimelineCommand.AddChirp cmd, CommandContext<Done> ctx) {
        return ctx.thenPersist(new ChirpTimelineEvent.ChirpAdded(cmd.chirp), evt -> {
            ctx.reply(Done.getInstance());
            topic.publish(cmd.chirp);
        });
    }
}
