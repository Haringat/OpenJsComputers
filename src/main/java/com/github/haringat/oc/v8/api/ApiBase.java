package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Value;
import com.github.haringat.oc.api.IApi;

public abstract class ApiBase implements IApi {

    protected V8 v8;
    protected V8Value api;
    protected String name;

    public ApiBase(V8 v8, String name) {
        this.v8 = v8;
        this.name = name;
        this.setupApi();
    }

    public abstract void setupApi();

    public abstract void release();
}
