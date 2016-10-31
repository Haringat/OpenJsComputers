import {component} from "./component";
class Screen extends component {
    constructor() {
        this.on = false;
    }

    isOn() {
        return this.on;
    }

    turnOn() {
        this.on = true;
    }

    turnOff() {
        this.on = false;
    }
}