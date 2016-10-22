package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

public abstract class ObjectApi extends ApiBase {

    protected V8Object api;

    public ObjectApi(V8 v8, String name) {
        this.api = new V8Object(v8);
        v8.add(name, this.api);
    }
}
