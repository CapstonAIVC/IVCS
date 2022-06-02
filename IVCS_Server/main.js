const { json } = require('express');
const express = require('express');
const { url } = require('inspector');
const app=express();
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const config = require('./config/nodejs_config');

const api_key = config.API_KEY;

app.engine('html', require('ejs').renderFile);
app.set('view engine', 'html');
app.use(express.static(__dirname + "/"));
// console.log(__dirname)
// app.use("/", express.static('./'));

information = ""

app.get("/main", (req, res) => {
    res.render("index",{});
})
app.get("/getUrl", (req, res) => {
    res.json(information);
})
app.get("/getUrl_web", (req, res) => {
    res.json(information_client);
})
app.get("/getUrl_mobile", (req, res) => {
    res.send(information_client);
})
app.get("/reset_info", (req, res) => {
    const getUrl_spawn = require('child_process').spawn;
    const getUrl_result = getUrl_spawn('python3', ['./pytorch/getInfo.py', '1', api_key]);
    getUrl_result.stdout.on('data', async (data) => {
        getUrl_result.stderr.on('data', function(data) { console.log(data.toString()); });
        
        information = data.toString().split("-")[0].split("\n")[0];
        console.log('Reset HLS Address!\n');

        res.json(information);
    });

})

io.on('connection',function(socket){
    const req = socket.request;
    const ip = req.headers['x-forwarded-for'] || req.connection.remoteAddress;
    console.log('새로운 클라이언트 접속', ip, socket.id, req.ip);
});

server.listen(3000,()=>{
    const getUrl_spawn = require('child_process').spawn;
    const getUrl_result = getUrl_spawn('python3', ['./pytorch/getInfo.py', '1', api_key]);
    getUrl_result.stdout.on('data', (data) => {
        getUrl_result.stderr.on('data', function(data) { console.log(data.toString()); });
        
        information = data.toString().split("-")[0].split("\n")[0];
        information_client = data.toString().split("-")[1].split("\n")[0];
        information_json = JSON.parse(information.replace(/'/g, '"'));
        console.log('The info is ready!!\n');
    });

    console.log('Socket IO server listening on port ');
});
