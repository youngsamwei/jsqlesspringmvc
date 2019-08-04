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
var hostName = "com.google.chrome.demo";
function connect() {
  port = chrome.runtime.connectNative(hostName);
  port.onMessage.addListener(onNativeMessage);
  port.onDisconnect.addListener(onDisconnected);
}

function onNativeMessage(message) {
	console.log("onNativeMessage=>"+JSON.stringify(message));
	chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
  chrome.tabs.sendMessage(tabs[0].id, {data: JSON.stringify(message)}, function(response) {
    console.log(JSON.stringify(message));
  });
});
}

function onDisconnected() {
  port = null;
}

chrome.runtime.onMessageExternal.addListener(
  function(request, sender, sendResponse) {
    console.log("chrome.runtime.onMessageExternal.addListener in background.js");
    if (request.data)
      var data = request.data;
      if(data=="connect")
      {
      	connect();
      }
      else
      {
      	if(port==null)
      	{
      		console.log("disconnect with"+hostName);
      		return;
      	}

	      console.log("Hi, there is message ["+data+"]from the website");
	      var message = {"message": request.data};
	      port.postMessage(message);
      }
  });