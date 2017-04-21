package com.github.haringat.openjscomputers.v8.api;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8Function;
import com.github.haringat.openjscomputers.api.IApi;
import com.github.haringat.openjscomputers.v8.eventloop.EventLoop;
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

    protected void addMethod(final String name, final JavaCallback callback) {
        final DirectApi _this = this;
        this.eventLoop.doSynchronized(new Runnable() {
            @Override
            public void run() {
                V8Function v8Function = new V8Function(_this.eventLoop.getV8(), callback);
                _this.functions.put(name, v8Function);
                _this.eventLoop.getV8().add(name, v8Function);
            }
        });
    }

    protected void setupApi() {
        this.functions = new HashMap<String, V8Function>();
    }

    @Override
    public void release() {
        final DirectApi _this = this;
        this.eventLoop.doSynchronized(new Runnable() {
            @Override
            public void run() {
                Iterator<String> it = _this.functions.keySet().iterator();
                while (it.hasNext()) {
                    String name = it.next();
                    _this.functions.get(name).release();
                    _this.eventLoop.getV8().getObject(name).release();
                    it.remove();
                }
            }
        });
    }
}
