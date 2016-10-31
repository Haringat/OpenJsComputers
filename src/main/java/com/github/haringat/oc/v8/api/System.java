package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.*;

import static com.eclipsesource.v8.utils.V8ObjectUtils.*;

import li.cil.oc.api.machine.Machine;

import java.util.*;

public class System extends ApiBase {

    private Map<String, List<V8Function>> listeners;

    public System(V8 v8, Machine machine) {
        super(v8, "system", machine);
    }

    @Override
    public void setupApi() {
        super.setupApi();
        final System _this = this;
        this.listeners = new HashMap<String, List<V8Function>>();
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                return getV8Result(_this.v8, _this.machine.crash(parameters.getString(0)));
            }
        }, "crash");
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                if (parameters.length() < 2 || parameters.getType(0) != V8Value.STRING || parameters.getType(1) != V8Value.V8_FUNCTION) {
                    return null;
                }
                String eventName = parameters.getString(0);
                if (!_this.listeners.keySet().contains(eventName)) {
                    _this.listeners.put(eventName, new ArrayList<V8Function>());
                }
                List<V8Function> listeners = _this.listeners.get(eventName);
                V8Function listener = (V8Function) parameters.getObject(1);
                listeners.add(listener);
                listener.release();
                return null;
            }
        }, "on");
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                if (parameters.length() == 0 || parameters.getType(0) != V8Value.STRING) {
                    return null;
                }
                Object[] params = toList(parameters).toArray();
                _this.machine.signal(parameters.getString(0), params);
                return null;
            }
        }, "fire");
    }

    public void fire(String eventName, V8Array args) {
        for (String name: this.listeners.keySet()) {
            if (name.equals(eventName)) {
                for (V8Function listener: this.listeners.get(name)) {
                    listener.call(null, args);
                }
                break;
            }
        }
    }
}
