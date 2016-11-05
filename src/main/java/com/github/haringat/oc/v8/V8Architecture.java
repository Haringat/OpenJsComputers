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

@V8Architecture.Name("JSV8")
public class V8Architecture extends JSArchitecture {

    private NodeJS node;
    private V8 v8;
    private System system;
    private EventLoop eventLoop;

    public V8Architecture(Machine machine) {
        super(machine);
    }

    @Override
    public boolean initialize() {
        //this.node = NodeJS.createNodeJS();
        //this.v8 = this.node.getRuntime();
        this.v8 = V8.createV8Runtime("global");
        final V8Architecture _this = this;
        this.v8.getLocker().release();
        this.eventLoop = new EventLoop(this.v8);
        synchronized (this.v8) {
            this.v8.getLocker().acquire();
            this.apis.add(new Console(this.eventLoop, this.machine));
            this.apis.add(new Component(this.eventLoop, this.machine));
            this.apis.add(new Timeout(this.eventLoop, this.machine));
            this.system = new System(this.eventLoop, this.machine);
            this.apis.add(this.system);
            this.v8.getLocker().release();
        }
        for(final String address: this.machine.components().keySet()) {
            if (this.machine.components().get(address).equals("eeprom")) {
                V8Array args;
                V8Function function;
                synchronized (this.v8) {
                    this.v8.getLocker().acquire();
                    args = new V8Array(this.v8);
                    function = new V8Function(this.eventLoop.getV8(), new JavaCallback() {
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
                    this.v8.getLocker().release();
                }
                this.eventLoop.execute(new Task(function, null, args));
                synchronized (this.v8) {
                    this.v8.getLocker().acquire();
                    args.release();
                    function.release();
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
            this.v8.terminateExecution();
            super.close();
            this.v8.release();
            //this.node.release();
            this.initialized = false;
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
