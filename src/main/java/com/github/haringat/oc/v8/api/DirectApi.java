package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Function;
import com.github.haringat.oc.api.IApi;
import com.github.haringat.oc.v8.eventloop.EventLoop;
import li.cil.oc.api.machine.Machine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class DirectApi implements IApi {

    protected EventLoop eventLoop;
    protected Map<String, V8Function> functions;
    protected Machine machine;

    protected DirectApi(EventLoop eventLoop, Machine machine) {
        this.eventLoop = eventLoop;
        this.machine = machine;
        this.setupApi();
    }

    protected void addMethod(String name, JavaCallback callback) {
        V8Function v8Function = new V8Function(this.eventLoop.getV8(), callback);
        this.functions.put(name, v8Function);
        this.eventLoop.getV8().add(name, v8Function);
    }

    protected void setupApi() {
        this.functions = new HashMap<String, V8Function>();
    }

    @Override
    public void release() {
        Iterator<String> it = this.functions.keySet().iterator();
        while (it.hasNext()) {
            String name = it.next();
            this.functions.get(name).release();
            this.eventLoop.getV8().getObject(name).release();
            it.remove();
        }
    }
}
