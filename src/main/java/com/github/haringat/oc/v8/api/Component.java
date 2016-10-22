package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

public class Component extends ObjectApi {

    public Component(V8 v8) {
        super(v8, "component");
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                return null;
            }
        }, "invoke");
    }
}
