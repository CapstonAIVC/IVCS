const express = require('express');
const app=express();
const server=require('http').createServer(app);
const io=require('socket.io')(server);

app.engine('html', require('ejs').renderFile);
app.set('view engine', 'html');
app.use(express.static(__dirname + "/"))

app.get("/client", (req, res) => {
    res.render("client",{})
})

// socket
io.on('connection',function(socket){
    console.log("New Client!");

    socket.on("test", (data) => {
        console.log(data)
    })
});

server.listen(3000,()=>{
    console.log('Socket IO server listening on port ');
});