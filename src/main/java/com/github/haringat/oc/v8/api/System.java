package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.*;

import static com.eclipsesource.v8.utils.V8ObjectUtils.*;

import li.cil.oc.api.machine.Machine;

public class System extends ApiBase {

    private V8Object listeners;

    public System(V8 v8, Machine machine) {
        super(v8, "system", machine);
    }

    @Override
    public void setupApi() {
        super.setupApi();
        final System _this = this;
        this.listeners = new V8Array(this.v8);
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                return getV8Result(_this.v8, _this.machine.crash(parameters.getString(0)));
            }
        }, "crash");
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                String eventName = parameters.getString(0);
                if (!_this.listeners.contains(eventName)) {
                    V8Array listeners = new V8Array(_this.v8);
                    _this.listeners.add(eventName, listeners);
                    listeners.release();
                }
                V8Array listeners = ((V8Array) _this.listeners.getObject(eventName));
                V8Function listener = (V8Function) parameters.getObject(1);
                listeners.push(listener);
                listener.release();
                listeners.release();
                return null;
            }
        }, "on");
    }
}
