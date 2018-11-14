var stompClient = null;
var sentencesStompClient = null;
var channelsStompClient = null;

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
    
    var socket3 = new SockJS('/gs-guide-websocket');
    channelsStompClient = Stomp.over(socket3);
    channelsStompClient.connect({}, function(frame) {
	    	setConnected(true);
	        //console.log('Connected: ' + frame);
	        stompClient.subscribe('/chain/channels', function (message) {
	            showChannels(JSON.parse(message.body));
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

function addChannel(channel) {
	$.get("/channel/add/" + $("#addChannelTextbox").val());
}

function deleteChannel(channel) {
    $.get("/channel/remove/" + $("#deleteChannelTextbox").val());
}

function showGreeting(message) {
	//console.log(message)
	message.forEach(function(chatMessage){
		$("#greetings").prepend("<tr><td><strong> (#" + chatMessage.channel + ") " + chatMessage.username + ": </strong>" +  chatMessage.message + "</td></tr>");
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

function showChannels(message) {
	message.forEach(function(channel) {
		var hasChannel = $("#channels").has("#"+channel)
		var hasChannelGreaterThanZero = hasChannel.length > 0;
		if(!hasChannelGreaterThanZero) {
			$("#channels").prepend("<ul id=\"" + channel + "\" >#" + channel + "</ul>")
		}
	});
	
	var currentChannels = $('#channels').map(function(){
    	return $(this).attr('id');
  	}).get();
	
	var removeIds = [];
	$('#channels').children().each(function() {
		var id = $(this).attr('id');
		var findResult = message.indexOf(id)
		if(findResult < 0) {
			removeIds.push(id);
		}
	});
	
	removeIds.forEach(function(id) {
		$('#channels').children().remove('#'+id);
	});
}
	
$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    }); 
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#addChannelButton" ).click(function() { addChannel(); });
    $( "#deleteChannelButton" ).click(function() { deleteChannel(); });
});