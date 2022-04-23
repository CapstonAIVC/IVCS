const express = require('express');
const { url } = require('inspector');
const app=express();
const server=require('http').createServer(app);
const io=require('socket.io')(server);
const api_key = "94643db94d744a2a850cdf9663965164"

app.engine('html', require('ejs').renderFile);
app.set('view engine', 'html');
app.use(express.static(__dirname + "/"))
// console.log(__dirname)
// app.use("/", express.static('./'));

information = ""

app.get("/client", (req, res) => {
    res.render("client",{})
})

app.get("/getUrl", (req, res) => {
    res.json(information);
})

// socket
io.on('connection',function(socket){
    const req = socket.request;
    const ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;
    console.log('새로운 클라이언트 접속', ip, socket.id, req.ip);

    socket.on("start", (data) => {
        console.log(data)
    })
    socket.on("end", (data) => {
        console.log(data)
    })
    socket.on("changeCCTV", (data) => {
        console.log(data)
    })
    socket.on("modelOutput", (data) => {
        console.log(data)
        // socket.emit("live_res", {"cctvname":"test"data});
        // sio.emit('modelOutput', {"cctvname": "테스트이름", "time":"20xx-0x-xx", "count":str(count_pred[4][0].item())})
    })
});

server.listen(3000,()=>{
    const getUrl_spawn = require('child_process').spawn;
    const getUrl_result = getUrl_spawn('python3', ['./pytorch/getInfo.py', '1', api_key]);
    getUrl_result.stdout.on('data', (data) => {
        getUrl_result.stderr.on('data', function(data) { console.log(data.toString()); });
        
        information = data.toString().split("\n")[0]
    });
    console.log('Socket IO server listening on port ');
});