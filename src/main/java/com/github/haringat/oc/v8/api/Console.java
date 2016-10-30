package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.*;
import com.github.haringat.OpenJsComputers;
import com.github.haringat.oc.v8.Utils;
import li.cil.oc.api.machine.Machine;

public class Console extends ApiBase {
    public Console(V8 v8, Machine machine) {
        super(v8, "console", machine);
    }

    @Override
    protected void setupApi() {
        super.setupApi();
        final Console _this = this;
        this.api.registerJavaMethod(new JavaVoidCallback() {
            @Override
            public void invoke(V8Object receiver, V8Array parameters) {
                for (int i = 0; i < parameters.length(); i++) {
                    if (parameters.get(i) instanceof String) {
                        OpenJsComputers.logger.info(parameters.get(i));
                    } else {
                        V8Array params = (V8Array) Utils.toV8Value(new Object[]{parameters.get(i)}, _this.v8, null);
                        OpenJsComputers.logger.info(_this.v8.getObject("JSON").executeStringFunction("stringify", params));
                        params.release();
                    }
                }
            }
        }, "log");
    }
}
