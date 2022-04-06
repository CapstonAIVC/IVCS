const socket = io();

const selected_camera = document.getElementById('camera');
const live_stream = document.getElementById('video');

selected_camera.addEventListener('submit', (e) => {
    e.preventDefault();
    const camera_id = e.target.camera_id.value
    socket.emit('hls_req_test', camera_id);
    e.target.camera_id.value = '';
})

// socket.on('chat message', (message) => {
//     live_stream.appendChild(makeMessage(message, true));
// })