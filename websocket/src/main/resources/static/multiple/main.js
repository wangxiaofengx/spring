'use strict';

const MESSAGE_TYPE_OFFER = 0x01;
const MESSAGE_TYPE_ANSWER = 0x02;
const MESSAGE_TYPE_CANDIDATE = 0x03;
const MESSAGE_TYPE_HANGUP = 0x04;

var localUserId = Math.random().toString(36).substr(2); // store local userId
var localStream; // local video stream object

var localVideo = document.querySelector('#localVideo');
var peerConnections = [];

/////////////////////////////////////////////


//var socket = io('http://localhost:8080/socket.io');
var socket = new WebSocket('wss://localhost:8080/websocket/1');

socket.on = function (name, callback) {
    if (!this.event) {
        this.event = {};
    }
    this.event[name] = callback;
};


socket.onmessage = function (message) {
    var data = JSON.parse(message.data);
    this.event[data.event](data.message);
    console.log("Signal server connected !");
};

socket.onopen = function () {
    console.log("Connected");
};

socket.onerror = function (err) {
    console.log("Got error", err);
};

socket.on('user-joined', function (data) {
    var userId = data.userId;
    if (localUserId == userId) {
        return;
    }
    console.log('Peer joined room: ', userId);
    autoSetupCall(userId);
});

socket.on('user-left', function (data) {
    var userId = data.userId;
    if (localUserId == userId) {
        return;
    }
    console.log('Peer left room: ', userId);
});

socket.on('broadcast', function (msg) {
    // if message is send by me. ignore it
    if (msg.userId == localUserId) {
        return;
    }
    // if message is not send to me. ignore it
    if (msg.targetUserId && msg.targetUserId != localUserId) {
        return;
    }
    switch (msg.msgType) {
        case MESSAGE_TYPE_OFFER:
            handleRemoteOffer(msg);
            break;
        case MESSAGE_TYPE_ANSWER:
            handleRemoteAnswer(msg);
            break;
        case MESSAGE_TYPE_CANDIDATE:
            handleRemoteCandidate(msg);
            break;
        case MESSAGE_TYPE_HANGUP:
            handleRemoteHangup(msg);
            break;
        default:
            break;
    }
});

// After joined room, remote peers will `autoSetupCall`, send offer
function handleRemoteOffer(msg) {
    console.log('Remote offer received: ', msg.userId);
    // set remote sdp
    var sdp = new RTCSessionDescription({
        'type': 'offer',
        'sdp': msg.sdp
    });
    console.log('setRemoteDescription & Answer call: ', msg.userId);
    peerConnections[msg.userId] = new RTCPeerConnectionWrapper(localUserId, msg.userId, localStream);
    peerConnections[msg.userId].setRemoteDescription(sdp);
    peerConnections[msg.userId].createAnswer();
}

// After `autoSetupCall`, wait for the remote peer anwser the offer
function handleRemoteAnswer(msg) {
    console.log('Remote answer received: ', msg.userId);
    if (peerConnections[msg.userId] == null) {
        console.log('Invlid state, can not find the offerer ', msg.userId);
        return
    }
    var sdp = new RTCSessionDescription({
        'type': 'answer',
        'sdp': msg.sdp
    });
    peerConnections[msg.userId].setRemoteDescription(sdp);
}

function handleRemoteCandidate(msg) {
    console.log('Remote candidate received: ', msg.userId);
    if (peerConnections[msg.userId] == null) {
        console.log('Invlid state, can not find the offerer ', msg.userId);
        return
    }
    var candidate = new RTCIceCandidate({
        sdpMLineIndex: msg.label,
        candidate: msg.candidate
    });
    peerConnections[msg.userId].addIceCandidate(candidate);
}

function handleRemoteHangup(msg) {
    console.log('Remote hangup received: ', msg.userId);
    if (peerConnections[msg.userId] == null) {
        console.log('Invlid state, can not find the offerer ', msg.userId);
        return
    }
    peerConnections[msg.userId].close();
}

// auto setup call by create & send offer when remote user joined room
function autoSetupCall(remoteUserId) {
    console.log('autoSetupCall: Sending offer to remote peer: ', remoteUserId);
    if (peerConnections[remoteUserId] == null) {
        peerConnections[remoteUserId] = new RTCPeerConnectionWrapper(localUserId, remoteUserId, localStream);
    }
    peerConnections[remoteUserId].createOffer();
}

function sendMessage(event, message) {
    // if (typeof message != 'string') {
    //     message = JSON.stringify(message);
    // }

    socket.send(JSON.stringify({'event': event, 'message': message}));
}

////////////////////////////////////////////////////

function RTCPeerConnectionWrapper(localUserId, remoteUserId, localStream) {
    this.localUserId = localUserId;
    this.remoteUserId = remoteUserId;
    this.pc = this.create(localStream);
    // this.pc.ondatachannel = function (ev) {
    //     console.log(ev);
    // };
    this.dataChannel = this.createDataChannel();
    this.dataChannel.onerror = function (error) {
        console.log("dataChannel Error:", error);
    };

    this.dataChannel.onopen = function (a) {
        console.log("dataChannel open:", a);
    };
    this.dataChannel.onclose = function (a) {
        console.log("dataChannel close:", a);
    };
    this.dataChannel.onmessage = function (event) {
        console.log("dataChannel Got message:", event.data);
    };

    this.remoteSdp = null;
}

RTCPeerConnectionWrapper.prototype.create = function (stream) {
    var configuration = {
        'iceServers': [
            {'url': 'stun:124.156.181.219'},
            {'url': 'turn:124.156.181.219', username: 'turnserver', credential: '123456'}
        ]
    };
    var pc = new RTCPeerConnection(configuration);
    pc.onicecandidate = this.handleIceCandidate.bind(this);
    pc.onaddstream = this.handleRemoteStreamAdded.bind(this);
    pc.onremovestream = this.handleRemoteStreamRemoved.bind(this);
    pc.ondatachannel = function (event) {
        var receiveChannel = pc.receiveChannel = event.channel;
        receiveChannel.onmessage = function (ev) {
            console.log('receive:' + ev);
        };
        receiveChannel.onopen = function (ev) {
            console.log('open');
        };
        receiveChannel.onclose = function (ev) {
            console.log('close');
        };
    };
    if (stream) {
        pc.addStream(stream);
    }

    return pc;
}

RTCPeerConnectionWrapper.prototype.close = function () {
    this.pc.close();
}

RTCPeerConnectionWrapper.prototype.handleIceCandidate = function (event) {
    if (event.candidate) {
        var message = {
            'userId': localUserId,
            'msgType': MESSAGE_TYPE_CANDIDATE,
            'id': event.candidate.sdpMid,
            'label': event.candidate.sdpMLineIndex,
            'candidate': event.candidate.candidate,
            'targetUserId': this.remoteUserId
        };
        sendMessage('broadcast', message);
        console.log('Broadcast Candidate:', message);
    } else {
        console.log('End of candidates.');
    }
}

RTCPeerConnectionWrapper.prototype.handleRemoteStreamAdded = function (event) {
    console.log('Remote stream added: ', this.remoteUserId);
    gotRemoteStream(event.stream);
}

RTCPeerConnectionWrapper.prototype.handleRemoteStreamRemoved = function (event) {
    console.log('Handle remote stream removed.');
}

RTCPeerConnectionWrapper.prototype.createOffer = function () {
    this.pc.createOffer(this.createOfferAndSendMessage.bind(this), this.handleCreateOfferError.bind(this));
}

RTCPeerConnectionWrapper.prototype.createAnswer = function () {
    this.pc.createAnswer().then(this.createAnswerAndSendMessage.bind(this), this.handleCreateAnswerError.bind(this));
}
RTCPeerConnectionWrapper.prototype.createDataChannel = function () {
    // return this.pc.createDataChannel("myDataChannel", {reliable: true});
    return this.pc.createDataChannel("myDataChannel");
}

RTCPeerConnectionWrapper.prototype.createOfferAndSendMessage = function (sessionDescription) {
    console.log('CreateOfferSdp:', sessionDescription);
    this.pc.setLocalDescription(sessionDescription);
    var message = {
        'userId': localUserId,
        'msgType': MESSAGE_TYPE_OFFER,
        'sdp': sessionDescription.sdp,
        'targetUserId': this.remoteUserId
    };
    sendMessage('broadcast', message);
    console.log('Broadcast Offer:', message);
}

RTCPeerConnectionWrapper.prototype.createAnswerAndSendMessage = function (sessionDescription) {
    console.log('CreateAnswerSdp:', sessionDescription);
    this.pc.setLocalDescription(sessionDescription);
    var message = {
        'userId': localUserId,
        'msgType': MESSAGE_TYPE_ANSWER,
        'sdp': sessionDescription.sdp,
        'targetUserId': this.remoteUserId
    };
    sendMessage('broadcast', message);
    console.log('Broadcast Answer:', message);
}

RTCPeerConnectionWrapper.prototype.handleCreateOfferError = function (event) {
    console.log('CreateOffer() error: ', event);
}

RTCPeerConnectionWrapper.prototype.handleCreateAnswerError = function (error) {
    console.log('CreateAnswer() error: ', error);
}

RTCPeerConnectionWrapper.prototype.setRemoteDescription = function (sessionDescription) {
    if (this.remoteSdp != null) {
        return
    }
    this.remoteSdp = sessionDescription;
    this.pc.setRemoteDescription(sessionDescription);
}

RTCPeerConnectionWrapper.prototype.addIceCandidate = function (candidate) {
    this.pc.addIceCandidate(candidate);
}

////////////////////////////////////////////////////
navigator && navigator.mediaDevices && navigator.mediaDevices.getUserMedia &&
navigator.mediaDevices.getUserMedia({
    audio: false,
    video: true
})
    .then(openLocalStream)
    .catch(function (e) {
        alert('getUserMedia() error: ' + e.name);
    });

function openLocalStream(stream) {
    console.log('Open local video stream');
    localVideo.srcObject = stream;
    localStream = stream;
}

function gotRemoteStream(stream) {
    var video = document.createElement('video');
    video.srcObject = stream;
    video.autoplay = true;
    video.muted = true;
    video.playsinline = true;
    document.querySelector('.videos').appendChild(video);
    console.log('Append video element: ', video);
}

var room = prompt('Enter room name:');
if (room !== '') {
    setTimeout(function () {
        console.log('Attempted to join room:', localUserId, room);
        var args = {
            'userId': localUserId,
            'roomName': room
        };
        sendMessage('user-joined', args);
        setInterval(function () {
            console.log(peerConnections);
            for (var k in peerConnections) {
                peerConnections[k].dataChannel.send("1111111111111111111");
            }
        }, 2000);
    }, 500);

}

// $('#send').click(function () {
//     for(var k in peerConnections){
//         peerConnections[k].dataChannel.send("1111111111111111111");
//     }
// });

