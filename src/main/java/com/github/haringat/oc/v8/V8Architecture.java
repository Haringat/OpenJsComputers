package com.github.haringat.oc.v8;

import com.eclipsesource.v8.*;
import com.github.haringat.OpenJsComputers;
import com.github.haringat.oc.JSArchitecture;
import com.github.haringat.oc.v8.api.Component;
import com.github.haringat.oc.v8.api.Console;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.machine.ExecutionResult;
import li.cil.oc.api.machine.Signal;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.Sys;
import static com.eclipsesource.v8.utils.V8ObjectUtils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@V8Architecture.Name("JSV8")
public class V8Architecture extends JSArchitecture {

    private V8 v8;

    public V8Architecture(Machine machine) {
        super(machine);
    }

    @Override
    public boolean initialize() {
        this.v8 = V8.createV8Runtime("global");
        this.apis.add(new Console(this.v8, this.machine));
        this.apis.add(new Component(this.v8, this.machine));
        for(String address: this.machine.components().keySet()) {
            if (this.machine.components().get(address).equals("eeprom")) {
                try {
                    this.v8.executeScript(new String((byte[]) this.machine.invoke(address, "get", new Object[]{})[0]));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    for (StackTraceElement line: e.getStackTrace()) {
                        System.out.println(line);
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
        // basically we do not need this as we go completely reactive once we set ourselves up in {@link #initialize()}
    }

    @Override
    public ExecutionResult runThreaded(boolean isSynchronizedReturn) {
        // js is purely single-threaded so we do not need this
        return new ExecutionResult.SynchronizedCall();
        /*try {
            final Signal signal;
            if (isSynchronizedReturn) {
                signal = null;
            } else {
                signal = this.machine.popSignal();
            }
            if (signal != null) {
                OpenJsComputers.logger.info("Got signal: " + signal.name());
                return new ExecutionResult.SynchronizedCall();
            } else {
                return new ExecutionResult.Sleep(1);
            }
        } catch (Throwable throwable) {
            return new ExecutionResult.Error(throwable.getMessage());
        }*/
    }

    @Override
    public void onSignal() {
        final Signal signal = this.machine.popSignal();
        List<Object> argList = new ArrayList<Object>();
        Collections.addAll(argList, signal.args());
        V8Array args = toV8Array(this.v8, argList);
        V8Object system = this.v8.getObject("system");
        system.executeJSFunction("on", signal.name(), args);
        args.release();
        system.release();
    }

    @Override
    public void onConnect() {
        System.out.println("onConnect called");
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
