const { json } = require('express');
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
app.get("/getUrl_mobile", (req, res) => {
    res.send(information);
})

//for test
app.post("/test", (req, res) => {
    res.send()
})

// socket
io.on('connection',function(socket){
    const req = socket.request;
    const ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;
    console.log('새로운 클라이언트 접속', ip, socket.id, req.ip);

    socket.on("req_counting", (data) => {
        // 임시 기능
        console.log(data)
        var random = Math.random()
        socket.emit("res_counting", random.toString())
    })
    socket.on("req_analysis", (data) => {
        // 임시 기능
        var obj = JSON.parse(data.toString())
        var cctvname = obj.cctvname
        var type = obj.type
        var testdata = Array()
        for (i=0;i<12;i++){
            testdata.push( parseInt(Math.random()*10) )
        }
        console.log(testdata)
        socket.emit("res_analysis", { "data": testdata, "type": type.toString() })
    })
    socket.on("modelOutput", (data) => {
        console.log(data)
        // socket.emit("live_res", {"cctvname":"test"data});
        // sio.emit('modelOutput', {"cctvname": "테스트이름", "time":"20xx-0x-xx", "count":str(count_pred[4][0].item())})
    })
    socket.on("hls_req", (data) => {
        // console.log(information_json);
        socket.emit('hls_res', information_json.cctvurl[data]);

    })
});

server.listen(3000,()=>{
    const getUrl_spawn = require('child_process').spawn;
    const getUrl_result = getUrl_spawn('python3', ['./pytorch/getInfo.py', '1', api_key]);
    getUrl_result.stdout.on('data', (data) => {
        getUrl_result.stderr.on('data', function(data) { console.log(data.toString()); });
        
        information = data.toString().split("\n")[0]
        information_json = JSON.parse(information.replace(/'/g, '"'))
	console.log(information_json)
        console.log(' The info is ready!!');
    });
    console.log('Socket IO server listening on port ');
});
