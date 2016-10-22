package com.github.haringat.oc;

import com.github.haringat.oc.api.IApi;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.driver.item.Memory;
import li.cil.oc.api.machine.Architecture;
import li.cil.oc.api.machine.Machine;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class JSArchitecture implements Architecture {

    protected final Machine machine;
    protected double memory = 0;
    protected List<IApi> apis = new ArrayList<IApi>();
    protected boolean initialized = false;

    public JSArchitecture(Machine machine) {
        this.machine = machine;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public boolean recomputeMemory(Iterable<ItemStack> components) {
        this.memory = 0;
        for (ItemStack component: components) {
            Item driver = Driver.driverFor(component);
            if (driver instanceof Memory) {
                this.memory += ((Memory) driver).amount(component);
            }
        }
        return this.memory > 0;
    }

    @Override
    public void close() {
        for (IApi api: this.apis) {
            api.release();
        }
    }
}
