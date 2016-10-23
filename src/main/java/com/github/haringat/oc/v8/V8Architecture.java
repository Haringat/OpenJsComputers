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
        this.apis.add(new Component(this.v8, this.machine));
        //this.v8.executeVoidScript("console.log(\"Hallo Welt from wrapped native API!\");");
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
        //this.v8.executeScript("(() => {component.invoke(\"eeprom\", );})();");
        /*try {
            this.v8.executeScript((String) this.machine.invoke("eeprom", "get", new Object[]{})[0]);
        } catch (Exception e) {
            this.machine.crash("no bios found");
            return false;
        }*/
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
        //this.machine.load(nbt);
    }

    @Override
    public void save(NBTTagCompound nbt) {
        //this.machine.save(nbt);
    }
}
