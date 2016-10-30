package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.*;
import com.github.haringat.OpenJsComputers;
import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.Item;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Machine;
import net.minecraft.item.ItemStack;

import static com.eclipsesource.v8.utils.V8ObjectUtils.*;

import java.lang.*;
import java.lang.System;
import java.util.*;

public class Component extends ApiBase {

    private Map<String, V8Object> primaries;

    public Component(V8 v8, Machine machine) {
        super(v8, "component", machine);
        this.refreshPrimaries();
    }

    public void refreshPrimaries() {
        Map<String, String> componentList = this.machine.components();
        // remove all primaries from the list which are not accessible anymore
        Iterator<String> typeIterator = this.primaries.keySet().iterator();
        while (typeIterator.hasNext()) {
            String type = typeIterator.next();
            if (!componentList.values().contains(this.primaries.get(type).getString("address"))) {
                typeIterator.remove();
            }
        }
        // add all types which are accessible but do not have a primary in the list
        for (String address : componentList.keySet()) {
            if (!this.primaries.keySet().contains(componentList.get(address))) {
                this.primaries.put(componentList.get(address), this.proxy(address));
            }
        }
    }

    @Override
    protected void setupApi() {
        super.setupApi();
        this.primaries = new HashMap<String, V8Object>();
        final Component _this = this;
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                Object[] varargs = new Object[parameters.length() - 2];
                for (int i = 2; i < parameters.length(); i++) {
                    varargs[i - 2] = parameters.get(i);
                }
                try {
                    Object[] results = _this.machine.invoke(parameters.getString(0), parameters.getString(1), varargs);
                    List<Object> resultList = new ArrayList<Object>(results.length);
                    Collections.addAll(resultList, results);
                    return toV8Array(_this.v8, resultList);
                } catch (Exception e) {
                    OpenJsComputers.logger.error(e.getMessage());
                    for (StackTraceElement line: e.getStackTrace()) {
                        OpenJsComputers.logger.error(line);
                    }
                    _this.machine.crash(e.getMessage());
                }
                return null;
            }
        }, "invoke");
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                return toV8Object(_this.v8, _this.machine.components());
            }
        }, "list");
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                final String address;
                if (parameters.length() > 0 && parameters.getType(0) == V8Value.STRING) {
                    address = parameters.getString(0);
                } else {
                    return null;
                }
                return _this.proxy(address);
            }
        }, "proxy");
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                if (parameters.length() == 0 || parameters.getType(0) != V8Value.STRING) {
                    return null;
                }
                return _this.primaries.get(parameters.getString(0));
            }
        }, "getPrimary");
        this.refreshPrimaries();
    }

    private V8Object proxy(final String address) {
        final Component _this = this;
        System.out.println("proxying address " + address);
        V8Object proxy = new V8Object(this.v8);
        for (ItemStack itemStack : this.machine.host().internalComponents()) {
            Item itemDriver = Driver.driverFor(itemStack);
        }
        Map<String, Callback> methods = this.machine.methods();
        for (final String key: methods.keySet()) {
            System.out.println("has method " + key);
            if (!methods.get(key).getter() && !methods.get(key).setter()) {
                System.out.println("is neither a setter nor a getter");
                proxy.registerJavaMethod(new JavaCallback() {
                    @Override
                    public Object invoke(V8Object receiver, V8Array parameters) {
                        List<Object> javaParameters = toList(parameters);
                        try {
                            return getV8Result(_this.v8, _this.machine.invoke(address, key, javaParameters.toArray()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }, key);
            } else {
                // TODO:implement getters/setters
            }
        }
        proxy.add("address", address);
        return proxy;
    }

    @Override
    public void release() {
        super.release();
        for(V8Object primary: this.primaries.values()) {
            if (!primary.isReleased()) {
                primary.release();
            }
        }
    }
}
