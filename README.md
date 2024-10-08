
# Trip Planner API

Este projeto é uma API para planejar viagens, desenvolvida como parte do evento NLW da Rocketseat. A API permite gerenciar viagens, atividades, participantes e links relacionados às viagens.

## Tecnologias Utilizadas

- Java
- Spring Boot
- Flyway
- H2 Database

## Como Usar

### Pré-requisitos

Certifique-se de ter o Java instalado em sua máquina.

### Configuração do Projeto

1. Clone o repositório:
    ```bash
    git clone https://github.com/thiaguss/Planner.git
    ```
2. Importe o projeto em sua IDE de preferência (IntelliJ, Eclipse, VSCode, etc.).

4. Inicie a aplicação

A aplicação estará disponível em `http://localhost:8080`.

## Endpoints

### Viagens (Trips)

#### Criar Viagem

**Endpoint:** `POST /trips`

**Descrição:** Cria uma nova viagem e registra os participantes.

**Request Body:**
```json
{
  "destination": "string",
  "starts_at": "string (ISO_DATE_TIME)",
  "ends_at": "string (ISO_DATE_TIME)",
  "emails_to_invite": ["string"],
  "owner_name": "string",
  "owner_email": "string"
}
```

**Response:**
```json
{
  "id": "UUID"
}
```

#### Obter Detalhes da Viagem

**Endpoint:** `GET /trips/{id}`

**Descrição:** Retorna os detalhes de uma viagem específica.

**Response:**
```json
{
  "id": "UUID",
  "destination": "string",
  "starts_at": "string (ISO_DATE_TIME)",
  "ends_at": "string (ISO_DATE_TIME)",
  "isConfirmed": "boolean",
  "activities": [],
  "participants": [],
  "links": []
}
```

#### Atualizar Viagem

**Endpoint:** `PUT /trips/{id}`

**Descrição:** Atualiza os detalhes de uma viagem existente.

**Request Body:**
```json
{
  "destination": "string",
  "starts_at": "string (ISO_DATE_TIME)",
  "ends_at": "string (ISO_DATE_TIME)"
}
```

**Response:**
```json
{
  "id": "UUID",
  "destination": "string",
  "starts_at": "string (ISO_DATE_TIME)",
  "ends_at": "string (ISO_DATE_TIME)",
  "isConfirmed": "boolean"
}
```

#### Confirmar Viagem

**Endpoint:** `GET /trips/{id}/confirm`

**Descrição:** Confirma uma viagem e envia emails de confirmação aos participantes.

**Response:**
```json
{
  "id": "UUID",
  "isConfirmed": "boolean"
}
```

### Atividades (Activities)

#### Registrar Atividade

**Endpoint:** `POST /trips/{id}/activities`

**Descrição:** Registra uma nova atividade para uma viagem específica.

**Request Body:**
```json
{
  "title": "string",
  "occurs_at": "string (ISO_DATE_TIME)"
}
```

**Response:**
```json
{
  "id": "UUID",
  "title": "string",
  "occurs_at": "string (ISO_DATE_TIME)"
}
```

#### Obter Todas as Atividades

**Endpoint:** `GET /trips/{id}/activities`

**Descrição:** Retorna todas as atividades de uma viagem específica.

**Response:**
```json
[
  {
    "id": "UUID",
    "title": "string",
    "occurs_at": "string (ISO_DATE_TIME)"
  }
]
```

### Participantes (Participants)

#### Convidar Participante

**Endpoint:** `POST /trips/{id}/invite`

**Descrição:** Convida um participante para uma viagem específica.

**Request Body:**
```json
{
  "email": "string"
}
```

**Response:**
```json
{
  "id": "UUID"
}
```

#### Obter Todos os Participantes

**Endpoint:** `GET /trips/{id}/participants`

**Descrição:** Retorna todos os participantes de uma viagem específica.

**Response:**
```json
[
  {
    "id": "UUID",
    "name": "string",
    "email": "string"
  }
]
```

### Links

#### Registrar Link

**Endpoint:** `POST /trips/{id}/links`

**Descrição:** Registra um novo link para uma viagem específica.

**Request Body:**
```json
{
  "url": "string",
  "title": "string"
}
```

**Response:**
```json
{
  "id": "UUID",
  "url": "string",
  "title": "string"
}
```

#### Obter Todos os Links

**Endpoint:** `GET /trips/{id}/links`

**Descrição:** Retorna todos os links de uma viagem específica.

**Response:**
```json
[
  {
    "id": "UUID",
    "url": "string",
    "title": "string"
  }
]
```

