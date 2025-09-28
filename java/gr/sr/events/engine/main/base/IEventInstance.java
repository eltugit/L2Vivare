package gr.sr.events.engine.main.base;

import gr.sr.events.engine.main.events.AbstractMainEvent;
import gr.sr.interf.delegate.InstanceData;

import java.util.concurrent.ScheduledFuture;

public interface IEventInstance {
    InstanceData getInstance();

    ScheduledFuture<?> scheduleNextTask(int paramInt);

    AbstractMainEvent.Clock getClock();

    boolean isActive();
}


