"use strict";

export class component {

    /**
     * @type {string}
     */
    address;

    constructor() {
    }

    static list() {
        return {};
    }

    /**
     * @param {string} type
     * @returns {component}
     */
    static getPrimary(type) {
        return new component();
    }

    /**
     * @param {string} address
     * @returns {component}
     */
    static proxy(address) {
        return new component();
    }

    /**
     * @param {string} address
     * @param {string} methodName
     * @param args
     * @returns {Array}
     */
    static invoke(address, methodName, ...args) {
        return [];
    }
}