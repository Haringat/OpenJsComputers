"use strict";
/**
 * @param {component} component
 */
((component) => {
    let components = component.list();
    for (let key in components) {
        if (!components.hasOwnProperty(key)) {
            continue;
        }
        console.log(key + ": " + components[key]);
    }
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
    screen.turnOn();
    gpu.bind(screen.address, true);
    setTimeout(() => {
        gpu.fill(0, 0, 10, 10, "x");
    }, 5000);
})(component);
