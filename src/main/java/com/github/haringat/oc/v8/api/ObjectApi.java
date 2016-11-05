package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;
import com.github.haringat.oc.api.IApi;
import com.github.haringat.oc.v8.eventloop.EventLoop;
import li.cil.oc.api.machine.Machine;

public abstract class ObjectApi implements IApi {

    protected final Machine machine;
    protected final String name;
    protected V8Object api;
    protected EventLoop eventLoop;

    protected ObjectApi(EventLoop eventLoop, String name, Machine machine) {
        this.eventLoop = eventLoop;
        this.machine = machine;
        this.name = name;
        this.setupApi();
    }

    protected void addMethod(String name, JavaCallback callback) {
        this.api.registerJavaMethod(callback, name);
    }

    public void release() {
        this.api.release();
        this.api = null;
    }

    protected void setupApi() {
        this.api = new V8Object(this.eventLoop.getV8());
        this.eventLoop.getV8().add(this.name, this.api);
    }
}
