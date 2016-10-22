package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.JavaVoidCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.github.haringat.OpenJsComputers;

public class Console extends ObjectApi {
    public Console(V8 v8) {
        super(v8, "console");
        this.api.registerJavaMethod(new JavaVoidCallback() {
            @Override
            public void invoke(V8Object receiver, V8Array parameters) {
                for (int i = 0; i < parameters.length(); i++) {
                    OpenJsComputers.logger.info(parameters.get(i));
                }
            }
        }, "log");
    }


}
