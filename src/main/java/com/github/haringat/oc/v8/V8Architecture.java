package com.github.haringat.oc.v8;

import com.eclipsesource.v8.*;
import com.github.haringat.OpenJsComputers;
import com.github.haringat.oc.JSArchitecture;
import com.github.haringat.oc.api.IApi;
import com.github.haringat.oc.v8.api.Component;
import com.github.haringat.oc.v8.api.Console;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.machine.ExecutionResult;
import li.cil.oc.api.machine.Signal;
import net.minecraft.nbt.NBTTagCompound;

@V8Architecture.Name("JSV8")
public class V8Architecture extends JSArchitecture {

    private V8 v8;

    public V8Architecture(Machine machine) {
        super(machine);
    }

    @Override
    public boolean initialize() {
        this.v8 = V8.createV8Runtime();
        this.apis.add(new Console(this.v8));
        this.apis.add(new Component(this.v8));
        this.v8.executeVoidScript("console.log(\"Hallo Welt from wrapped native API!\");");

        /*V8Object component = new V8Object(this.v8);
        component.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                return null;
            }
        }, "invoke");
        V8Function component_invoke = new V8Function(this.v8, new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                return null;
            }
        });*/
        this.initialized = true;
        return this.isInitialized();
    }

    @Override
    public void close() {
        this.v8.terminateExecution();
        super.close();
        this.v8.release();
    }

    @Override
    public void runSynchronized() {
    }

    @Override
    public ExecutionResult runThreaded(boolean isSynchronizedReturn) {
        try {
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
        }
    }

    @Override
    public void onSignal() {
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void load(NBTTagCompound nbt) {
        OpenJsComputers.logger.info(nbt.toString());
    }

    @Override
    public void save(NBTTagCompound nbt) {
        OpenJsComputers.logger.info(nbt.toString());
    }
}
