const socket = io.connect('http://localhost:3000');
const socket_data = io('http://localhost:4000');

const selected_camera = document.getElementById('camera');
// const live_stream = document.getElementById('hlsPlayEx');
const count_text = document.getElementsByClassName('counting_text')[0]

var analysis_camera = document.getElementById('analysis_camera_id');
var measure_unit = document.getElementById('measure_unit')
var start_date = document.getElementById('start_date')
var end_date = document.getElementById('end_date')
var start_time = document.getElementById('start_time')
var end_time = document.getElementById('end_time')

var counting_camera = [];

selected_camera.addEventListener('submit', (e) => {
    e.preventDefault();
    var camera_id = e.target.camera_id.value
    counting_camera.push(e.target.camera_id.value);
    socket.emit('hls_req', camera_id);
    e.target.camera_id.value = '';
})

function req_counting_flag() {
    setInterval(function () { 
        socket_data.emit('req_counting', counting_camera[counting_camera.length-1]); 
    }, 2000);
}

function analysis(){
    // console.log(measure_unit.value)
    // console.log(analysis_camera.value)
    // console.log(start_date.value+"-"+start_time.value)
    // console.log(end_date.value+"-"+end_time.value)
    socket_data.emit('req_plot', measure_unit.value, analysis_camera.value, start_date.value+"-"+start_time.value, end_date.value+"-"+end_time.value);
}

socket.on('hls_res', (hls_url) => {
    console.log("live_stream.src : "+hls_url);

    // videojs MP4 Dynamic Streaming
    // if (videojs.getPlayers()[`hlsPlayEx`]) {
    //     // hls-video is the id of the video tag
    //     delete videojs.getPlayers()[`hlsPlayEx`];
    // }
    // videojs(`#hlsPlayEx`).src({
    //     src: hls_url,   // dynamic link
    //     type: "video/mp4",  // type
    // });

    // var player = videojs('hlsPlayEx');
    // player.controlBar.show();
    // player.play();

    var video = document.getElementById('hlsPlayEx');
    var videoSrc = hls_url;
    //
    // First check for native browser HLS support
    //
    if (video.canPlayType('application/vnd.apple.mpegurl')) {
        video.src = videoSrc;
        //
        // If no native HLS support, check if HLS.js is supported
        //
    } else if (Hls.isSupported()) {
        var hls = new Hls();
        hls.loadSource(videoSrc);
        hls.attachMedia(video);
    }
})

socket_data.on('res_counting', (count) => {
    count_text.innerHTML = count;
})

socket_data.on('res_plot', (plot_img) => {
    
})