package com.github.haringat.oc.v8.eventloop;

import com.eclipsesource.v8.*;
import com.eclipsesource.v8.utils.MemoryManager;
import li.cil.oc.api.Persistable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.*;

public class EventLoop implements Persistable {

    private final List<Task> tasks = new ArrayList<Task>();
    private final List<ScheduledTask> scheduledTasks = new ArrayList<ScheduledTask>();
    private boolean active = true;
    private final V8 v8;
    private Thread v8Thread;
    private MemoryManager memoryManager;

    public EventLoop(V8 v8) {
        this.v8 = v8;
        this.memoryManager = new MemoryManager(this.v8);
        final EventLoop _this = this;
        this.v8Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (_this.active) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        this.processTasks();
                    }
                }
            }

            private void processTasks() {
                synchronized (_this.tasks) {
                    synchronized (_this.v8) {
                        if (!_this.active) {
                            return;
                        }
                        V8Locker locker = _this.v8.getLocker();
                        locker.acquire();
                        Iterator<Task> taskIterator = _this.tasks.iterator();
                        while(taskIterator.hasNext()) {
                            Task task = taskIterator.next();
                            task.execute();
                            taskIterator.remove();
                        }
                        locker.release();
                    }
                }
            }
        });
        V8Locker locker = this.v8.getLocker();
        if (locker.hasLock()) {
            locker.release();
        }
        this.v8Thread.start();
    }

    public void doSynchronized(Runnable task) {
        synchronized (this.v8) {
            V8Locker locker = this.v8.getLocker();
            locker.acquire();
            task.run();
            locker.release();
        }
    }

    public void evalAsync(final Runnable task) {
        synchronized (this.v8) {
            this.schedule(new Task(new V8Function(this.v8, new JavaCallback() {
                @Override
                public Object invoke(V8Object receiver, V8Array parameters) {
                    task.run();
                    return null;
                }
            }), null, new V8Array(this.v8)));
            this.v8Thread.interrupt();
        }
    }

    public int schedule(final Task task) {
        return this.schedule(task, 0);
    }

    public int schedule(final Task task, long timeout) {
        final EventLoop _this = this;
        Timer timer = new Timer();
        this.scheduledTasks.add(new ScheduledTask(task, timer));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (_this.tasks) {
                    _this.tasks.add(task);
                    _this.v8Thread.interrupt();
                }
            }
        }, timeout);
        return this.scheduledTasks.size() - 1;
    }

    public int scheduleRepetitive(final Task task, long timeout) {
        final EventLoop _this = this;
        Timer timer = new Timer();
        this.scheduledTasks.add(new ScheduledTask(task, timer));
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (_this.tasks) {
                    _this.tasks.add(task);
                    _this.v8Thread.interrupt();
                }
            }
        }, timeout, timeout);
        return this.scheduledTasks.size() - 1;
    }

    public boolean cancelScheduledTask(int handle) {
        return this.scheduledTasks.get(handle).cancel();
    }

    public Object execute(Task task) {
        Object result;
        synchronized (this.v8) {
            this.v8.getLocker().acquire();
            result = task.execute();
            this.v8.getLocker().release();
        }
        return result;
    }

    public void shutDown() {
        synchronized (this.v8) {
            this.active = false;
            for (ScheduledTask task: this.scheduledTasks) {
                task.cancel();
            }
            this.memoryManager.release();
        }
    }

    public V8 getV8() {
        return this.v8;
    }

    @Override
    public void load(NBTTagCompound nbt) {
    }

    @Override
    public void save(NBTTagCompound nbt) {
    }
}
