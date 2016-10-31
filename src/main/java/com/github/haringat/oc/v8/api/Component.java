package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.*;
import com.github.haringat.LogHelper;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Machine;
import li.cil.oc.api.network.Node;

import static com.eclipsesource.v8.utils.V8ObjectUtils.*;

import java.lang.*;
import java.util.*;

public class Component extends ApiBase {

    public Component(V8 v8, Machine machine) {
        super(v8, "component", machine);
    }

    @Override
    protected void setupApi() {
        super.setupApi();
        final Component _this = this;
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                if (parameters.length() < 2 || parameters.getType(0) != V8Value.STRING || parameters.getType(1) != V8Value.STRING) {
                    throw new IllegalArgumentException("component.invoke(string, string, ...string): Array<any>");
                }
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
                    LogHelper.error(e.getMessage());
                    for (StackTraceElement line: e.getStackTrace()) {
                        LogHelper.error(line);
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
                if (parameters.length() < 1 || parameters.getType(0) != V8Value.STRING) {
                    throw new IllegalArgumentException("component.getPrimary(string): {[key: string]: string}");
                }
                Map<String, String> components = _this.machine.components();
                for(String address: components.keySet()) {
                    if (components.get(address).equals(parameters.getString(0))) {
                        return _this.proxy(address);
                    }
                }
                return null;
            }
        }, "getPrimary");
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                if (parameters.length() < 1 || parameters.getType(0) != V8Value.STRING) {
                    throw new IllegalArgumentException("component.proxy(string): Object");
                }
                return _this.proxy(parameters.getString(0));
            }
        }, "proxy");
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                if (parameters.length() < 1 || parameters.getType(0) != V8Value.STRING) {
                    throw new IllegalArgumentException("Component.methods(string):Array<string>");
                }
                Map<String, Callback> methods = _this.methods(parameters.getString(0));
                if (methods != null) {
                    return toV8Array(_this.v8, new ArrayList<String>(methods.keySet()));
                } else {
                    return null;
                }
            }
        }, "methods");
    }

    private V8Object proxy(final String address) {
        final Component _this = this;
        V8Object proxy = new V8Object(this.v8);
        Map<String, Callback> methods = this.methods(address);
        if (methods == null) {
            return null;
        } else {
            for (final String key: methods.keySet()) {
                if (!methods.get(key).getter() && !methods.get(key).setter()) {
                    proxy.registerJavaMethod(new JavaCallback() {
                        @Override
                        public Object invoke(V8Object receiver, V8Array parameters) {
                            List<Object> javaParameters = toList(parameters);
                            try {
                                Object[] results = _this.machine.invoke(address, key, javaParameters.toArray());
                                List<Object> resultList = new ArrayList<Object>();
                                Collections.addAll(resultList, results);
                                return toV8Array(_this.v8, resultList);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                    }, key);
                } else {
                    // TODO:implement getters/setters
                    LogHelper.info("OpenJsComputers does not support getters and setters yet. Go and annoy the mod author until he finally implements them.");
                }
            }
            proxy.add("address", address);
            return proxy;
        }
    }

    private Map<String, Callback> methods(String address) {
        for (Node node: this.machine.node().reachableNodes()) {
            if (node.address().equals(address) && this.machine.node().canBeReachedFrom(node)) {
                return this.machine.methods(node.host());
            }
        }
        return null;
    }
}
