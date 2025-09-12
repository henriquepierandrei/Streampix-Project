document.addEventListener("DOMContentLoaded", async () => {
  const DEFAULT_SOUND = "sound.wav";

  const goalReason = document.getElementById("goal-reason");
  const goalEnd = document.getElementById("goal-end");
  const goalBar = document.getElementById("goal-bar");
  const goalProgress = document.getElementById("goal-progress");


    // 1️⃣ Função para carregar meta inicial
  async function loadInitialGoal() {
    try {
      const res = await fetch("http://localhost:8080/streamer/goal/to-show?streamerName=henrique");
      if (!res.ok) throw new Error("Erro ao carregar meta inicial");

      const goal = await res.json();

      updateGoalHTML(goal);

    } catch (err) {
      console.error("Erro ao carregar meta inicial:", err);
    }
  }

  // 2️⃣ Atualiza HTML da meta
  function updateGoalHTML(goal) {
    goalReason.textContent = goal.reason || "---";
  
    if (goal.end_at_in_days !== undefined && goal.end_at_in_days !== null) {
      const dias = goal.end_at_in_days;
      const palavra = dias === 1 ? "dia" : "dias";
      goalEnd.textContent = `Termina em ${dias} ${palavra}`;
    } else {
      goalEnd.textContent = "";
    }
  
    goalProgress.textContent = `R$ ${goal.current_balance} / R$ ${goal.balance_to_achieve}`;
  
    const percent = (Number(goal.current_balance) / Number(goal.balance_to_achieve)) * 100;
    goalBar.style.width = `${Math.min(percent, 100)}%`;
  }
  

  // 3️⃣ Conecta WebSocket STOMP
  function connectWebSocket() {
    const socket = new SockJS("http://localhost:8080/streampix-websocket");
    const stompClient = Stomp.over(socket);
    stompClient.debug = str => console.log("STOMP: " + str);

    stompClient.connect({}, frame => {
      console.log("Conectado via STOMP", frame);

      stompClient.subscribe("/topics/goal", message => {
        try {
          const goal = JSON.parse(message.body);
          console.log("Recebido via WS:", goal);
          updateGoalHTML(goal);
        } catch (e) {
          console.error("Erro ao processar meta WS:", e);
        }
      });

    }, error => {
      console.error("Erro de conexão STOMP:", error);
    });

    window.addEventListener("beforeunload", () => {
      if (stompClient && stompClient.connected) stompClient.disconnect();
    });
  }

  // 4️⃣ Executa
  await loadInitialGoal();
  connectWebSocket();
});
