document.addEventListener('DOMContentLoaded', () => {
    const toastsContainer = document.getElementById('toasts');
    const DEFAULT_SOUND = "https://res.cloudinary.com/dvadwwvub/video/upload/v1757448760/sound_chxmth.mp3"; // Caminho correto do som de notificação

    const toastQueue = [];
    let isShowing = false;
    let currentAudio = null;
    const MAX_QUEUE_SIZE = 10;

    function showToast({ name, amount, message, donateIsDarkTheme }) {
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

    function queueToast(donation) {
        if (toastQueue.length >= MAX_QUEUE_SIZE) {
            toastQueue.splice(0, toastQueue.length - MAX_QUEUE_SIZE + 1);
        }
        toastQueue.push(donation);
        if (!isShowing) processQueue();
    }

    async function processQueue() {
        if (toastQueue.length === 0) {
            isShowing = false;
            return;
        }
        isShowing = true;
        const next = toastQueue.shift();
        showToast(next);

        // Toca primeiro som de notificação e depois áudio da mensagem
        await playNotificationAndMessage(DEFAULT_SOUND, next.playSoundUrl);

        setTimeout(processQueue, 300); // pequeno delay antes do próximo toast
    }

    async function playNotificationAndMessage(notificationSoundUrl, messageSoundUrl) {
        // Toca som de notificação
        if (notificationSoundUrl) await playAudio(notificationSoundUrl);
        // Toca áudio da mensagem
        if (messageSoundUrl) await playAudio(messageSoundUrl);
    }

    function playAudio(url) {
        return new Promise((resolve) => {
            if (currentAudio) {
                currentAudio.pause();
                currentAudio.currentTime = 0;
            }
            currentAudio = new Audio(url);
            currentAudio.volume = 0.7;
            currentAudio.onended = resolve;
            currentAudio.onerror = resolve; // em caso de erro, segue adiante
            currentAudio.play().catch(resolve);
        });
    }

    // Conexão STOMP com reconexão automática
    function connectStomp() {
        const socket = new SockJS('/streampix-websocket');
        const stompClient = Stomp.over(socket);
        stompClient.debug = () => {}; // debug desativado para produção

        stompClient.connect({}, () => {
            console.log("Conectado via STOMP");

            stompClient.subscribe('/topics/donation', message => {
                try {
                    const donation = JSON.parse(message.body);
                    const { payload, audioUrl, donateIsDarkTheme } = donation;
                    if (!payload) return;

                    queueToast({
                        name: payload.name || "Anônimo",
                        amount: payload.amount || 0,
                        message: payload.message || "",
                        playSoundUrl: audioUrl || null, // áudio da mensagem
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

    window.addEventListener('beforeunload', () => {
        if (stompClient && stompClient.connected) stompClient.disconnect();
        if (currentAudio) currentAudio.pause();
    });
});
