package com.github.haringat.openjscomputers.v8;

import com.eclipsesource.v8.*;
import com.github.haringat.LogHelper;
import com.github.haringat.openjscomputers.JSArchitecture;
import com.github.haringat.openjscomputers.v8.api.*;
import com.github.haringat.openjscomputers.v8.api.System;
import com.github.haringat.openjscomputers.v8.eventloop.EventLoop;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.machine.ExecutionResult;
import li.cil.oc.api.machine.Signal;
import net.minecraft.nbt.NBTTagCompound;
import static com.eclipsesource.v8.utils.V8ObjectUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"SynchronizeOnNonFinalField"})
@V8Architecture.Name("JSV8")
public class V8Architecture extends JSArchitecture {

    private System system;
    private EventLoop eventLoop;

    public V8Architecture(Machine machine) {
        super(machine);
    }

    @Override
    public boolean initialize() {
        final V8Architecture _this = this;
        this.eventLoop = new EventLoop(V8.createV8Runtime("global"));
        this.apis.add(new Console(this.eventLoop, this.machine));
        this.apis.add(new Component(this.eventLoop, this.machine));
        this.apis.add(new Timeout(this.eventLoop, this.machine));
        this.apis.add(new JSON(this.eventLoop, this.machine));
        this.system = new System(this.eventLoop, this.machine);
        this.apis.add(this.system);
        for(final String address: this.machine.components().keySet()) {
            if (this.machine.components().get(address).equals("eeprom")) {
                this.eventLoop.evalAsync(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            _this.eventLoop.getV8().executeScript(new String((byte[]) _this.machine.invoke(address, "get", new Object[]{})[0]));
                        } catch(Exception e) {
                            LogHelper.error(e.getMessage());
                            for (StackTraceElement line: e.getStackTrace()) {
                                LogHelper.error(line);
                            }
                            _this.machine.crash(e.getMessage());
                        }
                    }
                });
            }
        }
        this.initialized = true;
        return this.isInitialized();
    }

    @Override
    public void close() {
        this.eventLoop.shutDown();
        this.eventLoop = null;
    }

    @Override
    public void runSynchronized() {
        // basically we do not need this as we go reactive once we set ourselves up in initialize()
    }

    @Override
    public ExecutionResult runThreaded(boolean isSynchronizedReturn) {
        // dummy because we handle signals in onSignal()
        return new ExecutionResult.SynchronizedCall();
    }

    @Override
    public void onSignal() {
        synchronized (this.eventLoop.getV8()) {
            this.eventLoop.getV8().getLocker().acquire();
            final Signal signal = this.machine.popSignal();
            LogHelper.all("Caught signal: " + signal.name());
            List<Object> argList = new ArrayList<Object>();
            Collections.addAll(argList, signal.args());
            V8Array args = toV8Array(this.eventLoop.getV8(), argList);
            this.system.fire(signal.name(), args);
            args.release();
            this.eventLoop.getV8().getLocker().release();
        }
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void load(NBTTagCompound nbt) {
        this.eventLoop.load(nbt);
    }

    @Override
    public void save(NBTTagCompound nbt) {
        this.eventLoop.save(nbt);
    }
}
