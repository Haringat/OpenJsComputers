"use strict";
/**
 * @param {component} component
 */
((component) => {
    let components = component.list();
    /**
     * @type {Screen}
     */
    let screen = component.getPrimary("screen");
    /**
     *
     * @type {GPU}
     */
    let gpu = component.getPrimary("gpu");
    if (!screen) {
        system.crash("no screen found.");
    }
    if (!gpu) {
        system.crash("no gpu found.");
    }
    for (let key in screen) {
        if (screen.hasOwnProperty(key)) {
            console.log("gpu address: " + screen.address);
            console.log("screen[" + key + "]: " + typeof screen[key]);
        }
    }
    for (let key in screen) {
        if (screen.hasOwnProperty(key)) {
            console.log("gpu address: " + gpu.address);
            console.log("gpu[" + key + "]: " + typeof screen[key]);
        }
    }
    screen.turnOn();
    gpu.bind(screen.address, true);
    gpu.fill(0, 0, 10, 10, "x");
})(component);
