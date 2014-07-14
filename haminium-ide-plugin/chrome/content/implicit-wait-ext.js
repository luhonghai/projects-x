function httpCommon(method, url, data, headers, callback) {
  var httpRequest = new XMLHttpRequest();
  //LOG.debug('Executing: ' + method + " " + url);
  httpRequest.open(method, url);
  httpRequest.onreadystatechange = function() {
    try {
      if (httpRequest.readyState === 4) {
        if (httpRequest.status === 200 || (httpRequest.status > 200 && httpRequest.status < 300)) {
          callback(httpRequest.responseText, true, httpRequest.status);
        } else if (httpRequest.status === 500 ) {
          callback(httpRequest.responseText, false, httpRequest.status);
        } else {
          //TODO eliminate alert and signal the failure
//          alert('There was a problem with the request.\nUrl: ' + url + '\nHttp Status: ' + httpRequest.status + "\nResponse: " + httpRequest.responseText);
          LOG.debug('Error: There was a problem with the request.\nUrl: ' + url + '\nHttp Status: ' + httpRequest.status + "\nResponse: " + httpRequest.responseText);
          callback(null, false, httpRequest.status);
        }
      }
    } catch(e) {
      //TODO eliminate alert and signal the failure
      alert('Caught Exception in httpPost: ' + e);
      throw e;
    }
  };
  //httpRequest.channel.loadFlags |= Components.interfaces.nsIRequest.LOAD_BYPASS_CACHE;
  for (var header in headers) {
    httpRequest.setRequestHeader(header, headers[header] + '');
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
/**
 * @Author : Florent BREHERET
 * @Function : Activate an implicite wait on action commands when trying to find elements.
 * @Param timeout : Timeout in millisecond, set 0 to disable the implicit wait
 * @Exemple 1 : setImplicitWaitLocator | 0
 * @Exemple 1 : setImplicitWaitLocator | 1000
 */
Selenium.prototype.doSetImplicitWaitLocator = function(timeout){
	if( !editor.implicitwait ) throw new SeleniumError("setImplicitWaitLocator works on Selenium IDE only ! ");
	if( timeout==0 ) {
		editor.implicitwait.locatorTimeout=0;
		editor.implicitwait.isImplicitWaitLocatorActivated=false;
	}else{
		editor.implicitwait.locatorTimeout=parseInt(timeout);
		editor.implicitwait.isImplicitWaitLocatorActivated=true;
	}
};
/**
 
 
 * @author : Florent BREHERET
 * @Function : Activate an implicite wait for condition before commands are executed.
 * @Param timeout : Timeout in millisecond, set 0 to disable the implicit wait
 * @Param condition_js : Javascript logical expression that need to be true to execute each command.
 *
 * @Exemple 0 : setImplicitWaitCondition |  0  |  
 * @Exemple 1 : setImplicitWaitCondition |  1000  | (typeof window.Sys=='undefined') ? true : window.Sys.WebForms.PageRequestManager.getInstance().get_isInAsyncPostBack()==false;
 * @Exemple 2 : setImplicitWaitCondition |  1000  | (typeof window.dojo=='undefined') ? true : window.dojo.io.XMLHTTPTransport.inFlight.length==0;
 * @Exemple 3 : setImplicitWaitCondition |  1000  | (typeof window.Ajax=='undefined') ? true : window.Ajax.activeRequestCount==0;
 * @Exemple 4 : setImplicitWaitCondition |  1000  | (typeof window.tapestry=='undefined') ? true : window.tapestry.isServingRequests()==false;
 * @Exemple 4 : setImplicitWaitCondition |  1000  | (typeof window.jQuery=='undefined') ? true : window.jQuery.active==0;
 */
Selenium.prototype.doSetImplicitWaitCondition = function( timeout, condition_js ) {
	if( !editor.implicitwait ) throw new SeleniumError("setImplicitWaitCondition works on Selenium IDE only ! ");
	if( timeout==0 ) {
		editor.implicitwait.conditionTimeout=0;
		editor.implicitwait.implicitAjaxWait_Condition=null;
		editor.implicitwait.implicitAjaxWait_Function=function(){return true;};
		editor.implicitwait.isImplicitWaitAjaxActivated=false;
	}else{
		editor.implicitwait.conditionTimeout=parseInt(timeout);
		editor.implicitwait.implicitAjaxWait_Condition=condition_js;
		selenium.getEval('editor.implicitwait.implicitAjaxWait_Function=function(){ return eval("' + condition_js + '");};');
		editor.implicitwait.isImplicitWaitAjaxActivated=true;
	}
}

Selenium.prototype.getUserAgent = function() {
	throw {
		haminiumStage: true,
		message: 'Go to haminium method'
	}
}