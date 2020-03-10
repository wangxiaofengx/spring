'use strict';

var isChannelReady = false;
var isInitiator = false;
var isStarted = false;
var localStream;
var pc;
var allUserInfo = {};
var currUserInfo = {};
var localVideo = document.querySelector('#localVideo');

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
    var userId = userInfo.userId;
    console.log(userId, ' leave room');
    allUserInfo[userId].connect.getRemoteStreams().forEach(function (stream) {
        document.getElementById(stream.id).remove();
    });
    allUserInfo[userId].connect && allUserInfo[userId].connect.close();
    delete allUserInfo[userId];
});

// This client receives a message
socket.on('message', function (message, userInfo) {
    console.log('Client received message:', message, userInfo);
    var userId = userInfo.userId;
    if (message === 'got user media') {
        allUserInfo[userId].connect = createPeerConnection(userId);
        doCall(allUserInfo[userId]);
    } else if (message.type === 'offer') {
        allUserInfo[userId] = userInfo;
        allUserInfo[userId].connect = createPeerConnection(userId);
        allUserInfo[userId].connect.setRemoteDescription(new RTCSessionDescription(message));
        console.log("received offer");
        doAnswer(allUserInfo[userId]);
    } else if (message.type === 'answer') {
        console.log("received answer");
        allUserInfo[userId].connect.setRemoteDescription(new RTCSessionDescription(message));
    } else if (message.type === 'candidate') {
        var candidate = new RTCIceCandidate({
            sdpMLineIndex: message.label,
            candidate: message.candidate
        });
        allUserInfo[userId].connect.addIceCandidate(candidate);
    }
});


function sendMessage(message, name, sendTo) {
    console.log('Client sending message: ', message);
    socket.emit(name || 'message', message, sendTo);
}

// load audio and video
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

function createPeerConnection(userId) {
    try {
        var connect = new RTCPeerConnection(null);
        console.log(connect);
        connect.onicecandidate = function (event) {
            handleIceCandidate(event, userId);
        };
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

function handleIceCandidate(event, userId) {
    console.log('icecandidate event: ', event);
    if (event.candidate) {
        sendMessage({
            type: 'candidate',
            label: event.candidate.sdpMLineIndex,
            id: event.candidate.sdpMid,
            candidate: event.candidate.candidate
        }, '', userId);
    } else {
        console.log('End of candidates.');
    }
}

function handleCreateOfferError(event) {
    console.log('createOffer() error: ', event);
}

function doCall(userInfo) {
    var connect = userInfo.connect;
    console.log('Sending offer to peer');
    connect.createOffer().then(function (sessionDescription) {
        connect.setLocalDescription(sessionDescription);
        console.log('setLocalAndSendMessage sending message', sessionDescription);
        sendMessage(sessionDescription, '', userInfo.userId);
    }, handleCreateOfferError);
}

function doAnswer(userInfo) {
    var connect = userInfo.connect;
    console.log('Sending answer to peer.');
    connect.createAnswer().then(function (sessionDescription) {
        connect.setLocalDescription(sessionDescription);
        console.log('setLocalAndSendMessage sending message', sessionDescription);
        sendMessage(sessionDescription, '', userInfo.userId);
    }, onCreateSessionDescriptionError);
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
    video.id = event.stream.id;
    video.playsinline = true;
    document.getElementById('videos').appendChild(video);
}

function handleRemoteStreamRemoved(event) {
    console.log('Remote stream removed. Event: ', event);
}
