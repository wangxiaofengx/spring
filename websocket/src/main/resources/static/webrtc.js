var connection = new WebSocket('ws://192.168.1.2:8080/websocket/1');
var localName = Math.random().toString(36).substr(2);

var loginInput = document.querySelector('#loginInput');
var loginBtn = document.querySelector('#loginBtn');
var otherUsernameInput = document.querySelector('#otherUsernameInput');
var connectToOtherUsernameBtn = document.querySelector('#connectToOtherUsernameBtn');
var peerConnections = [];
loginInput.value = localName;
//when a user clicks the login button
loginBtn.addEventListener("click", function (event) {
    localName = loginInput.value;
    if (localName.length > 0) {
        send({
            type: "login",
            name: localName
        });
    }

});


//handle messages from the server
connection.onmessage = function (message) {
    console.log("Got message", message.data);
    var data = JSON.parse(message.data);
    if (data.name == localName) {
        return;
    }
    switch (data.type) {
        case "login":
            onLogin(data.name);
            break;
        case "offer":
            onOffer(data.offer, data.name);
            break;
        case "answer":
            onAnswer(data.answer, data.name);
            break;
        case "candidate":
            onCandidate(data.candidate, data.name);
            break;
        default:
            break;
    }
};

//when a user logs in
function onLogin(name) {

    //creating our RTCPeerConnection object

    // var configuration = {
    //     "iceServers": [{"url": "stun:stun.1.google.com:19302"}]
    // };

    var configuration = {
        'iceServers': [
            {'url': 'stun:124.156.181.219'},
            {'url': 'turn:124.156.181.219', username: 'turnserver', credential: '123456'}
        ]
    };


    if (peerConnections[name] == null) {
        peerConnections[name] = new RTCPeerConnection(configuration);
    }

    console.log("RTCPeerConnection object was created");
    console.log(peerConnections[name]);

    //setup ice handling
    //when the browser finds an ice candidate we send it to another peer
    peerConnections[name].onicecandidate = function (event) {
        if (event.candidate) {
            send({
                name: name,
                type: "candidate",
                candidate: event.candidate
            });
        }
    };
    peerConnections[name].createOffer(function (offer) {
        peerConnections[name].setLocalDescription(offer);
        send({
            name: name,
            type: "offer",
            offer: offer
        });
    }, function (error) {
        alert("An error has occurred.");
    });
};

connection.onopen = function () {
    console.log("Connected");
};

connection.onerror = function (err) {
    console.log("Got error", err);
};

// Alias for sending messages in JSON format
function send(message) {
    connection.send(JSON.stringify(message));
};
//setup a peer connection with another user
connectToOtherUsernameBtn.addEventListener("click", function () {

    if (localName.length > 0) {
        //make an offer
        peerConnections[name].createOffer(function (offer) {
            peerConnections[name].setLocalDescription(offer);
            send({
                name: name,
                type: "offer",
                offer: offer
            });


        }, function (error) {
            alert("An error has occurred.");
        });
    }
});

//when somebody wants to call us
function onOffer(offer, name) {
    peerConnections[name].setRemoteDescription(new RTCSessionDescription(offer));

    console.log("1111111111");
    // peerConnections[name].createAnswer().then(function (sessionDescription) {
    //     peerConnections[name].setLocalDescription(sessionDescription);
    //     send({
    //         name: name,
    //         type: "answer",
    //         answer: sessionDescription
    //     });
    // }, function (reason) {
    //     alert("oops...error");
    // });

    // peerConnections[name].createAnswer(function (answer) {
    //     peerConnections[name].setLocalDescription(answer);
    //
    //     send({
    //         name: name,
    //         type: "answer",
    //         answer: answer
    //     });
    //
    // }, function (error) {
    //     alert("oops...error");
    // });
}

//when another user answers to our offer
function onAnswer(answer, name) {
    peerConnections[name].setRemoteDescription(new RTCSessionDescription(answer));
}

//when we got ice candidate from another user
function onCandidate(candidate, name) {
    peerConnections[name].addIceCandidate(new RTCIceCandidate(candidate));
}

