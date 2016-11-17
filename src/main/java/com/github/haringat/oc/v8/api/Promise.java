package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.*;
import com.github.haringat.oc.v8.eventloop.EventLoop;
import com.github.haringat.oc.v8.eventloop.Task;
import li.cil.oc.api.machine.Machine;

import java.util.*;

import static com.eclipsesource.v8.utils.V8ObjectUtils.*;

public class Promise extends ObjectApi {

    private JavaCallback constructor;
    private V8Object prototype;
    private Map<V8Object, List<State>> promises = new HashMap<V8Object, List<State>>();

    public Promise(EventLoop eventLoop, Machine machine) {
        super(eventLoop, "Promise", machine);
    }

    @Override
    protected void setupApi() {
        final Promise _this = this;
        /*this.constructor = new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                String usage = "Usage: new Promise(([() => void], [() => void]) => void): Promise";
                if (parameters.length() < 1 || parameters.getType(0) != V8Value.V8_FUNCTION) {
                    throw new IllegalArgumentException(usage);
                }
                if (receiver == null || receiver.isUndefined()) {
                    throw new IllegalArgumentException("Constructor must be invoked with \"new\"");
                }
                receiver.setPrototype(_this.prototype);
                receiver.add("state", States.PENDING);
                V8Function resolve = new V8Function(_this.eventLoop.getV8(), new JavaCallback() {
                    @Override
                    public Object invoke(V8Object receiver, V8Array parameters) {
                        receiver.add("state", States.RESOLVED);
                    }
                });
                return receiver;
            }
        };
        this.eventLoop.doSynchronized(new Runnable() {
            @Override
            public void run() {
                _this.prototype = new V8Object(_this.eventLoop.getV8());
                _this.prototype.registerJavaMethod(new JavaCallback() {
                    @Override
                    public Object invoke(V8Object receiver, V8Array parameters) {
                        if (receiver.getInteger("state") == States.PENDING) {
                            for (V8Object keyObject: _this.promises.keySet()) {
                                // if we have a different descriptor object for the same JS object it should still work
                                if (receiver.strictEquals(keyObject)) {
                                    _this.promises.get(keyObject).add();
                                }
                            }
                        } else if (receiver.getInteger("state") == States.RESOLVED) {
                            if (parameters.length() > 0 && (parameters.getType(0) == V8Value.V8_FUNCTION)) {
                                V8Function successCallback = (V8Function) parameters.getObject(0);
                                List<Object> paramList = new ArrayList<Object>();
                                paramList.add(receiver.get("value"));
                                V8Array parameter = toV8Array(_this.eventLoop.getV8(), paramList);
                                _this.eventLoop.schedule(new Task(successCallback, null, parameter));
                            } else {
                                // go to next then();
                            }
                        }

                    }
                }, "then");
                _this.api = new V8Function(_this.eventLoop.getV8(), _this.constructor);
                _this.api.setPrototype(_this.prototype);
                _this.eventLoop.getV8().add(_this.name, _this.api);
            }
        });
        this.addMethod("resolve", new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                V8Object base = new V8Object(_this.eventLoop.getV8());
                base.setPrototype()
                return _this.constructor.invoke();
            }
        });*/
    }

    private class States {
        public static final int PENDING = 0;
        public static final int RESOLVED = 0;
        public static final int REJECTED = 0;
    }

    private class State {
        private V8Function onSuccess;
        private V8Function onFailure;
        public State() {
        }
        public State(V8Function onSuccess) {
            this();
            this.onSuccess = onSuccess;
        }
        public State(V8Function onSuccess, V8Function onFailure) {
            this(onSuccess);
            this.onFailure = onFailure;
        }
    }
}
