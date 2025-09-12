document.addEventListener('DOMContentLoaded', () => {
    const toastsContainer = document.getElementById('toasts');
    const DEFAULT_SOUND = "https://res.cloudinary.com/dvadwwvub/video/upload/v1757448760/sound_chxmth.mp4";

    function showToast({ name, amount, message, playSoundUrl, donateIsDarkTheme }) {
        const toast = document.createElement('div');
        toast.className = donateIsDarkTheme ? 'toast toast-dark' : 'toast toast-light';

        let msg = message;
        if (msg && msg.length > 30) msg = msg.substring(0, 30) + "...";

        toast.innerHTML = `
            <img src="logo.png" class="toast-logo" alt="logo">
            <div class="content">
                <div><span class="name">${name}</span> enviou <strong>R$ ${amount}</strong></div>
                ${msg ? `<div id="toast-message">"${msg}"</div>` : ''}
            </div>
        `;

        toastsContainer.appendChild(toast);

        setTimeout(() => toast.classList.add('show'), 50);

        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 5000);
    }

    const toastQueue = [];
    let isShowing = false;
    let currentAudio = null;
    const MAX_QUEUE_SIZE = 10;

    function queueToast(donation) {
        if (toastQueue.length >= MAX_QUEUE_SIZE) {
            toastQueue.splice(0, toastQueue.length - MAX_QUEUE_SIZE + 1);
        }
        toastQueue.push(donation);
        if (!isShowing) processQueue();
    }

    function processQueue() {
        if (toastQueue.length === 0) {
            isShowing = false;
            return;
        }
        isShowing = true;
        const next = toastQueue.shift();
        showToast(next);
        playAudioAndWait(next.playSoundUrl || DEFAULT_SOUND);
    }

    function playAudioAndWait(audioUrl) {
        if (currentAudio) {
            currentAudio.pause();
            currentAudio.currentTime = 0;
        }

        currentAudio = new Audio(audioUrl);
        currentAudio.volume = 0.7;

        currentAudio.addEventListener('ended', () => {
            setTimeout(processQueue, 3000);
        });

        currentAudio.addEventListener('error', () => {
            console.warn('Erro no áudio, pulando...');
            setTimeout(processQueue, 3000);
        });

        currentAudio.play().catch(() => setTimeout(processQueue, 3000));
    }

    // Conexão WebSocket STOMP
    const socket = new SockJS('https://streampix-backend.onrender.com/streampix-websocket');

    stompClient.connect({}, frame => {


        stompClient.subscribe('/topics/donation', message => {
            try {
                const donation = JSON.parse(message.body);
                const { payload, audioUrl, donateIsDarkTheme } = donation;
                if (!payload) return;

                queueToast({
                    name: payload.name || "Anônimo",
                    amount: payload.amount || 0,
                    message: payload.message || "",
                    playSoundUrl: audioUrl || DEFAULT_SOUND,
                    donateIsDarkTheme: donateIsDarkTheme || false
                });
            } catch (e) {
                console.error("Erro ao processar mensagem:", e);
            }
        });

    }, error => {
        console.error("Erro de conexão STOMP:", error);
        queueToast({
            name: "Sistema",
            amount: 0,
            message: "Erro de conexão com o servidor.",
            playSoundUrl: null,
            donateIsDarkTheme: true
        });
    });

    window.addEventListener('beforeunload', () => {
        if (stompClient && stompClient.connected) stompClient.disconnect();
        if (currentAudio) currentAudio.pause();
    });

    function connectStomp() {
        const socket = new SockJS('https://streampix-backend.onrender.com/streampix-websocket');
        const stompClient = Stomp.over(socket);
        stompClient.debug = () => {};

        stompClient.connect({}, frame => {
            console.log("Conectado via STOMP", frame);

            stompClient.subscribe('/topics/donation', message => {
                try {
                    const donation = JSON.parse(message.body);
                    const { payload, audioUrl, donateIsDarkTheme } = donation;
                    if (!payload) return;

                    queueToast({
                        name: payload.name || "Anônimo",
                        amount: payload.amount || 0,
                        message: payload.message || "",
                        playSoundUrl: audioUrl || DEFAULT_SOUND,
                        donateIsDarkTheme: donateIsDarkTheme || false
                    });
                } catch (e) {
                    console.error("Erro ao processar mensagem:", e);
                }
            });

        }, error => {
            console.error("Erro de conexão STOMP:", error);
            setTimeout(connectStomp, 5000); // tenta reconectar em 5s
        });

        return stompClient;
    }
    const stompClient = connectStomp();

});
