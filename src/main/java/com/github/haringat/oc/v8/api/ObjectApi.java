package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.*;
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

    protected void addMethod(final String name, final JavaCallback callback) {
        final ObjectApi _this = this;
        this.eventLoop.doSynchronized(new Runnable() {
            @Override
            public void run() {
                _this.api.registerJavaMethod(callback, name);
            }
        });
    }

    protected void addMethod(final String name, final JavaVoidCallback callback) {
        final ObjectApi _this = this;
        this.eventLoop.doSynchronized(new Runnable() {
            @Override
            public void run() {
                _this.api.registerJavaMethod(callback, name);
            }
        });
    }

    public void release() {
        final ObjectApi _this = this;
        this.eventLoop.doSynchronized(new Runnable() {
            @Override
            public void run() {
                _this.api.release();
            }
        });
        this.api = null;
    }

    protected void setupApi() {
        final ObjectApi _this = this;
        this.eventLoop.doSynchronized(new Runnable() {
            @Override
            public void run() {
                _this.api = new V8Object(_this.eventLoop.getV8());
                _this.eventLoop.getV8().add(_this.name, _this.api);
            }
        });
    }
}
