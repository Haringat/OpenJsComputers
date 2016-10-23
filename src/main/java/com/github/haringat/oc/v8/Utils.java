package com.github.haringat.oc.v8;

import com.eclipsesource.v8.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class Utils {
    public static Object toV8Value(final Object o, final V8 v8, final Object owner) {
        if (o == null ||
                o instanceof Byte ||
                o instanceof Short ||
                o instanceof Integer ||
                o instanceof Long ||
                o instanceof Float ||
                o instanceof Double ||
                o instanceof Boolean ||
                o instanceof Character ||
                o instanceof String ||
                o instanceof V8Value) {
            return o;
        } else if (o.getClass().isArray()) {
            V8Array v8Array = new V8Array(v8);
            for (Object entry: (Object[]) o) {
                Object v8CompatibleEntry = toV8Value(entry, v8, owner);
                if (v8CompatibleEntry instanceof Integer) {
                    v8Array.push((Integer) v8CompatibleEntry);
                } else if(v8CompatibleEntry instanceof Long) {
                    v8Array.push(((Long) v8CompatibleEntry).intValue());
                } else if(v8CompatibleEntry instanceof Short) {
                    v8Array.push(((Short) v8CompatibleEntry).intValue());
                } else if(v8CompatibleEntry instanceof Byte) {
                    v8Array.push(((Byte) v8CompatibleEntry).intValue());
                } else if(v8CompatibleEntry instanceof Boolean) {
                    v8Array.push((Boolean) v8CompatibleEntry);
                } else if(v8CompatibleEntry instanceof Float) {
                    v8Array.push(((Float) v8CompatibleEntry).doubleValue());
                } else if(v8CompatibleEntry instanceof Double) {
                    v8Array.push((Double) v8CompatibleEntry);
                } else if(v8CompatibleEntry instanceof String) {
                    v8Array.push((String) v8CompatibleEntry);
                } else if(v8CompatibleEntry instanceof Character) {
                    v8Array.push(v8CompatibleEntry.toString());
                } else if(v8CompatibleEntry instanceof V8Value) {
                    v8Array.push((V8Value) v8CompatibleEntry);
                } else if(v8CompatibleEntry == null) {
                    v8Array.pushNull();
                } else {
                    throw new TypeNotPresentException("A converted value is neither a V8Value nor a primitive. Long story short: We've got a problem.", null);
                }
            }
            return v8Array;
        } else if (o instanceof Method) {
            final Method method = (Method) o;
            new V8Function(v8, new JavaCallback() {
                @Override
                public Object invoke(V8Object receiver, V8Array parameters) {
                    try {
                        Object result = method.invoke(owner, parameters);
                        return toV8Value(result, v8, null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        } else {
            V8Object v8Object = new V8Object(v8);
            for (Field field: o.getClass().getFields()) {
                try {
                    //v8Object.add(field.getName(), (V8Value) toV8Value(field.get(o), v8, owner));
                    Object v8CompatibleEntry = toV8Value(field.get(o), v8, owner);
                    if (v8CompatibleEntry instanceof Integer) {
                        v8Object.add(field.getName(), (Integer) v8CompatibleEntry);
                    } else if(v8CompatibleEntry instanceof Long) {
                        v8Object.add(field.getName(), ((Long) v8CompatibleEntry).intValue());
                    } else if(v8CompatibleEntry instanceof Short) {
                        v8Object.add(field.getName(), ((Short) v8CompatibleEntry).intValue());
                    } else if(v8CompatibleEntry instanceof Byte) {
                        v8Object.add(field.getName(), ((Byte) v8CompatibleEntry).intValue());
                    } else if(v8CompatibleEntry instanceof Boolean) {
                        v8Object.add(field.getName(), (Boolean) v8CompatibleEntry);
                    } else if(v8CompatibleEntry instanceof Float) {
                        v8Object.add(field.getName(), ((Float) v8CompatibleEntry).doubleValue());
                    } else if(v8CompatibleEntry instanceof Double) {
                        v8Object.add(field.getName(), (Double) v8CompatibleEntry);
                    } else if(v8CompatibleEntry instanceof String) {
                        v8Object.add(field.getName(), (String) v8CompatibleEntry);
                    } else if(v8CompatibleEntry instanceof Character) {
                        v8Object.add(field.getName(), v8CompatibleEntry.toString());
                    } else if(v8CompatibleEntry instanceof V8Value) {
                        v8Object.add(field.getName(), (V8Value) v8CompatibleEntry);
                    } else if(v8CompatibleEntry == null) {
                        v8Object.addNull(field.getName());
                    } else {
                        throw new TypeNotPresentException("A converted value is neither a V8Value nor a primitive. Long story short: We've got a problem.", null);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return v8Object;
        }
        return null;
    }
}
