package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.V8Value;
import com.github.haringat.oc.api.IApi;

public abstract class ApiBase implements IApi {

    protected V8Value api;

    public void release() {
        this.api.release();
    }
}
