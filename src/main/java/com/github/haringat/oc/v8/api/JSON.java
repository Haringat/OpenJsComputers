package com.github.haringat.oc.v8.api;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;
import com.github.haringat.oc.v8.eventloop.EventLoop;
import li.cil.oc.api.machine.Machine;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.eclipsesource.v8.utils.V8ObjectUtils.*;

public class JSON extends ObjectApi {


    private static final String stringToken = "\"([^\\\\\"\\p{Cntrl}]+)?\"";
    private static final String booleanToken = "(true|false)";
    private static final String nullToken = "(null)";
    private static final String numberToken = "(-?[\\d]*(?:\\.[\\d]*)?(?:[eE][+-]?[\\d]*)?)";
    private static final String objectToken = "\\{\\s*(?:((?&string))\\s*:\\s*((?&any))\\s*(?:,\\s*((?&string))\\s*:\\s*((?&any))\\s*)*)?}";
    private static final String arrayToken = "\\[\\s*(?:((?&any))\\s*(?:,\\s*((?any))\\s*)*)?]";
    private static final String groups = "(?(DEFINE)(?<string>" + stringToken + ")(?<boolean>" + booleanToken + ")(?<null>" + nullToken + ")(?<number>" + numberToken + ")(?<object>" + objectToken + ")(?<array>" + arrayToken + ")(?<any>(?&string)|(?&boolean)|(?&null)|(?&number)|(?&object)|(?&array)))";
    private static final Pattern stringPattern = Pattern.compile(stringToken);
    private static final Pattern boolPattern = Pattern.compile(booleanToken);
    private static final Pattern nullPattern = Pattern.compile(nullToken);
    private static final Pattern numberPattern = Pattern.compile(numberToken);
    private static final Pattern objectPattern = Pattern.compile(groups + objectToken);
    private static final Pattern arrayPattern = Pattern.compile(groups + arrayToken);


    public JSON(EventLoop eventLoop, Machine machine) {
        super(eventLoop, "JSON", machine);
    }

    @Override
    protected void setupApi() {
        final JSON _this = this;
        super.setupApi();
        this.addMethod("stringify", new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                String usage = "JSON.stringify(any): string";
                if (parameters.length() != 1) {
                    throw new IllegalArgumentException(usage);
                }
                return _this.stringify(parameters.get(0));
            }
        });
        this.addMethod("parse", new JavaCallback() {
            @Override
            public Object invoke(V8Object receiver, V8Array parameters) {
                String usage = "JSON.parse(string): any";
                if (parameters.length() != 1 || parameters.getType(0) != V8Value.STRING) {
                    throw new IllegalArgumentException(usage);
                }
                return _this.parse(parameters.getString(0));
            }
        });
    }

    private String stringify(V8Object object) {
        Map<String, String> entries = new HashMap<String, String>();
        for (String key: object.getKeys()) {
            String entry = this.stringify(getValue(object, key));
            if (entry != null) {
                entries.put(key, entry);
            }
        }
        String result = "{";
        String[] keys = (String[]) entries.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            result += keys[i] + ": " + entries.get(keys[i]);
            if (i < entries.size() - 1) {
                result += ",";
            }
        }
        return result + "}";
    }

    private String stringify(V8Array array) {
        List<String> entries = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++) {
            String entry = this.stringify(getValue(array, i));
            if (entry != null) {
                entries.add(entry);
            }
        }
        String result = "[";
        for (int i = 0; i < entries.size(); i++) {
            result += entries.get(i);
            if (i < entries.size() - 1) {
                result += ",";
            }
        }
        return result + "]";
    }

    private String stringify(String string) {
        return "\"" + string + "\"";
    }

    private String stringify(Integer number) {
        return number.toString();
    }

    private String stringify(Boolean bool) {
        return bool.toString();
    }

    private String stringify(Double number) {
        return number.toString();
    }

    private String stringify(Byte number) {
        return number.toString();
    }

    private String stringify(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof V8Array) {
            return this.stringify((V8Array) obj);
        } else if (obj instanceof V8Object) {
            return this.stringify((V8Object) obj);
        } else if (obj instanceof String) {
            return this.stringify((String) obj);
        } else if (obj instanceof Integer) {
            return this.stringify((Integer) obj);
        } else if (obj instanceof Byte) {
            return this.stringify((Byte) obj);
        } else if (obj instanceof Boolean) {
            return this.stringify((Boolean) obj);
        } else if (obj instanceof Double) {
            return this.stringify((Double) obj);
        } else {
            return null;
        }
    }

    private Object parse(String token) throws JSONException {
        if (token.matches(objectPattern.pattern())) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            Matcher objectMatcher = objectPattern.matcher(token);
            for (int i = 1; i <= objectMatcher.groupCount(); i += 2) {
                if (!objectMatcher.group(i).matches(stringPattern.pattern())) {
                    throw new JSONParsingException("object key is not a string");
                }
                resultMap.put(objectMatcher.group(i), this.parse(objectMatcher.group(i + 1)));
            }
            return toV8Object(this.eventLoop.getV8(), resultMap);
        } else if (token.matches(arrayPattern.pattern())) {
            List<Object> resultList = new ArrayList<Object>();
            Matcher arrayMatcher = arrayPattern.matcher(token);
            for (int i = 1; i <= arrayMatcher.groupCount(); i++) {
                resultList.add(this.parse(arrayMatcher.group(i)));
            }
            return toV8Array(this.eventLoop.getV8(), resultList);
        } else if (token.matches(stringPattern.pattern())) {
            Matcher stringMatcher = stringPattern.matcher(token);
            return stringMatcher.group(1);
        } else if (token.matches(boolPattern.pattern())) {
            Matcher boolMatcher = boolPattern.matcher(token);
            return Boolean.valueOf(boolMatcher.group(1));
        } else if (token.matches(numberPattern.pattern())) {
            Matcher numberMatcher = numberPattern.matcher(token);
            return Double.valueOf(numberMatcher.group(1));
        } else if (token.matches(nullPattern.pattern())) {
            return null;
        } else {
            throw new JSONOutOfPatternsExceptions(token);
        }
    }

    private class JSONException extends Exception {
        JSONException(String cause) {
            super(cause);
        }
    }

    private class JSONOutOfPatternsExceptions extends JSONException {
        JSONOutOfPatternsExceptions(String cause) {
            super("Could not find pattern for: " + cause);
        }
    }

    private class JSONParsingException extends JSONException {
        JSONParsingException(String cause) {
            super("Syntax error: " + cause);
        }
    }
}
