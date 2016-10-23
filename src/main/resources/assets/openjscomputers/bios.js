"use strict";
((component) => {
    let components = component.list();
    for(let key in components) {
        component[components[key]] = key;
        console.log(key + ": " + components[key]);
    }
    component.invoke(component["screen"], "turnOn");
    component.invoke(component["gpu"], "bind", component["screen"], true);
    component.invoke(component["gpu"], "fill", 0, 0, 10, 10, "x");
})(component);
