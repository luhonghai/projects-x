/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Hai Lu luhonghai@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
 
function Haminium(editor) {
	var self=this;
    this.remoteCommandUrl = "http://localhost:17388/remote/command";
	this.isRequesting = false;
	this.isWaiting = false;
	this.editor = editor;
	this.isLogEnabled=true;
	editor.app.addObserver({
		testSuiteChanged: function(testSuite) {
			if (!self.editor.selDebugger.isHooked) {
				self.editor.selDebugger.isHooked = self.HookAnObjectMethodAfter(self.editor.selDebugger, 'init', self, self.selDebuggerInitHooked);
			}
		}
	});
}

Haminium.prototype.executeCommand = function(name, param1, param2) {
    var waitObj = {
        isWaitingHaminium: true,
        message: "Waiting for command execution complete ..."
    }
    if (!editor.haminium.isRequesting && !editor.haminium.isWaiting) {
        editor.haminium.isWaiting = true;
        editor.haminium.result = null;
        editor.haminium.isRequesting = true;
        httpGet(
                this.remoteCommandUrl + "/" + name + "/" + param1 + "/" + param2,
            null,
            function(data) {
                if (typeof data != 'undefined' && data.length > 0) {
                    editor.haminium.isRequesting = false;
                    editor.haminium.result = JSON.parse(data);
                }
            });
        throw waitObj;
    } else if (!editor.haminium.result) {
        throw waitObj;
    }
};

Haminium.prototype.selDebuggerInitHooked = function( object, arguments, retvalue  ) {
	object.runner.LOG.debug('Haminium connector installation');
	object.runner.IDETestLoop.prototype.resume = function() {
        try {
            var self=this;
            if(this.abord) return;
            if(editor.selDebugger.state == Debugger.PAUSE_REQUESTED){
                return this.continueTestAtCurrentCommand();
            }
            editor.selDebugger.runner.selenium.browserbot.runScheduledPollers();
            this._executeCurrentCommand();
            this.continueTestWhenConditionIsTrue();
        } catch (e) {
            if(e.isWaitingHaminium){
                editor.haminium.isLogEnabled = false;
                return window.setTimeout( function(){return self.resume.apply(self);}, 20);
            }
            editor.haminium.isLogEnabled = true;
            if(!this._handleCommandError(e)){
                this.testComplete();
            }else {
                this.continueTest();
            }
        }
        editor.haminium.isLogEnabled = true;
        editor.haminium.isWaiting = false;
    };
	this.HookAnObjectMethodBefore(object.runner.LOG, 'log', this, function(){return this.isLogEnabled} );
};

Haminium.prototype.HookAnObjectMethodBefore = function(ClassObject, ClassMethod, HookClassObject, HookMethod) {
  if (ClassObject) {
	var method_id = ClassMethod.toString() + HookMethod.toString();
    if (!ClassObject[method_id]) {
	  ClassObject[method_id] = ClassObject[ClassMethod];
	  ClassObject[ClassMethod] = function() {
		if( HookMethod.call(HookClassObject, ClassObject, arguments )==true ){
			return ClassObject[method_id].apply(ClassObject, arguments);
		}
      }
	  return true;
    }
  }
  return false;
};

Haminium.prototype.HookAnObjectMethodAfter = function(ClassObject, ClassMethod, HookClassObject, HookMethod) {
  if (ClassObject) {
	var method_id = ClassMethod.toString() + HookMethod.toString();
    if (!ClassObject[method_id]) {
	  ClassObject[method_id] = ClassObject[ClassMethod];
	  ClassObject[ClassMethod] = function() {
        var retvalue = ClassObject[method_id].apply(ClassObject, arguments);
        return HookMethod.call(HookClassObject, ClassObject, arguments, retvalue );
      }
	  return true;
    }
  }
  return false;
};

function httpCommon(method, url, data, headers, callback) {
    var httpRequest = new XMLHttpRequest();
    httpRequest.open(method, url);
    httpRequest.onreadystatechange = function() {
        try {
            if (httpRequest.readyState === 4) {
                if (httpRequest.status === 200 || (httpRequest.status > 200 && httpRequest.status < 300)) {
                    callback(httpRequest.responseText, true, httpRequest.status);
                } else if (httpRequest.status === 500 ) {
                    callback(httpRequest.responseText, false, httpRequest.status);
                } else {
                    LOG.debug('Error: There was a problem with the request.\nUrl: ' + url + '\nHttp Status: ' + httpRequest.status + "\nResponse: " + httpRequest.responseText);
                    callback(null, false, httpRequest.status);
                }
            }
        } catch(e) {
            throw e;
        }
    };
    if (headers) {
        for (var header in headers) {
            httpRequest.setRequestHeader(header, headers[header] + '');
        }
    }
    if (data) {
        httpRequest.send(data);
    } else {
        httpRequest.send();
    }
}

function httpPost(url, data, headers, callback) {
    httpCommon('POST',url, data, headers, callback);
}

function httpGet(url, headers, callback) {
    httpCommon('GET',url, null, headers, callback);
}

try {
	this.editor.haminium = new Haminium(this.editor);
} catch (error) {
	alert('Could not init Haminium connector. Error: ' + error);
}