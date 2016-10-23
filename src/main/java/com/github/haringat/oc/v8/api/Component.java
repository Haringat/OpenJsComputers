package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.*;
import com.github.haringat.OpenJsComputers;
import com.github.haringat.oc.v8.Utils;
import li.cil.oc.api.machine.Machine;

import java.util.Map;

public class Component extends ObjectApi {

    private Machine machine;

    public Component(V8 v8, Machine machine) {
        super(v8, "component");
        this.machine = machine;
    }

    @Override
    public void setupApi() {
        super.setupApi();
        final Component _this = this;
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                Object[] varargs = new Object[parameters.length() - 2];
                for (int i = 2; i < parameters.length(); i++) {
                    varargs[i - 2] = parameters.get(i);
                }
                try {
                    //V8Array result = new V8Array(_this.v8);
                    Object[] rawResult = _this.machine.invoke(parameters.getString(0), parameters.getString(1), varargs);
                    Object returnValue = Utils.toV8Value(rawResult, _this.v8, null);
                    return returnValue;
                    /*for (Object resultObject: rawResult) {
                        V8Object jsObject = new V8Object(_this.v8);
                        result.push((V8Value) Utils.toV8Value(resultObject.getClass().getDeclaredFields(), _this.v8, null));
                    }*/
                    //return result;
                } catch (Exception e) {
                    OpenJsComputers.logger.error(e.getMessage());
                    for (StackTraceElement line: e.getStackTrace()) {
                        OpenJsComputers.logger.error(line);
                    };
                    _this.machine.crash(e.getMessage());
                }
                return new V8Object(_this.v8);
            }
        }, "invoke");
        this.api.registerJavaMethod(new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                final V8Object components = new V8Object(_this.v8);
                Map<String, String> componentMap = _this.machine.components();
                for (String address: componentMap.keySet()) {
                    components.add(address, componentMap.get(address));
                }
                return components;
            }
        }, "list");
    }
}
