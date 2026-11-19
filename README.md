# Tá Normal? — API (Domain Driven Design Java)

API RESTful da CitizenTech **Tá Normal?**, Global Solution 2026 da FIAP.
O passageiro cadastra o trajeto que faz todo dia e, antes de sair de casa, recebe o aviso de **quantos minutos a viagem deve atrasar** e se vale ir por outro caminho. A API junta o status oficial das linhas (Metrô e CPTM) com os relatos dos passageiros.

- **Deploy (Render):** https://tanormal-api-rafaell.onrender.com/api/linhas
- **Aviso do trajeto de exemplo:** https://tanormal-api-rafaell.onrender.com/api/trajetos/1/aviso
- **Documentação (PDF):** [docs/Documentacao_TaNormal_DDD_Java.pdf](docs/Documentacao_TaNormal_DDD_Java.pdf)

**Equipe:** Enzo Augusto (RM562249) · Gustavo Neres dos Santos (RM561785) · Rafaell Santiago (RM563486) · Sebastian Iriarte (RM563619)

## Ficha da CitizenTech

| Campo | Resposta |
|---|---|
| **Nome da CitizenTech** | Tá Normal? |
| **Cidade e bairro** | São Paulo, Zona Leste. Trajeto piloto: Itaim Paulista → Brás pela Linha 12-Safira, depois Linha 3-Vermelha do Metrô até a Sé |
| **Cidadão beneficiado** | Quem depende de metrô e trem para estudar e trabalhar, principalmente quem mora longe do centro e faz baldeação, onde um atraso no meio do caminho vira atraso no dia inteiro |
| **Problema, em uma frase** | Quando o metrô ou o trem dá problema, o aviso oficial só diz "velocidade reduzida" e o passageiro não sabe quanto vai atrasar, quanto tempo vai durar nem se vale mudar de caminho, e às vezes o status ainda diz "operação normal" com a linha lenta |
| **Como funciona** | A pessoa cadastra o trajeto e o horário em que sai de casa. A API junta o status oficial das linhas, os relatos dos passageiros e o histórico de ocorrências, estima o atraso em minutos e, a partir de 20 minutos, sugere uma rota alternativa |
| **O que cada parte faz** | Agile: ideia, personas, casos de uso, backlog e protótipo. Data Science: regressão linear que prevê os minutos de atraso e regressão logística para o atraso de 20 minutos ou mais. Java: esta API, com trajetos, relatos, ocorrências e o aviso de atraso. Database Design: banco Oracle. Dynamic Programming: grafo das estações para a rota alternativa. Redes: rede da estação com Wi-Fi para o passageiro. AR-VR: vídeo 3D do passageiro recebendo o aviso |
| **Link do repositório** | https://github.com/RafaellSantiagoMS/tanormal-api (protótipo do app: https://github.com/RafaellSantiagoMS/ta-normal-app) |
| **Indicador de impacto** | Minutos de atraso evitados por usuário por semana; acerto dos alertas (precisão e recall); quantas vezes o status oficial dizia "normal" enquanto os relatos apontavam lentidão |
| **ODS conectados** | 11 (cidades e transporte público melhores), 9 (infraestrutura e inovação) e 10 (quem mora na periferia é quem mais perde tempo no transporte) |

## Tecnologias

| Item | Versão |
|---|---|
| Java (JDK) | 21 |
| Spring Boot | 4.1.1 (Spring Web MVC) |
| Banco de dados | Oracle da FIAP, acessado com JDBC (driver ojdbc17) |
| Build | Maven (Maven Wrapper) |
| Deploy | Render, com Dockerfile |
| IDE | IntelliJ IDEA |

## Organização do código

```
br.com.fiap
├── conexoes     ConexaoFactory: abre a conexão JDBC com o Oracle
├── entities     Usuario, Linha, Estacao, Trajeto, Relato, Ocorrencia, Aviso e SituacaoLinha
├── dao          Um DAO por tabela, com os comandos SQL (insert, select, update, delete)
├── bo           Regras de negócio e o cálculo do aviso (AvisoBo)
├── controller   Endpoints REST e o CorsConfig
└── excecoes     NaoEncontradoException (404) e RegraNegocioException (400)
```

## Como executar

1. **Banco:** no SQL Developer, conectado no Oracle da FIAP, abra e rode com *Executar Script* (F5):
   1. [`docs/ddl-oracle.sql`](docs/ddl-oracle.sql): cria as tabelas `TN_*` e as sequences;
   2. [`docs/dml-oracle.sql`](docs/dml-oracle.sql): cadastra as 4 linhas do protótipo, as estações e os dados de demonstração.
2. **IntelliJ:** *File > Open* e escolha a pasta do projeto. Espere o Maven baixar as dependências e confira o JDK 21 em *File > Project Structure*.
3. **Usuário e senha do Oracle:** em *Run > Edit Configurations > Environment variables*, coloque
   `DB_USERNAME=rmXXXXXX;DB_PASSWORD=sua_senha`.
   A senha não fica no código porque o repositório é público.
4. Rode a classe `TaNormalApplication` e teste no Postman ou no navegador: http://localhost:8080/api/linhas

### Deploy no Render

1. *New > Web Service* e escolha este repositório. O Render usa o `Dockerfile`.
2. Em *Environment*, crie as variáveis `DB_USERNAME` e `DB_PASSWORD` com o usuário e a senha do Oracle.
3. Plano *Free* e *Deploy Web Service*.

> No plano gratuito o Render desliga o serviço depois de 15 minutos sem uso. A primeira requisição depois disso pode levar até 3 minutos.

## Endpoints

| Método | Endpoint | O que faz |
|---|---|---|
| GET | `/api/usuarios` | Lista os usuários |
| GET | `/api/usuarios/{id}` | Busca um usuário |
| GET | `/api/usuarios/{id}/trajetos` | Trajetos do usuário |
| POST | `/api/usuarios` | Cadastra usuário |
| PUT | `/api/usuarios/{id}` | Atualiza usuário |
| DELETE | `/api/usuarios/{id}` | Remove usuário |
| GET | `/api/linhas` | Lista as linhas com o status oficial |
| GET | `/api/linhas/{id}` | Busca uma linha |
| GET | `/api/linhas/{id}/estacoes` | Estações da linha |
| GET | `/api/linhas/{id}/relatos` | Relatos da linha |
| GET | `/api/linhas/{id}/ocorrencias` | Histórico de ocorrências da linha |
| POST | `/api/linhas` | Cadastra linha |
| PUT | `/api/linhas/{id}` | Atualiza linha |
| PUT | `/api/linhas/{id}/status` | Atualiza o status oficial |
| DELETE | `/api/linhas/{id}` | Remove linha |
| GET | `/api/estacoes` | Lista as estações |
| GET | `/api/estacoes/{id}` | Busca estação |
| POST | `/api/estacoes` | Cadastra estação |
| PUT | `/api/estacoes/{id}` | Atualiza estação |
| DELETE | `/api/estacoes/{id}` | Remove estação |
| GET | `/api/trajetos` | Lista os trajetos |
| GET | `/api/trajetos/{id}` | Busca trajeto |
| **GET** | **`/api/trajetos/{id}/aviso`** | **Aviso de atraso do trajeto** |
| **GET** | **`/api/trajetos/{id}/aviso/chuva`** | **Aviso de atraso considerando chuva** |
| POST | `/api/trajetos` | Cadastra trajeto |
| PUT | `/api/trajetos/{id}` | Atualiza trajeto |
| DELETE | `/api/trajetos/{id}` | Remove trajeto |
| GET | `/api/relatos` | Lista os relatos |
| GET | `/api/relatos/{id}` | Busca relato |
| POST | `/api/relatos` | Envia relato (PARADO, LENTO ou LOTADO) |
| PUT | `/api/relatos/{id}` | Atualiza relato |
| PUT | `/api/relatos/{id}/confirmar` | Outro passageiro confirma o relato |
| DELETE | `/api/relatos/{id}` | Remove relato |
| GET | `/api/ocorrencias` | Histórico de ocorrências |
| GET | `/api/ocorrencias/abertas` | Ocorrências ainda abertas |
| GET | `/api/ocorrencias/{id}` | Busca ocorrência |
| POST | `/api/ocorrencias` | Registra ocorrência (OFICIAL também muda o status da linha) |
| PUT | `/api/ocorrencias/{id}` | Atualiza ocorrência |
| PUT | `/api/ocorrencias/{id}/encerrar` | Encerra a ocorrência |
| DELETE | `/api/ocorrencias/{id}` | Remove ocorrência |

## Roteiro rápido de teste (Postman)

1. `GET /api/trajetos/1/aviso` → "Tá normal! Pode sair às 06:30 como sempre."
2. `POST /api/ocorrencias` com o corpo abaixo → a Linha 12-Safira passa para `VELOCIDADE_REDUZIDA`.
   ```json
   { "idLinha": 1, "status": "VELOCIDADE_REDUZIDA", "fonte": "OFICIAL",
     "descricao": "Velocidade reduzida entre Brás e Calmon Viana", "chuva": true }
   ```
3. `GET /api/trajetos/1/aviso/chuva` → "Sua viagem deve atrasar uns 12 min. Saia às 06:18 para chegar no horário."
4. `PUT /api/ocorrencias/{id}/encerrar` (id devolvido no passo 2) → a linha volta para `NORMAL`.

## Como o atraso é estimado

- **Status oficial** de cada linha do trajeto: Normal = 0 min, Velocidade reduzida = 10, Operação parcial = 20, Paralisada = 40.
- **Relatos dos últimos 30 minutos**: Parado = 8 min, Lento = 4, Lotado = 2. Cada confirmação aumenta o peso do relato (até 3 vezes) e os relatos somam no máximo 25 min por linha.
- Em cada linha vale o **maior** dos dois valores. Depois somam-se as linhas do trajeto.
- **Chuva**: +20% no atraso.
- A partir de **20 min** de atraso o aviso sugere uma rota alternativa.

Os cortes de 30 minutos para os relatos e de 20 minutos para o alerta são os mesmos usados no app e no notebook de Data Science.
