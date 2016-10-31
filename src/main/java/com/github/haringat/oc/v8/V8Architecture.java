package com.github.haringat.oc.v8;

import com.eclipsesource.v8.*;
import com.github.haringat.LogHelper;
import com.github.haringat.oc.JSArchitecture;
import com.github.haringat.oc.api.IApi;
import com.github.haringat.oc.v8.api.Component;
import com.github.haringat.oc.v8.api.Console;
import com.github.haringat.oc.v8.api.System;
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

    private V8 v8;
    private System system;

    public V8Architecture(Machine machine) {
        super(machine);
    }

    @Override
    public boolean initialize() {
        this.v8 = V8.createV8Runtime("global");
        this.apis.add(new Console(this.v8, this.machine));
        this.apis.add(new Component(this.v8, this.machine));
        this.system = new System(this.v8, this.machine);
        this.apis.add(this.system);
        for(String address: this.machine.components().keySet()) {
            if (this.machine.components().get(address).equals("eeprom")) {
                try {
                    this.v8.executeScript(new String((byte[]) this.machine.invoke(address, "get", new Object[]{})[0]));
                } catch (Exception e) {
                    LogHelper.error(e.getMessage());
                    for (StackTraceElement line: e.getStackTrace()) {
                        LogHelper.error(line);
                    }
                    this.machine.crash(e.getMessage());
                    return false;
                }
            }
        }
        this.initialized = true;
        return this.isInitialized();
    }

    @Override
    public void close() {
        this.v8.terminateExecution();
        super.close();
        this.v8.release();
        this.initialized = false;
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
        for (IApi api: this.apis) {
            if (api instanceof System) {
                ((System) api).fire(signal.name(), args);
            }
        }
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
