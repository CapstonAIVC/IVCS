const socket = io();

const selected_camera = document.getElementById('camera');
const live_stream = document.getElementById('hlsPlayEx');

selected_camera.addEventListener('submit', (e) => {
    e.preventDefault();
    const camera_id = e.target.camera_id.value
    socket.emit('hls_req', camera_id);
    e.target.camera_id.value = '';
})

socket.on('hls_res', (hls_url) => {
    console.log("live_stream.src : "+live_stream.src);

    if (videojs.getPlayers()[`hlsPlayEx`]) {
        // hls-video is the id of the video tag                        
        delete videojs.getPlayers()[`hlsPlayEx`];
    }
    videojs(`#hlsPlayEx`).src({
        src: hls_url,   // dynamic link
        type: "video/mp4",  // type
    });

    var player = videojs('hlsPlayEx');
    player.controlBar.show();
    player.play();
})