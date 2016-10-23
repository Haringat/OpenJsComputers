package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.*;
import com.github.haringat.OpenJsComputers;

public class Console extends ObjectApi {
    public Console(V8 v8) {
        super(v8, "console");
    }

    @Override
    public void setupApi() {
        super.setupApi();
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
