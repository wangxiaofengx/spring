'use strict';

var isChannelReady = false;
var isInitiator = false;
var isStarted = false;
var localStream;
var pc;
var allUserInfo = {};
var currUserInfo = {};
var userConnections = {};
var localVideo = document.querySelector('#localVideo');
/////////////////////////////////////////////

var room = 'foo';
var socket = new WebSocket('wss:/' + location.host + '/websocket/' + room);

socket.on = function (name, callback) {
    if (!this.event) {
        this.event = {};
    }
    this.event[name] = callback;
};

socket.onmessage = function (message) {
    var data = JSON.parse(message.data);
    this.event[data.event](data.message, data.userInfo);
};

socket.onopen = function (count) {
    console.log("Connected");
};

socket.onerror = function (err) {
    console.log("Got error", err);
};
socket.onclose = function (arg) {
    console.log("connect close ", arg);
};

socket.emit = function (name, message, sendTo) {
    this.send(JSON.stringify({'event': name, 'message': message, 'userInfo': currUserInfo, 'sendTo': sendTo}));
};

socket.on('open', function (message) {
    var count = message.onlineCount;
    currUserInfo = message.userInfo;
    console.log("online number:", count);
    console.log('curr user info :', currUserInfo);
    if (count > 1) {
        isChannelReady = true;
        sendMessage(currUserInfo, 'join');
    } else {
        isInitiator = true;
    }
});

socket.on('join', function (message, userInfo) {
    console.log(userInfo.userId + ' join room ');
    allUserInfo[userInfo.userId] = userInfo;
});

socket.on('leave', function (message) {
    var userInfo = message.userInfo;
    console.log(userInfo.userId, ' leave room');
    delete allUserInfo[userInfo.userId];
});

// This client receives a message
socket.on('message', function (message, userInfo) {
    console.log('Client received message:', message, userInfo);
    if (message === 'got user media') {
        maybeStart(userInfo);
    } else if (message.type === 'offer') {
        if (!isInitiator && !isStarted) {
            maybeStart();
        }
        pc.setRemoteDescription(new RTCSessionDescription(message));
        console.log("received offer")
        doAnswer();
    } else if (message.type === 'answer' && isStarted) {
        console.log("received answer")
        pc.setRemoteDescription(new RTCSessionDescription(message));
    } else if (message.type === 'candidate' && isStarted) {
        var candidate = new RTCIceCandidate({
            sdpMLineIndex: message.label,
            candidate: message.candidate
        });
        pc.addIceCandidate(candidate);
    } else if (message === 'bye' && isStarted) {
        handleRemoteHangup();
    }
});


////////////////////////////////////////////////

function sendMessage(message, name, sendTo) {
    console.log('Client sending message: ', message);
    socket.emit(name || 'message', sendTo);
}

////////////////////////////////////////////////////


navigator.mediaDevices.getUserMedia({
    audio: true,
    video: true
}).then(function (stream) {
    localStream = stream;
    localVideo.srcObject = stream;
    sendMessage('got user media');
}).catch(function (e) {
    alert('getUserMedia() error: ' + e.name);
});

function maybeStart(userInfo) {
    var userId = userInfo.userId;
    if (allUserInfo[userId]) {
        console.log('create connect ', userId);
        userConnections[userId] = createPeerConnection();
        doCall(userId);
    } else {
        console.log('not create connect ', userId);
    }

    // isStarted = true;
    // console.log('isInitiator', isInitiator);
    // if (isInitiator) {
    //     doCall();
    // }
}

/////////////////////////////////////////////////////////

function createPeerConnection() {
    try {
        var connect = new RTCPeerConnection(null);
        connect.onicecandidate = handleIceCandidate;
        connect.onaddstream = handleRemoteStreamAdded;
        connect.onremovestream = handleRemoteStreamRemoved;
        connect.addStream(localStream);
        console.log('Created RTCPeerConnnection');
        return connect;
    } catch (e) {
        console.log('Failed to create PeerConnection, exception: ' + e.message);
        alert('Cannot create RTCPeerConnection object.');
        return;
    }
}

function handleIceCandidate(event) {
    console.log('icecandidate event: ', event);
    if (event.candidate) {
        sendMessage({
            type: 'candidate',
            label: event.candidate.sdpMLineIndex,
            id: event.candidate.sdpMid,
            candidate: event.candidate.candidate
        });
    } else {
        console.log('End of candidates.');
    }
}

function handleCreateOfferError(event) {
    console.log('createOffer() error: ', event);
}

function doCall(userId) {
    var connect = userConnections[userId];
    console.log('Sending offer to peer');
    connect.createOffer(function (sessionDescription) {
        this.setLocalDescription(sessionDescription);
        console.log('setLocalAndSendMessage sending message', sessionDescription);
        sendMessage(sessionDescription, '', userId);
    }, handleCreateOfferError);
}

function doAnswer() {
    console.log('Sending answer to peer.');
    pc.createAnswer().then(
        setLocalAndSendMessage,
        onCreateSessionDescriptionError
    );
}

function setLocalAndSendMessage(sessionDescription) {

    pc.setLocalDescription(sessionDescription);
    console.log('setLocalAndSendMessage sending message', sessionDescription);
    sendMessage(sessionDescription);
}

function onCreateSessionDescriptionError(error) {
    trace('Failed to create session description: ' + error.toString());
}

function handleRemoteStreamAdded(event) {
    console.log('Remote stream added.', event);
    var video = document.createElement('video');
    video.srcObject = event.stream;
    video.autoplay = true;
    video.muted = true;
    video.playsinline = true;
    document.getElementById('videos').appendChild(video);
}

function handleRemoteStreamRemoved(event) {
    console.log('Remote stream removed. Event: ', event);
}

function hangup() {
    console.log('Hanging up.');
    stop();
    sendMessage('bye');
}

function handleRemoteHangup() {
    console.log('Session terminated.');
    stop();
    isInitiator = true;
}

function stop() {
    isStarted = false;
    pc.close();
    pc = null;
}
