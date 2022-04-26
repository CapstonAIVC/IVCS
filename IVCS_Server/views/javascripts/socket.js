const socket = io.connect('http://localhost:3000');
// const socket_data = io.connect('http://localhost:5000');

const selected_camera = document.getElementById('camera');
// const live_stream = document.getElementById('hlsPlayEx');
var counting_camera = [];

selected_camera.addEventListener('submit', (e) => {
    e.preventDefault();
    var camera_id = e.target.camera_id.value
    counting_camera.push(e.target.camera_id.value);
    socket.emit('hls_req', camera_id);
    e.target.camera_id.value = '';
})

function show_counting(){
    const socket_data = io('http://localhost:5000');
    socket_data.emit('request_counting', counting_camera[counting_camera.length-1]);
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

socket.on('counting', )