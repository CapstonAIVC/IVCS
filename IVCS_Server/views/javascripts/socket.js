// const socket = io.connect('http://localhost:3000');
const socket_data = io('http://localhost:4000');

let xhr = new XMLHttpRequest();

// const play_camera = document.getElementsByName('play_button')[0];
// const selected_camera = document.getElementsByName('count-camera-id')[0];

const count_text = document.getElementsByClassName('counting_text')[0];

// var analysis_camera = document.getElementById('analysis_camera_id');
var measure_unit = document.getElementById('measure_unit');
var start_date = document.getElementById('start_date');
var end_date = document.getElementById('end_date');
var start_time = document.getElementById('start_time');
var end_time = document.getElementById('end_time');

var camera_list = document.getElementsByTagName('ul')[1];

var camera_json;
var plot_img = document.getElementById('analysis_result');
var screen_shot_img = document.getElementById('screen_shot_result');
var density_img = document.getElementById('density_map_result');

var hls;

var req_counting_interval;

var hls_flag = false;
var img_flag = false;
var count_flag = false;

$.getJSON('http://localhost:3000/getUrl_web', function(data) {
    camera_json = JSON.parse(data.replace(/'/g, '"'))
    var xmlHttp = new XMLHttpRequest();
    for(var i=0; i<camera_json['cctvname'].length; i++){
        xmlHttp.open( "GET", camera_json['cctvurl'][i], false );
        xmlHttp.send( null );
        camera_json['cctvurl'][i] = xmlHttp.responseURL
        const li = document.createElement("li");
        li.className = "nav-item";
        li.setAttribute('value', i);
        if(i==0) li.innerHTML = '<a class="nav-link active" id='+i+' data-toggle="tab" href="#business-1" role="tab">'+camera_json['cctvname'][i]+'</a>';
        else li.innerHTML = '<a class="nav-link" id='+i+' data-toggle="tab" href="#business-1" role="tab">'+camera_json['cctvname'][i]+'</a>';
        camera_list.appendChild(li);
        li.addEventListener('click', function(){
            if(count_flag){
                count_flag = false;
                clearInterval(req_counting_interval);
                count_text.innerHTML = "---";
                screen_shot_img.src = "";
                density_img.src = "";
            }

            if(hls_flag){
                hls.detachMedia();;
                hls_flag = false;
            }

            if(img_flag){
                plot_img.src = "";
                img_flag = false;
            }
        });
        if(i==1) break; // 데모를 위해 2개만 보여줌
    }
});

function get_camera_id(){
    // socket.emit('hls_req', document.getElementsByClassName("active")[1].id);
    // console.log("live_stream.src : "+hls_url);

    var video = document.getElementById('hlsPlayEx');
    var videoSrc = camera_json['cctvurl'][document.getElementsByClassName("active")[1].id];
    
    if (video.canPlayType('application/vnd.apple.mpegurl')) {
        video.src = videoSrc;
    } else if (Hls.isSupported()) {
        hls = new Hls();
        hls.loadSource(videoSrc);
        hls.attachMedia(video);
        hls_flag = true;
    }
}

function req_counting_flag() {
    count_flag = true;
    req_counting_interval = setInterval(function () { 
        socket_data.emit('req_counting', document.getElementsByClassName("active")[1].id);
        console.log("plz");
    }, 2000);
}

let sendData = () => {
    xhr.open("POST", "http://localhost:4000/req_plot", true);
    xhr.setRequestHeader("Accept", "application/json");
    xhr.setRequestHeader("Content-Type", "application/json");

    let data = {
        "measure_method": measure_unit.value,
        "cameraid": document.getElementsByClassName("active")[1].id,
        "start" : start_date.value+"_"+start_time.value,
        "end" : end_date.value+"_"+end_time.value,
    };

    xhr.send(JSON.stringify(data));
}

function analysis(){
    sendData();

    xhr.responseType = "arraybuffer";
    xhr.onload = function() {
        var arrayBuffer = xhr.response;
        var byteArray = new Uint8Array(arrayBuffer);
        plot_img.src =  URL.createObjectURL( new Blob([byteArray.buffer], { type: 'image/png' } ));
        img_flag = true;
    }
}

// socket.on('hls_res', (hls_url) => {
//     console.log("live_stream.src : "+hls_url);

//     // videojs MP4 Dynamic Streaming
//     // if (videojs.getPlayers()[`hlsPlayEx`]) {
//     //     // hls-video is the id of the video tag
//     //     delete videojs.getPlayers()[`hlsPlayEx`];
//     // }
//     // videojs(`#hlsPlayEx`).src({
//     //     src: hls_url,   // dynamic link
//     //     type: "video/mp4",  // type
//     // });

//     // var player = videojs('hlsPlayEx');
//     // player.controlBar.show();
//     // player.play();

//     var video = document.getElementById('hlsPlayEx');
//     var videoSrc = hls_url;
//     //
//     // First check for native browser HLS support
//     //
//     if (video.canPlayType('application/vnd.apple.mpegurl')) {
//         video.src = videoSrc;
//         //
//         // If no native HLS support, check if HLS.js is supported
//         //
//     } else if (Hls.isSupported()) {
//         hls = new Hls();
//         hls.loadSource(videoSrc);
//         hls.attachMedia(video);
//         hls_flag = true;
//     }
// })

socket_data.on('res_counting', (count, input_bytes, density_bytes) => {
    count_text.innerHTML = String(count);
    var inputBytes = new Uint8Array(input_bytes);
    var densityBytes = new Uint8Array(density_bytes);
    screen_shot_img.src =  URL.createObjectURL( new Blob([inputBytes.buffer], { type: 'image/png' } ));
    density_img.src =  URL.createObjectURL( new Blob([densityBytes.buffer], { type: 'image/png' } ));
})