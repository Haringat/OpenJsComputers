package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;
import com.github.haringat.oc.api.IApi;
import li.cil.oc.api.machine.Machine;

public abstract class ApiBase implements IApi {

    protected V8 v8;
    protected V8Object api;
    protected String name;
    protected Machine machine;

    protected ApiBase(V8 v8, String name, Machine machine) {
        this.v8 = v8;
        this.name = name;
        this.machine = machine;
        this.setupApi();
    }

    protected void setupApi() {
        this.api = new V8Object(this.v8);
        this.v8.add(this.name, this.api);
    }

    public void release() {
        this.api.release();
        this.api = null;
    }
}
