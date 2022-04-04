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

    socket.on("start", (data) => {
        console.log(data)
    })
    socket.on("end", (data) => {
        console.log(data)
    })
    socket.on("changeCCTV", (data) => {
        console.log(data)
    })
    socket.on("test", (tmp)=>{
        console.log(tmp.toString());

        // 1. child-process모듈의 spawn 취득
        const spawn = require('child_process').spawn;
        // 2. spawn을 통해 "python 파이썬파일.py" 명령어 실행
        const result = spawn('python3', ['./pytorch/test.py', 'testParameter']);
        // 3. stdout의 'data'이벤트리스너로 실행결과를 받는다.
        result.stdout.on('data', function(data) { console.log(data.toString()); });
        // 4. 에러 발생 시, stderr의 'data'이벤트리스너로 실행결과를 받는다.
        result.stderr.on('data', function(data) { console.log(data.toString()); });
    })
});

server.listen(3000,()=>{
    console.log('Socket IO server listening on port ');
});