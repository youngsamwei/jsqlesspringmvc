/*
var launch_message;
document.addEventListener('myCustomEvent', function(evt) {
    chrome.runtime.sendMessage({type: "launch", message: evt.detail}, function(response) {
      console.log(response)
    });
}, false);

document.addEventListener('jsqles_evt_get_sqlversion', function(evt) {
    chrome.runtime.sendMessage({type: "jsqles_evt_get_sqlversion", message: evt.detail}, function(response) {
      console.log(response)
    });
}, false);
*/

function appendMessage(text) {
	document.getElementById('response_div_id').innerHTML += "<p>" + text + "</p>";
}

chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
	if (request.data != null)
	{
		console.log("get response from extension" + request.data);
		appendMessage("get response from extension" + request.data);

		console.log("create custom event");
		var evt = new CustomEvent('Event');
		evt.initCustomEvent('customEvent', true, true, {'data': request.data});
		console.log("fire custom event");
		document.getElementById('response_div_id').dispatchEvent(evt)
	}
});