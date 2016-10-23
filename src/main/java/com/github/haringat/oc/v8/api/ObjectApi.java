package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

public abstract class ObjectApi extends ApiBase {

    protected V8Object api;

    public ObjectApi(V8 v8, String name) {
        super(v8, name);
    }

    public void setupApi() {
        this.api = new V8Object(this.v8);
        this.v8.add(this.name, this.api);
    }

    public void release() {
        this.api.release();
        this.api = null;
    }
}
