package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.*;
import com.github.haringat.LogHelper;
import com.github.haringat.oc.v8.eventloop.EventLoop;
import com.github.haringat.oc.v8.eventloop.Task;
import li.cil.oc.api.machine.Machine;

import java.lang.*;
import java.util.*;

import static com.eclipsesource.v8.utils.V8ObjectUtils.*;

public class Timeout extends DirectApi {

    public Timeout(EventLoop eventLoop, Machine machine) {
        super(eventLoop, machine);
    }

    protected void setupApi() {
        super.setupApi();
        final Timeout _this = this;
        this.addMethod("setTimeout", new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, final V8Array parameters) {
                String usage = "setTimeout((...any) => void, number, ...any): number";
                if (parameters.length() < 1 || parameters.getType(0) != V8Value.V8_FUNCTION) {
                    throw new IllegalArgumentException(usage);
                }
                final V8Function callback = (V8Function) parameters.getObject(0);
                final long timeout;
                if (parameters.length() > 1) {
                    if (parameters.getType(1) != V8Value.INTEGER) {
                        throw new IllegalArgumentException(usage);
                    } else {
                        timeout = parameters.getInteger(1);
                    }
                } else {
                    timeout = 0;
                }
                V8Array args;
                if (parameters.length() > 2) {
                    List<Object> argList = toList(parameters);
                    args = toV8Array(_this.eventLoop.getV8(), argList.subList(2, argList.size() - 1));
                } else {
                    args = new V8Array(_this.eventLoop.getV8());
                }
                return _this.eventLoop.schedule(new Task(callback, null, args), timeout);
            }
        });
        this.addMethod("setInterval", new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                String usage = "setInterval((...any) => void, number, ...any): number";
                if (parameters.length() < 2 || parameters.getType(0) != V8Value.V8_FUNCTION || parameters.getType(1) != V8Value.INTEGER) {
                    throw new IllegalArgumentException(usage);
                }
                final V8Function callback = (V8Function) parameters.getObject(0);
                final long interval = parameters.getInteger(1);
                V8Array args;
                if (parameters.length() > 2) {
                    List<Object> argList = toList(parameters);
                    args = toV8Array(_this.eventLoop.getV8(), argList.subList(2, argList.size() - 1));
                } else {
                    args = new V8Array(_this.eventLoop.getV8());
                }
                return _this.eventLoop.scheduleRepetitive(new Task(callback, null, args), interval);
            }
        });
        this.addMethod("cancelTimeout", new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                String usage = "cancelTimeout(number): void";
                if (parameters.length() < 1 || parameters.getType(0) != V8Value.INTEGER) {
                    throw new IllegalArgumentException(usage);
                }
                int handler = parameters.getInteger(0);
                _this.eventLoop.cancelScheduledTask(handler);
                return null;
            }
        });
        this.addMethod("cancelInterval", new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                String usage = "cancelInterval(number): void";
                if (parameters.length() < 1 || parameters.getType(0) != V8Value.INTEGER) {
                    throw new IllegalArgumentException(usage);
                }
                int handler = parameters.getInteger(0);
                _this.eventLoop.cancelScheduledTask(handler);
                return null;
            }
        });
    }
}
