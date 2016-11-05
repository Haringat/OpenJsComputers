package com.github.haringat.oc.v8.eventloop;

import java.util.Timer;

class ScheduledTask {
    private Task task;
    private Timer timer;
    boolean cancelled = false;

    ScheduledTask(Task task, Timer timer) {
        this.task = task;
        this.timer = timer;
    }

    boolean cancel() {
        if (!this.cancelled) {
            this.timer.cancel();
            this.task.cleanUp();
            this.cancelled = true;
            return true;
        } else {
            return false;
        }
    }
}
