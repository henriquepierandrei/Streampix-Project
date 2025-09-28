# 🎮 StreamPix Donations API

<div align="center">
  <img src="src/main/resources/static/video-readme.gif" alt="StreamPix Demo" width="700" style="border-radius: 10px; box-shadow: 0 4px 20px rgba(0,0,0,0.1);">

<h3>🚀 Plataforma de Doações Inteligente para Streamers</h3>

  <p>
    <strong>StreamPix</strong> é uma plataforma revolucionária que transforma doações via PIX em experiências interativas únicas,
    com mensagens geradas por IA e alertas em tempo real para suas lives.
  </p>
</div>

---

## 🏆 **Tecnologias & Status**

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React-18.2.0-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-5.0-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Python](https://img.shields.io/badge/Python-3.11-3776AB?style=for-the-badge&logo=python&logoColor=white)

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-24.0-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-Cache-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-Real_Time-25D366?style=for-the-badge)

![Mercado Pago](https://img.shields.io/badge/Mercado%20Pago-PIX_API-00B1EA?style=for-the-badge)
![AI Powered](https://img.shields.io/badge/AI-Powered-FF6B6B?style=for-the-badge&logo=openai&logoColor=white)

</div>

---

## ✨ **Por que StreamPix?**

<table>
<tr>
<td width="50%">

### 🎯 **Para Streamers**
- 💰 **Doações PIX instantâneas** sem complicação
- 🤖 **Mensagens únicas com IA** para cada doação
- 📊 **Analytics completo** com métricas em tempo real
- 🎨 **Overlays customizáveis** para OBS
- 🔄 **Integração simples** em qualquer setup

</td>
<td width="50%">

### 🎮 **Para Viewers**
- ⚡ **Doação em 30 segundos** via PIX
- 🎵 **Mensagens com áudio** usando Edge-TTS
- 🎨 **Interface moderna** e responsiva
- 📱 **QR Code dinâmico** para facilitar doações
- 🔒 **100% seguro** com Mercado Pago

</td>
</tr>
</table>

---

## 🏗️ **Arquitetura do Sistema**

<div align="center">

```mermaid
graph TB
    subgraph "Frontend Layer"
        A[React + TypeScript]
        B[Material-UI]
        C[Socket.io Client]
    end
    
    subgraph "API Gateway"
        D[Spring Cloud Gateway]
    end
    
    subgraph "Backend Services"
        E[Spring Boot API]
        F[WebSocket Server]
        G[Python AI Service]
        H[FastAPI + Edge-TTS]
    end
    
    subgraph "Data & Cache"
        I[PostgreSQL 15]
        J[Redis Cache]
        K[Message Queue]
    end
    
    subgraph "External Services"
        L[Mercado Pago API]
        M[Cloudinary Storage]
    end
    
    A --> D
    B --> D
    C --> F
    D --> E
    D --> F
    D --> G
    G --> H
    E --> I
    E --> J
    F --> K
    E --> L
    H --> M
```

</div>

---

## 🚀 **Stack Tecnológica**

<div align="center">
<table>
<tr>
<th width="33%">🔧 Backend</th>
<th width="33%">🎨 Frontend</th>
<th width="33%">🤖 AI & Services</th>
</tr>
<tr>
<td>

- **Java 21** + Spring Boot 3.2
- **Spring WebSocket** para real-time
- **Spring Security** + JWT
- **PostgreSQL 15** + Redis
- **Docker** containerização

</td>

  <img src="src/main/resources/static/diagrama-img-email.png" alt="StreamPix Demo" width="700" style="border-radius: 10px; box-shadow: 0 4px 20px rgba(0,0,0,0.1);">


<td>

- **React 18** + TypeScript 5
- **Material-UI** components
- **Socket.io** WebSocket client
- **React Query** state management
- **Vite** build tool

</td>
<td>

- **Python 3.11** + FastAPI
- **Edge-TTS** para síntese de voz
- **Mercado Pago SDK** PIX
- **Cloudinary** storage
- **OpenAI/Local LLM** para IA

</td>
</tr>
</table>
</div>

---

## 📱 **API Reference**

### 🔐 **Autenticação**

<details>
<summary><b>POST</b> <code>/auth/register</code> - Registrar Streamer</summary>

```json
{
  "email": "streamer@example.com",
  "password": "senha123",
  "name": "João"
}
```

**<code>✅ Response 201:</code>**
<code> Streamer registrado com sucesso! </code>

**<code>❎ Response 400:</code>**

❎ Email ou nome já existente:
```json
{
  "timestamp": "2025-09-11T12:49:02.982250147",
  "status": 400,
  "error": "Bad Request",
  "message": "message error here",
  "path": "/auth/register"
}
```

</details>

<details>
<summary><b>POST</b> <code>/auth/login</code> - Login de Usuário</summary>

```json
{
  "email": "streamer@example.com",
  "password": "senha123"
}
```

**<code>✅ Response 200:</code>**

✅ Streamer Logado
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "tokenExpireAt": 757549795261,
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshTokenExpireAt": 1758150995261
}
```


**<code>❎ Response 400:</code>**

❎ Erro nas credenciais para login
```json
{
  "timestamp": "2025-09-11T09:52:44.2360403",
  "status": 400,
  "error": "Bad Request",
  "message": "message error here",
  "path": "/auth/login"
}
```

</details>

---

### 💰 **Doações**

<details>
<summary><b>POST</b> <code>/donation</code> - Criar Nova Doação</summary>

```json
{
  "name": "Matheus",
  "amount": 10.00,
  "message": "Olá, gostei muito da sua live!",
  "voice_type": "female"
}
```

**<code>✅ Response 200:</code>**

✅ Doação realizada com sucesso
```json
{
  "qrcode": "00020126580014br.gov.bcb.pix...",
  "amount": 10.00,
  "donationId": "uuid-here",
  "expiresIn": 300
}
```

**<code>❎ Response 500:</code>**

❎ Erro na geração do áudio ocasiona o Internal Server Error 500
```json
{
  "timestamp": "2025-09-11T09:53:55.1895305",
  "status": 500,
  "error": "Internal Server Error",
  "message": "message error here",
  "path": "/donation"
}
```

**<code>❎ Response 409:</code>**

❎ Quando há conflitos nos valores inseridos com os valores permitidos
```json
{
  "timestamp": "2025-09-11T09:56:14.8558602",
  "status": 409,
  "error": "Conflict",
  "message": "message error here",
  "path": "/donation"
}
```

</details>

<details>
<summary><b>GET</b> <code>/donation/{UUID}</code> - Status da Doação</summary>

**<code>✅ Response 200:</code>**

**✅ Doação Realizada / Doação Pendente:**
```json
{
  "already_paid": false,  // "already_paid": true => Doação paga com sucesso.
  "time_remaining_seconds": 84,
  "qrcode": "00020126580014br.gov.bcb.pix...",
  "amount": 10.00,
  "name": "Matheus"
}
```
**<code>❎ Response 409:</code>**

**❎ Doação Expirada:**
```json
{
  "timestamp": "2025-09-10T23:25:08.174959401",
  "status": 409,
  "error": "Conflict",
  "message": "message error here",
  "path": "/donation/uuid-here"
}
```

**<code>❎ Response 404:</code>**
**❎ Doação Não encontrada:**
```json
{
  "timestamp": "2025-09-11T10:02:18.9028454",
  "status": 404,
  "error": "Not Found",
  "message": "message error here",
  "path": "/donation/05cccad1-40c8-4459-ad8f-611ae109ac2"
}
```
</details>

---

### 🎯 **Metas e Analytics**

<details>
<summary><b>GET</b> <code>/streamer/goal</code> - 🔒 Buscar Meta Atual</summary>

**Headers:** `Authorization: Bearer {token}`

**<code>✅ Response 200:</code>**

✅ Meta buscada com sucesso
```json
{
  "current_balance": 1250.00,
  "balance_to_achieve": 5000.00,
  "end_at_in_days": 30,
  "uuid": "2fabd5dc-8ea1-4c2d-af0a-a75b96ab030f",
  "reason": "Novo Setup Gamer"
}
```

**<code>❎ Response 404:</code>**

❎ Meta não encontrada ou inexistente
```json
{
  "timestamp": "2025-09-11T10:28:01.3166017",
  "status": 404,
  "error": "Not Found",
  "message": "message error here",
  "path": "/streamer/goal"
}
```

</details>

<details>
<summary><b>POST</b> <code>/streamer/goal</code> - 🔒 Criar Meta</summary>

**Headers:** `Authorization: Bearer {token}`

```json
{
  "balance_to_achieve": 3000.00,
  "end_at_in_days": 60,
  "reason": "Setup para streaming 4K"
}
```

**<code>✅ Response 200:</code>**

**✅ Meta criada:**
```json
{
  "status": "OK",
  "message": "Meta criada com sucesso, identificação: 552fe179-5c94-43be-adde-34a8c4aa1e21"
}
```
**<code>❎ Response 400:</code>**

**❌ Meta já existente:**
```json
{
  "timestamp": "2025-09-11T10:40:26.2271125",
  "status": 400,
  "error": "Bad Request",
  "message": "message error here",
  "path": "/streamer/goal"
}
```

</details>

<details>
<summary><b>PUT</b> <code>/streamer/goal</code> - 🔒 Atualizar Meta</summary>

**Headers:** `Authorization: Bearer {token}`

```json
{
  "uuid": "552fe179-5c94-43be-adde-34a8c4aa1e21",   // Inclua o UUID para atualizar a meta
  "balance_to_achieve": 3000.00,
  "end_at_in_days": 60,
  "reason": "Setup para streamer"
}
```

**<code>✅ Response 200:</code>**

**✅ Meta Atualizada:**
```json
{
  "status": "OK",
  "message": "Meta Atualizada com sucesso!"
}
```

**<code>❎ Response 404:</code>**

**❎ Meta Não Encontrada:**
```json
{
  "timestamp": "2025-09-11T10:49:25.2750292",
  "status": 404,
  "error": "Not Found",
  "message": "message error here",
  "path": "/streamer/goal"
}
```
</details>

<details>
<summary><b>DELETE</b> <code>/streamer/goal</code> - 🔒 Deletar Meta</summary>

**Headers:** `Authorization: Bearer {token}`

**<code>✅ Response 200:</code>**

**✅ Meta Atualizada:**
```json
{
  "status": "OK",
  "message": "Meta excluída com sucesso!"
}
```

**<code>❎ Response 404:</code>**

**❎ Meta Não Encontrada:**
```json
{
  "timestamp": "2025-09-11T10:53:36.5917167",
  "status": 404,
  "error": "Not Found",
  "message": "message error here",
  "path": "/streamer/goal"
}
```
</details>

---

### 📊 **Logs e Histórico**

<details>
<summary><b>GET</b> <code>/streamer/log/donations</code> - 🔒 Histórico de Doações</summary>

**Headers:** `Authorization: Bearer {token}`

**Query Params:**
- `minAmount` - Valor mínimo
- `maxAmount` - Valor máximo
- `startDate` - Data inicial (YYYY-MM-DD)
- `endDate` - Data final (YYYY-MM-DD)
- `page` - Página (default: 0)
- `size` - Tamanho (default: 20)


**<code>✅ Response 200:</code>**

**✅ Log de doações buscados:**
```json
{
  "content": [
    {
      "donated_at": "2025-09-09T19:45:43.089375Z",
      "audio_url": "https://res.cloudinary.com/.../audio.mp3",
      "uuid": "21660273-be46-4dce-82b1-428fec3b5e51",
      "name": "João Silva",
      "message": "Continue com esse conteúdo incrível!",
      "amount": "25,00"
    }
  ],
  "totalPages": 5,
  "totalElements": 98,
  "size": 20,
  "number": 0
}
```

</details>

---

### ⚙️ **Configurações do Streamer**

<details>
<summary><b>GET</b> <code>/streamer</code> - 🔒 Dados do Dashboard</summary>

**Headers:** `Authorization: Bearer {token}`

**<code>✅ Response 200:</code>**
**✅ Obter os dados do Streamer na Dashboard:**
```json
{
  "streamer_name": "João Gamer",
  "streamer_balance": "1572,55",
  "is_auto_play": true,
  "min_amount": 5,
  "max_characters_name": 15,
  "max_characters_message": 200,
  "qr_code_is_dark_theme": true,
  "add_messages_bellow": false,
  "donate_is_dark_theme": true,
  "http_response": {
    "status": "OK",
    "message": "Dados obtidos com sucesso!"
  }
}
```

</details>

<details>
<summary><b>PUT</b> <code>/streamer</code> - 🔒 Atualizar Configurações</summary>

**Headers:** `Authorization: Bearer {token}`

**<code>✅ Response 200:</code>**

✅ Atualizar o streamer
```json
{
  "streamer_name": "henrique123",
  "streamer_balance": "0,0",
  "is_auto_play": true,
  "min_amount": 5.0,
  "max_characters_name": 10,
  "max_characters_message": 250,
  "qr_code_is_dark_theme": false,
  "add_messages_bellow": false,
  "donate_is_dark_theme": true,
  "http_response": {
    "status": "OK",
    "message": "Streamer atualizado com sucesso!"
  }
}
```

</details>

---

## 🔌 **WebSocket Events**

### 📡 **Conexão**
```javascript
const socket = new SockJS(${URL.BACKEND} + '/streampix-websocket');
```

### 🎊 **Eventos Disponíveis**

| Destino STOMP                      | Descrição                                                                         | Payload                                                                                                                              |
| ---------------------------------- | --------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| `/topics/donation`                 | Nova doação recebida / Reproduzida (`notifyDonationSuccess` e `/replay-donation`) | `{ id, isDonated, audioUrl, qrCodeIsDarkTheme, addMessagesBellow, donateIsDarkTheme, dto: { donorName, message, amountFormatted } }` |
| `/topics/payments/{transactionId}` | Status de processamento do PIX (`notifyPayment`)                                  | `{ transactionId, isDonated, timeRemainingSeconds }`                                                                                 |
| `/topics/goal/`                    | Meta atualizada (`notifyGoalIncrement`)                                           | `{ id, currentBalance, targetBalance, reason, remainingDays }`                                                                       |


---

## 🤖 **Serviço de IA**

### 🎙️ **Geração de Mensagem + Áudio**

<details>
<summary><b>POST</b> <code>/generate-message</code> - Criar Mensagem Personalizada</summary>

```json
{
  "donorName": "João Silva",
  "amount": 25.00,
  "originalMessage": "Parabéns pelo conteúdo!",
  "streamContext": {
    "game": "Valorant",
    "category": "FPS",
    "viewerCount": 340
  },
  "personality": "funny",
  "voiceType": "female"
}
```

**✅ Response:**
```json
{
  "generatedMessage": "🎮 João Silva dropou R$ 25 no servidor! Com essa doação épica, você merece ser o MVP da partida! Continue dominando no Valorant! 🔥",
  "audioUrl": "https://res.cloudinary.com/.../audio.mp3",
  "tokens_used": 67,
  "model": "gpt-3.5-turbo",
  "duration_seconds": 8.5
}
```

</details>

---

## 🎨 **Overlay para OBS/Streamlabs**

### 📺 **Configuração Browser Source**

```
URL: ${URL.FRONTEND}/streamer/qrcode/{StreamerName}
```
### ⚙️ Configurações Recomendadas - Qr-Code

| Propriedade | Valor              |
|-------------|--------------------|
| **Width** | 350px              |
| **Height** | 350px              |
| **FPS** | 30                 |
| **CSS** | Fundo transparente |
| **Shutdown source when not visible** | ✅                  |
| **Refresh browser when scene becomes active** | ✅                  |

---

```
URL: ${URL.FRONTEND}/streamer/dashboard/goal/to-show/{StreamerName}
```
### ⚙️ Configurações Recomendadas - Meta

| Propriedade | Valor              |
|-------------|--------------------|
| **Width** | 720px              |
| **Height** | 100px              |
| **FPS** | 30                 |
| **CSS** | Fundo transparente |
| **Shutdown source when not visible** | ✅                  |
| **Refresh browser when scene becomes active** | ✅                  |

---


```
URL: ${URL.BACKEND}/messaging/index.html            // Por enquanto não existe URL com identificação único
```
### ⚙️ Configurações Recomendadas - Mensagem

| Propriedade | Valor              |
|-------------|--------------------|
| **Width** | 580px              |
| **Height** | 150px              |
| **FPS** | 60                 |
| **CSS** | Fundo transparente |
| **Shutdown source when not visible** | ❎                  |
| **Refresh browser when scene becomes active** | ❎                  |

---

## 🔒 **Segurança & Performance**

<div align="center">
<table>
<tr>
<th width="50%">🛡️ Segurança</th>
<th width="50%">⚡ Performance</th>
</tr>
<tr>
<td>

- **JWT Authentication** com refresh tokens
- **Rate Limiting** 100 req/min por IP
- **CORS configurado** para domínios específicos
- **Validação de dados** em todas APIs
- **Logs de segurança** completos

</td>
<td>

- **Connection Pooling** no banco
- **Compressão Gzip** habilitada
- **CDN Cloudinary** para mídia
- **WebSocket** otimizado para real-time

</td>
</tr>
</table>
</div>

### 🔐 **Headers de Autenticação**
```bash
# Todas as rotas protegidas (🔒) requerem:
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

---

---

## 📊 **Métricas & Analytics**

<div align="center">

| Métrica | Descrição |
|---------|-----------|
| 💰 **Total Arrecadado** | Soma de todas as doações |
| 📈 **Doações/Hora** | Média de doações por hora |          // Em desenvolvimento...
| 👥 **Doadores Únicos** | Total de pessoas que doaram |     // Em desenvolvimento...
| 🎯 **Meta Progresso** | % da meta atual atingida |         
| ⏱️ **Tempo Médio** | Tempo médio entre doações |           // Em desenvolvimento...   

</div>

---

## 🎯 **Roadmap**

- [ ] 🎨 **Temas customizáveis** para overlay
- [ ] 📊 **Dashboard avançado**
- [ ] 🎵 **Efeitos sonoros** personalizáveis - Em desenvolvimento...

---

## 📞 **Suporte & Contato**

<div align="center">

| Canal | Informação |
|-------|------------|
| 📧 **Email** | profissional.henriquepierandrei@gmail.com |
| 💬 **Discord** | rique_pieran |
| 🐛 **Issues** | [GitHub Issues](https://github.com/seu-usuario/streampix/issues) |

</div>

---

## 📝 **Licença**

Este projeto está licenciado sob a **MIT License** - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 🙏 **Agradecimentos**

Agradecimentos especiais para:

- **Mercado Pago** pela robusta API de pagamentos PIX
- **Edge-TTS** pela síntese de voz de alta qualidade
- **Spring Community** pelo framework excepcional
- **React Team** pela biblioteca incrível
- **Todos os beta testers** que ajudaram a melhorar a plataforma

---

<div align="center">
  <br>
  <img src="https://img.shields.io/badge/Made%20with-❤️-red.svg?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Powered%20by-AI-blue.svg?style=for-the-badge"/>
  <br><br>
  <strong>© 2025 StreamPix - Transformando doações em experiências únicas</strong>
</div>