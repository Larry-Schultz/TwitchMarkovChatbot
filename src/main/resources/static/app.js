var stompClient = null;
var sentencesStompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
    $("#sentences").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        //console.log('Connected: ' + frame);
        stompClient.subscribe('/chain/chatMessages', function (chatMessages) {
            showGreeting(JSON.parse(chatMessages.body));
        });
    });
    
    var socket2 = new SockJS('/gs-guide-websocket');
    sentencesStompClient = Stomp.over(socket2);
    sentencesStompClient.connect({}, function(frame) {
	    	setConnected(true);
	        //console.log('Connected: ' + frame);
	        stompClient.subscribe('/chain/generatedSentences', function (message) {
	            showSentences(JSON.parse(message.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
	//console.log(message)
	message.forEach(function(chatMessage){
		$("#greetings").prepend("<tr><td><strong>" + chatMessage.username + ": </strong>" +  chatMessage.message + "</td></tr>");
	});
	var greetingLength = $("#greetings").children().length;
	if(greetingLength > 50) {
		$("#greetings").children().slice(50, greetingLength).remove();
	};
}

function showSentences(message) {
	//console.log(message);
	message.forEach(function(sentence) {
		$("#sentences").prepend("<li>" + sentence +"</li>");
	});
	var sentencesLength = $("#sentences").children().length;
	if(sentencesLength > 50) {
		$("#sentences").children().slice(50, sentencesLength).remove();
	};
}
	
$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});