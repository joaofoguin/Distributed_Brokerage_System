# 📊 Sistema de Corretora Distribuída (Java RMI)

## 📌 Descrição

Este projeto implementa um sistema distribuído que simula o funcionamento de uma corretora de valores, utilizando **Java RMI (Remote Method Invocation)**.

O sistema é composto por:

* 🖥️ Servidor local (Corretora)
* ☁️ Servidor cloud (opcional - cascata)
* 👥 Múltiplos clientes

Os clientes podem acessar remotamente o servidor para consultar e atualizar informações de ações em tempo real.

---

## 🎯 Objetivo

Desenvolver um sistema distribuído que atenda aos seguintes requisitos:

* Comunicação remota via RMI
* Concorrência com múltiplos clientes
* Transparência de acesso
* Atualização em tempo real (callback)
* Controle de acesso
* Tolerância a falhas

---

## 🧱 Arquitetura do Sistema

```
        ┌──────────────┐
        │   Cliente 1  │
        └──────┬───────┘
               │
        ┌──────▼───────┐
        │   Cliente 2  │
        └──────┬───────┘
               │
        ┌──────▼────────────┐
        │ Servidor Local    │
        │ (Corretora RMI)   │
        └──────┬────────────┘
               │ (cascata)
        ┌──────▼────────────┐
        │ Servidor Cloud    │
        └───────────────────┘
```

---

## ⚙️ Tecnologias Utilizadas

* Java 8+
* Java RMI
* AWS (para servidor cloud)
* Programação concorrente (Threads, synchronized, Queue)

---

## 📂 Estrutura do Projeto

* `Cliente.java` → Cliente que consome os serviços remotos
* `ClienteCallback.java` → Interface de callback
* `ClienteCallbackImpl.java` → Implementação do callback
* `CorretoraRemote.java` → Interface remota
* `CorretoraImpl.java` → Implementação da corretora
* `Servidor.java` → Servidor RMI

---

## 🔧 Funcionalidades

### 📈 Ações

* Listar ações disponíveis
* Consultar preço de uma ação
* Atualizar preço de uma ação
* Adicionar nova ação

---

### 👥 Clientes

* Registro automático no servidor
* Identificação por IP + nome da máquina
* Listagem de clientes online

---

### 🔔 Atualização em Tempo Real

* Uso de **callback RMI**
* Clientes são notificados automaticamente quando o preço muda

Exemplo:

```
🔔 Atualização: BTC = R$ 310000
```

---

### 🔄 Concorrência

* Controle de acesso por ação
* Uso de fila (`Queue<Thread>`) para evitar conflitos
* Garantia de consistência dos dados

---

### ☁️ Cascata (Servidor Cloud)

* Servidor local consulta o cloud quando necessário
* Atualizações são propagadas entre servidores
* Garante maior disponibilidade dos dados

---

### 🔐 Controle de Acesso

* Cada cliente recebe um ID único
* Identificação baseada em IP e hostname

---

### ♻️ Tolerância a Falhas

* Cliente tenta reconectar automaticamente
* Sistema continua funcionando mesmo se o cloud estiver offline

---

## ▶️ Como Executar

### 1️⃣ Compilar

```bash
javac *.java
```

---

### 2️⃣ Executar Servidor

```bash
java Servidor
```

---

### 3️⃣ Executar Cliente(s)

```bash
java Cliente
```

---

## 🧪 Testes

Durante a execução, é possível:

* Conectar múltiplos clientes simultaneamente
* Realizar consultas concorrentes
* Atualizar preços em tempo real
* Visualizar notificações instantâneas

---

## 💡 Exemplo de Uso

### Cliente:

```
1 - Listar ações
2 - Consultar preço
3 - Atualizar preço
```

---

### Servidor:

```
1 - Listar ações
2 - Consultar preço
3 - Atualizar preço
4 - Adicionar ação
5 - Listar clientes online
```

---

## ⚠️ Tratamento de Erros

* Entrada inválida (InputMismatchException) tratada
* Ação inexistente tratada com exceção
* Reconexão automática em caso de falha

---

## 📊 Requisitos Atendidos

| Requisito               | Status |
| ----------------------- | ------ |
| Comunicação RMI         | ✅      |
| Concorrência            | ✅      |
| Transparência de acesso | ✅      |
| Callback (tempo real)   | ✅      |
| Controle de acesso      | ✅      |
| Tolerância a falhas     | ✅      |
| Sistema distribuído     | ✅      |

---

## 🚀 Possíveis Melhorias

* Autenticação de usuários
* Interface gráfica (GUI)
* Persistência em banco de dados
* Logs avançados
* Simulação automática de mercado

---

## 👨‍💻 Autores

* João Pedro Silva da Rosa Lima
* Armando Alves de Oliveira Braga
* Sophia Ishii Dognani

---

## 📚 Conclusão

O sistema demonstra na prática conceitos fundamentais de sistemas distribuídos, como comunicação remota, concorrência e tolerância a falhas, utilizando Java RMI de forma eficiente e robusta.

---

## 🏁 Status

✔ Projeto finalizado
✔ Pronto para apresentação
✔ Funcional com múltiplos clientes
