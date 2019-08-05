/*
var port = null;
chrome.runtime.onMessage.addListener(
  function(request, sender, sendResponse) {
     if (request.type == "launch"){
       	connectToNativeHost(request.message);
    } else if (request.type == "jsqles_evt_get_sqlversion"){
        connectToNativeHost("jsqles_evt_get_sqlversion");
    }
	return true;
});


//onNativeDisconnect
function onDisconnected()
{
	console.log(chrome.runtime.lastError);
	console.log('disconnected from native app.');
	port = null;
}

function onNativeMessage(message) {
	console.log('recieved message from native app: ' + JSON.stringify(message));
}

//connect to native host and get the communicatetion port
function connectToNativeHost(msg)
{
	var nativeHostName = "com.google.chrome.demo";
	console.log(nativeHostName);
 	port = chrome.runtime.connectNative(nativeHostName);
	port.onMessage.addListener(onNativeMessage);
	port.onDisconnect.addListener(onDisconnected);
	port.postMessage({message: msg});
 }
 */

 /* https://blog.csdn.net/lee353086/article/details/49362811 */
function focusOrCreateTab(url) {
  chrome.windows.getAll({"populate":true}, function(windows) {
    var existing_tab = null;
    for (var i in windows) {
      var tabs = windows[i].tabs;
      for (var j in tabs) {
        var tab = tabs[j];
        if (tab.url == url) {
          existing_tab = tab;
          break;
        }
      }
    }
    if (existing_tab) {
      chrome.tabs.update(existing_tab.id, {"selected":true});
    } else {
      chrome.tabs.create({"url":url, "selected":true});
    }
  });
}

chrome.browserAction.onClicked.addListener(function(tab) {
  var manager_url = chrome.extension.getURL("main.html");
  focusOrCreateTab(manager_url);
});

document.addEventListener("DOMContentLoaded", function () {
});

var port = null;
var rsp = null;
var hostName = "com.google.chrome.demo";
function connect() {
  port = chrome.runtime.connectNative(hostName);
  port.onMessage.addListener(onNativeMessage);
  port.onDisconnect.addListener(onDisconnected);
}

function onNativeMessage(message) {

	console.log("onNativeMessage=>"+JSON.stringify(message));

    /*调用web页面的response处理函数*/
	if (rsp){
	    rsp(message);
	    rsp = null;
	}
//	chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
//        chrome.tabs.sendMessage(tabs[0].id, {data: JSON.stringify(message)}, function(response) {
//            console.log(JSON.stringify(message));
//        });
//    });
}

function onDisconnected() {
  port = null;
}

chrome.runtime.onMessageExternal.addListener(
  function(request, sender, sendResponse) {
//    console.log("chrome.runtime.onMessageExternal.addListener in background.js");

        if (request.data){
            if (request.data == "connect"){
                connect();
                return;
            }
        }
      	if(port==null)	{
            connect();
        }
        /*保存web页面传递的response处理函数*/
        rsp = sendResponse;
//        message = {"requestType":"query", "dbname":"testdb", "sqlText": request.sqlText};
        port.postMessage(request);
//        console.log("Hi, there is message ["+data+"]from the website");


      return true;
  });