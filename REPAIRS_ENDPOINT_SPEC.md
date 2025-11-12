# Especificação do Endpoint de Listagem de Reparos

## Endpoint: GET /repairs

### Descrição
Este documento especifica os dados que o endpoint do backend deve retornar para alimentar a página de listagem de reparos (`/repairs`) do sistema.

---

## Requisição

### URL
```
GET /repairs?userId={userId}
```

### Query Parameters
| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `userId` | number | Não | ID do usuário para filtrar reparos. Se não fornecido, retorna todos os reparos. |

### Headers
```
Authorization: Bearer {token}
Content-Type: application/json
```

---

## Resposta

### Status Code
- **200 OK**: Retorna lista de reparos com sucesso
- **401 Unauthorized**: Token inválido ou ausente
- **500 Internal Server Error**: Erro no servidor

### Formato da Resposta
```json
{
  "success": true,
  "data": [
    {
      "id": 1921,
      "userId": 4,
      "appliances": [
        {
          "type": "string",
          "brand": "string",
          "model": "string",
          "voltage": "string",
          "serial": "string",
          "customerNote": "string"
        }
      ],
      "parts": [
        {
          "name": "string",
          "quantity": 1,
          "unitValue": 0.00,
          "totalValue": 0.00
        }
      ],
      "partsTotal": 0.00,
      "laborValue": 0.00,
      "discount": 0.00,
      "totalValue": 0.00,
      "receivedAt": "string (DD/MM/YYYY)",
      "deadline": "string (DD/MM/YYYY)",
      "warranty": "string",
      "serviceDescription": "string",
      "notes": "string",
      "status": "EM_ANDAMENTO | FINALIZADO | NAO_INICIADO",
      "updatedAt": "string (ISO 8601: YYYY-MM-DDTHH:mm:ss)"
    }
  ]
}
```

---

## Estrutura Detalhada dos Campos

### Objeto Principal (Repair)

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `id` | number | ✅ Sim | Identificador único do reparo |
| `userId` | number | ✅ Sim | ID do usuário/cliente dono do reparo |
| `appliances` | array | ✅ Sim | Lista de eletrodomésticos do reparo (pode ser vazio []) |
| `parts` | array | ✅ Sim | Lista de peças utilizadas (pode ser vazio []) |
| `partsTotal` | number | ✅ Sim | Valor total das peças |
| `laborValue` | number | ✅ Sim | Valor da mão de obra |
| `discount` | number | ✅ Sim | Valor do desconto aplicado |
| `totalValue` | number | ✅ Sim | Valor total do serviço (partsTotal + laborValue - discount) |
| `receivedAt` | string | ✅ Sim | Data de recebimento do aparelho (formato: DD/MM/YYYY) |
| `deadline` | string | ⚠️ Condicional | Data limite de retirada (formato: DD/MM/YYYY). Obrigatório quando status=FINALIZADO |
| `warranty` | string | ✅ Sim | Período de garantia (ex: "90 dias") |
| `serviceDescription` | string | ✅ Sim | Descrição detalhada do serviço realizado |
| `notes` | string | ❌ Não | Observações adicionais sobre o reparo |
| `status` | enum | ✅ Sim | Status atual: "EM_ANDAMENTO", "FINALIZADO" ou "NAO_INICIADO" |
| `updatedAt` | string | ✅ Sim | Data/hora da última atualização (formato ISO 8601: YYYY-MM-DDTHH:mm:ss) |

### Objeto Appliance (Eletrodoméstico)

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `type` | string | ✅ Sim | Tipo do eletrodoméstico (ex: "Micro-ondas", "Geladeira") |
| `brand` | string | ✅ Sim | Marca do aparelho (ex: "Electrolux", "Brastemp") |
| `model` | string | ✅ Sim | Modelo do aparelho |
| `voltage` | string | ✅ Sim | Voltagem (ex: "127 V", "220 V", "Bivolt") |
| `serial` | string | ✅ Sim | Número de série do aparelho |
| `customerNote` | string | ❌ Não | Observação do cliente sobre o problema |

### Objeto Part (Peça)

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `name` | string | ✅ Sim | Nome/descrição da peça |
| `quantity` | number | ✅ Sim | Quantidade utilizada |
| `unitValue` | number | ✅ Sim | Valor unitário da peça |
| `totalValue` | number | ✅ Sim | Valor total (quantity × unitValue) |

---

## Regras de Negócio

### 1. Filtragem por Usuário
- Se o parâmetro `userId` for fornecido, retornar apenas os reparos daquele usuário
- Se não for fornecido, retornar todos os reparos (útil para administradores)

### 2. Ordenação
- A lista deve ser retornada ordenada pela data de atualização (`updatedAt`)
- **Ordem**: Do mais recente para o mais antigo (DESC)
- **Nota**: O frontend aplica `.reverse()`, então o backend pode retornar em ordem crescente se preferir

### 3. Status Válidos
Os únicos valores aceitos para o campo `status` são:
- `"EM_ANDAMENTO"` - Reparo em andamento
- `"FINALIZADO"` - Reparo concluído
- `"NAO_INICIADO"` - Reparo ainda não iniciado (aguardando aprovação/peças)

### 4. Cálculo de Valores
- `totalValue` deve ser: `partsTotal + laborValue - discount`
- `partsTotal` deve ser a soma de todos os `totalValue` do array `parts`
- Cada `part.totalValue` deve ser: `quantity × unitValue`

### 5. Formato de Datas
- `receivedAt` e `deadline`: Formato brasileiro DD/MM/YYYY (ex: "09/10/2025")
- `updatedAt`: ISO 8601 completo YYYY-MM-DDTHH:mm:ss (ex: "2025-10-30T18:00:00")

### 6. Arrays Vazios
- É permitido retornar `appliances: []` e `parts: []` vazios
- Neste caso, `partsTotal` deve ser `0`

### 7. Campo Deadline
- Obrigatório quando `status === "FINALIZADO"`
- Pode ser `null` ou ausente para outros status

---

## Exemplo de Resposta Completa

```json
{
  "success": true,
  "data": [
    {
      "id": 1921,
      "userId": 4,
      "appliances": [
        {
          "type": "Micro-ondas",
          "brand": "Electrolux",
          "model": "MEO44",
          "voltage": "127 V",
          "serial": "ELMO4425BR127",
          "customerNote": "O aparelho caiu no chão e não está mais funcionando."
        },
        {
          "type": "Cafeteira",
          "brand": "Dolce Gusto",
          "model": "Genio S Plus",
          "voltage": "127 V",
          "serial": "DGNI27BR459",
          "customerNote": "A cafeteira não está aquecendo o café."
        }
      ],
      "parts": [
        {
          "name": "Diodo de alta tensão (micro-ondas)",
          "quantity": 1,
          "unitValue": 12.00,
          "totalValue": 12.00
        },
        {
          "name": "Fusível de proteção (micro-ondas)",
          "quantity": 1,
          "unitValue": 28.00,
          "totalValue": 28.00
        },
        {
          "name": "Magnetron (micro-ondas)",
          "quantity": 1,
          "unitValue": 185.00,
          "totalValue": 185.00
        },
        {
          "name": "Bomba de água (cafeteira)",
          "quantity": 1,
          "unitValue": 95.00,
          "totalValue": 95.00
        },
        {
          "name": "Termostato (cafeteira)",
          "quantity": 1,
          "unitValue": 38.00,
          "totalValue": 38.00
        }
      ],
      "partsTotal": 358.00,
      "laborValue": 220.00,
      "discount": 30.00,
      "totalValue": 548.00,
      "receivedAt": "09/10/2025",
      "deadline": null,
      "warranty": "90 dias",
      "serviceDescription": "Reparo em micro-ondas sem aquecimento — substituição de fusível, diodo e magnetron.\nReparo em cafeteira Dolce Gusto que não bombeava água — substituição de bomba e termostato, além de limpeza interna.",
      "notes": "Ambos os aparelhos foram testados e apresentam funcionamento normal.\nCliente orientado sobre limpeza periódica da cafeteira e uso de tomada exclusiva para o micro-ondas.",
      "status": "EM_ANDAMENTO",
      "updatedAt": "2025-10-30T18:00:00"
    },
    {
      "id": 1906,
      "userId": 4,
      "appliances": [
        {
          "type": "Notebook",
          "brand": "Dell",
          "model": "Inspiron 15 3000",
          "voltage": "Bivolt",
          "serial": "D15-2025-5544",
          "customerNote": "Cliente relatou queda antes do problema."
        }
      ],
      "parts": [
        {
          "name": "Cabo flat display Inspiron",
          "quantity": 1,
          "unitValue": 250.00,
          "totalValue": 250.00
        }
      ],
      "partsTotal": 250.00,
      "laborValue": 130.00,
      "discount": 0.00,
      "totalValue": 380.00,
      "receivedAt": "05/08/2025",
      "deadline": "12/08/2025",
      "warranty": "90 dias",
      "serviceDescription": "Troca do cabo flat do display",
      "notes": "Cliente relatou queda antes do problema",
      "status": "FINALIZADO",
      "updatedAt": "2025-08-12T11:00:00"
    }
  ]
}
```

---

## Funcionalidades da Página /repairs

### 1. Busca (Search)
O frontend implementa busca client-side nos seguintes campos:
- `appliances[].type`
- `appliances[].brand`
- `appliances[].model`
- `appliances[].serial`
- `serviceDescription`

**Recomendação**: Considere implementar busca no backend para melhor performance com grandes volumes de dados.

### 2. Filtro por Status
O frontend permite filtrar por:
- Todos
- EM_ANDAMENTO
- FINALIZADO
- NAO_INICIADO

**Implementação atual**: Client-side
**Recomendação**: Adicionar query parameter `?status=EM_ANDAMENTO` no backend

### 3. Exibição de Cards
Cada reparo é exibido em um card mostrando:
- Número da OS (formatado como `#A{id}`)
- Status com badge colorizado
- Lista de eletrodomésticos (numerada)
- Valor total formatado (R$)
- Data de recebimento
- Data de retirada (apenas se status=FINALIZADO)
- Data de última atualização
- Botão "Ver Detalhes" (navega para `/repairs/{id}`)

---

## Melhorias Sugeridas para o Backend

### 1. Paginação
```
GET /repairs?userId={userId}&page=1&limit=10
```
Retornar:
```json
{
  "success": true,
  "data": [...],
  "pagination": {
    "currentPage": 1,
    "totalPages": 5,
    "totalItems": 50,
    "itemsPerPage": 10
  }
}
```

### 2. Filtros por Query String
```
GET /repairs?userId={userId}&status=EM_ANDAMENTO&search=micro-ondas
```

### 3. Ordenação Customizada
```
GET /repairs?userId={userId}&sortBy=updatedAt&order=desc
```

### 4. Resumo/Estatísticas
Adicionar endpoint separado ou incluir na resposta:
```json
{
  "summary": {
    "total": 50,
    "emAndamento": 15,
    "finalizado": 30,
    "naoIniciado": 5
  }
}
```

---

## Notas de Implementação

1. **Segurança**: Sempre validar que o `userId` do token corresponde ao `userId` dos reparos retornados (exceto para admins)

2. **Performance**: Com grande volume de dados, considere:
   - Implementar paginação
   - Criar índices no banco de dados para `userId` e `status`
   - Implementar cache para listas frequentemente acessadas

3. **Validação**: O backend deve validar:
   - Status deve ser um dos 3 valores aceitos
   - Datas devem ser válidas
   - Valores numéricos devem ser não-negativos
   - Arrays podem ser vazios mas não `null`

4. **Compatibilidade**: Esta especificação está baseada no código frontend atual (outubro/2025). Qualquer mudança na estrutura deve ser comunicada ao time de frontend.

---

## Contato
Para dúvidas sobre esta especificação, contactar o time de frontend.
