package com.github.haringat.oc.v8;

import com.eclipsesource.v8.*;
import com.github.haringat.LogHelper;
import com.github.haringat.oc.JSArchitecture;
import com.github.haringat.oc.api.IApi;
import com.github.haringat.oc.v8.api.Component;
import com.github.haringat.oc.v8.api.Console;
import com.github.haringat.oc.v8.api.System;
import com.github.haringat.oc.v8.api.Timeout;
import com.github.haringat.oc.v8.eventloop.EventLoop;
import com.github.haringat.oc.v8.eventloop.Task;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.machine.ExecutionResult;
import li.cil.oc.api.machine.Signal;
import net.minecraft.nbt.NBTTagCompound;
import static com.eclipsesource.v8.utils.V8ObjectUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("SynchronizeOnNonFinalField")
@V8Architecture.Name("JSV8")
public class V8Architecture extends JSArchitecture {

    private V8 v8;
    private System system;
    private EventLoop eventLoop;

    public V8Architecture(Machine machine) {
        super(machine);
    }

    @Override
    public boolean initialize() {
        this.v8 = V8.createV8Runtime("global");
        final V8Architecture _this = this;
        this.eventLoop = new EventLoop(this.v8);
                _this.apis.add(new Console(_this.eventLoop, _this.machine));
                _this.apis.add(new Component(_this.eventLoop, _this.machine));
                _this.apis.add(new Timeout(_this.eventLoop, _this.machine));
                _this.system = new System(_this.eventLoop, _this.machine);
                _this.apis.add(_this.system);
        for(final String address: this.machine.components().keySet()) {
            if (this.machine.components().get(address).equals("eeprom")) {
                final V8Array[] args = new V8Array[1];
                final V8Function[] function = new V8Function[1];
                this.eventLoop.doSynchronized(new Runnable() {
                    @Override
                    public void run() {
                        args[0] = new V8Array(_this.v8);
                        function[0] = new V8Function(_this.v8, new JavaCallback() {
                            @Override
                            public Object invoke(V8Object receiver, V8Array parameters) {
                                try {
                                    _this.v8.executeScript(new String((byte[]) _this.machine.invoke(address, "get", new Object[]{})[0]));
                                    return null;
                                } catch(Exception e) {
                                    LogHelper.error(e.getMessage());
                                    for (StackTraceElement line: e.getStackTrace()) {
                                        LogHelper.error(line);
                                    }
                                    _this.machine.crash(e.getMessage());
                                    return false;
                                }
                            }
                        });
                    }
                });
                this.eventLoop.execute(new Task(function[0], null, args[0]));
                synchronized (this.v8) {
                    this.v8.getLocker().acquire();
                    args[0].release();
                    function[0].release();
                    this.v8.getLocker().release();
                }
            }
        }
        this.initialized = true;
        return this.isInitialized();
    }

    @Override
    public void close() {
        synchronized (this.v8) {
            this.v8.getLocker().acquire();
            this.eventLoop.shutDown();
            this.eventLoop = null;
            super.close();
            //this.node.release();
            this.initialized = false;
        }
        synchronized (this.v8) {
            this.v8.getLocker().acquire();
            this.v8.release();
            this.v8 = null;
        }
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
        final Signal signal = this.machine.popSignal();
        LogHelper.all("Caught signal: " + signal.name());
        List<Object> argList = new ArrayList<Object>();
        Collections.addAll(argList, signal.args());
        V8Array args = toV8Array(this.v8, argList);
        this.system.fire(signal.name(), args);
        args.release();
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void load(NBTTagCompound nbt) {
        //this.machine.load(nbt);
    }

    @Override
    public void save(NBTTagCompound nbt) {
        //this.machine.save(nbt);
    }
}
