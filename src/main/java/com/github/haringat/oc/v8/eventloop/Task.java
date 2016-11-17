package com.github.haringat.oc.v8.eventloop;

import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;

public class Task {
    private V8Function function;
    private V8Object thisArg;
    private V8Array arguments;

    Object execute() {
        return this.function.call(this.thisArg, this.arguments);
    }

    public Task(V8Function function, V8Object thisArg, V8Array arguments) {
        this.function = function;
        this.thisArg = thisArg;
        this.arguments = arguments;
    }

    V8Function getFunction() {
        return this.function;
    }

    V8Object getThisArg() {
        return this.thisArg;
    }

    V8Array getArguments() {
        return this.arguments;
    }

    void cleanUp() {
        this.function.release();
        for (int i = 0; i < this.arguments.length(); i++) {
            if (this.arguments.get(i) instanceof V8Value) {
                ((V8Value) this.arguments.get(i)).release();
            }
        }
        this.arguments.release();
        if (this.thisArg != null) {
            this.thisArg.release();
        }
    }
}
