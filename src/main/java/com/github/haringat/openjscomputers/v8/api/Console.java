package com.github.haringat.openjscomputers.v8.api;

import com.eclipsesource.v8.*;
import com.github.haringat.LogHelper;
import com.github.haringat.openjscomputers.v8.eventloop.EventLoop;
import li.cil.oc.api.machine.Machine;

public class Console extends ObjectApi {
    public Console(EventLoop eventLoop, Machine machine) {
        super(eventLoop, "console", machine);
    }

    @Override
    protected void setupApi() {
        super.setupApi();
        final Console _this = this;
        this.addMethod("log", new JavaVoidCallback() {
            @Override
            public void invoke(V8Object receiver, V8Array parameters) {
                for (int i = 0; i < parameters.length(); i++) {
                    LogHelper.info(parameters.get(i));
                }
            }
        });
    }
}
