

(function () {
	var time = new Date();
	var tz = time.getTimezoneOffset();
   // https://developer.mozilla.org/en-US/docs/Web/API/Navigator
	var msg = {
		token: document.currentScript.getAttribute('token'),
		cid: document.currentScript.getAttribute('cid'),
		crid: document.currentScript.getAttribute('crid'),
		uuid: document.currentScript.getAttribute('uuid'),
		language: navigator.language,
		realUrl: window.location.href,
		ua: navigator.userAgent,
		screenWidth: window.screen.width,
		screenHeight: window.screen.height,
		colorDepth: screen.colorDepth,
		tzOffset: tz,
		mouseDetected: false
	};
	var time = time.getTime();
	var tid = setTimeout(cancelThisStuff,5000);
	document.addEventListener('mouseout', mouser);

	function mouser(event) {
  		clearTimeout(tid);
 		msg.mouseDetected = true;
  		document.removeEventListener('mouseout',mouser);
  		transmit();
  		true;
	}  

	function cancelThisStuff() {
		console.log("CANCELLED!");
		document.removeEventListener('mouseout',mouser);
		transmit();
	}
	
	function transmit() {
		var now = new Date().getTime();
		msg.fingerprint = hashcode(msg.language + msg.tzoffset + msg.screenWidth.toString() + 
									msg.screenHeight.toString() + msg.colorDepth.toString());
		msg.time = now -time;
		var data = JSON.stringify(msg,null,2);
		console.log(data);
		var request = new XMLHttpRequest();
		request.open('POST', 'http://localhost:8080/botcheck', true);
		request.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
		request.send(data);
	}
	
	function hashcode (s){
  		var n =  s.split("").reduce(function(a,b){a=((a<<5)-a)+b.charCodeAt(0);return a&a},0);   
  		if (n < 0)
    		n = 0xFFFFFFFF + n + 1;
   		return n.toString(16).toUpperCase();           
	}
}());