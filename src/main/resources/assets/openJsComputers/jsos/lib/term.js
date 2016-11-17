/**
 * @type {GPU} gpu
 */
let gpu = component.gpu;
/**
 * @type {Screen} screen
 */
let screen = component.screen;
gpu.bind(component.screen.address, true);
/**
 *
 * @param {string} msg
 * @param {number} xOffset
 * @param {number} yOffset
 */
function write (msg, xOffset, yOffset) {
    "use strict";
    if (!screen.isOn()) {
        screen.turnOn();
    }
    gpu.set(xOffset, yOffset, msg);
}
export default ;