const socketio = require("socket.io");
const express = require("express");
const http = require("http");

var app = require('express')();
var server = require('http').createServer(app);
var io = require('socket.io')(server);

io.on("connection", (socket) => {
    io.emit('test', 'testtest')
    socket.emit("test","test socket.emit")

    socket.on("test", (obj) =>{
        console.log(obj + "is came")
        print("!23123")
    })
});

io.on("test", (socket) => {
    print("sdfsdfs")
})

server.listen(3000, () => console.log("### 서버 시작 ###"));