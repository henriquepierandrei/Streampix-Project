# Imagem base do Python
FROM python:3.12-slim

# Diretório de trabalho
WORKDIR /app

# Copia requirements e código
COPY requirements.txt .
COPY . .

# Instala dependências
RUN pip install --no-cache-dir -r requirements.txt

# Expõe a porta que o FastAPI vai rodar
EXPOSE 8000

# Comando para rodar o TTS
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
