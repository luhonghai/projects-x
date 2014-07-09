
function replaceProperties(source, prop) {
    var s;
    for (var key in prop) {
        if (prop.hasOwnProperty(key)) {
            s = "${" + key + "}";
            while (source.indexOf(s) != -1) {
                source = source.replace(s, prop[key]);
            }
        }
    }
    return source;
}

Selenium.prototype.doExecuteWithProperties = function(locator, methodValue) {
    /**
    * Execute a Selenium method with value from properties
    *
    * @param locator an <a href="#locators">element locator</a>
	* @param methodValue include method name and value. eg. type|testing
	* will execute method "type" with locator and value is testing
    */
	var params = methodValue.split("|");
    var p1 = locator;
    var p2 = params[1];
    var method = params[0];
	method = "do" + method[0].toUpperCase() + method.substring(1, method.length);
    p1 = replaceProperties(p1, Cmgium.properties);
    p2 = replaceProperties(p2, Cmgium.properties);
    Selenium["prototype"][method].call(this, p1, p2);
};

Selenium.prototype.doExecuteWithParameter = function(locator, methodValue) {
    /**
     * Execute a Selenium method with value from function parameter
     *
     * @param locator an <a href="#locators">element locator</a>
     * @param methodValue include method name and value. eg. type|testing
     * will execute method "type" with locator and value is testing
     */
    var params = methodValue.split("|");
    var p1 = locator;
    var p2 = params[1];
    var method = params[0];
    method = "do" + method[0].toUpperCase() + method.substring(1, method.length);
    if (typeof Cmgium.params != "undefined") {
        p1 = replaceProperties(p1, Cmgium.params);
        p2 = replaceProperties(p2, Cmgium.params);
    }
    if (typeof Cmgium.properties != "undefined") {
        p1 = replaceProperties(p1, Cmgium.properties);
        p2 = replaceProperties(p2, Cmgium.properties);
    }
    Selenium["prototype"][method].call(this, p1, p2);
};

Selenium.prototype.doSetParameter = function(params) {
    Cmgium.params = JSON.parse(params);

};

Selenium.prototype.doTakeScreenshoot = function(fileName, methodValue) {
    /**
     * capture screen shoot
     *
     * @param fileName
     * @param methodValue
     */

};

//MORE_COMMAND