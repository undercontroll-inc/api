# API - Criar Ordem de Serviço (OS)

## Endpoint
```
POST /repairs
```

## Descrição
Este endpoint cria uma nova ordem de serviço no sistema. É utilizado pelo administrador para registrar uma nova OS associada a um cliente.

## Autenticação
- **Requerido**: Sim
- **Tipo**: Bearer Token
- **Nível de acesso**: ADMINISTRATOR

## Request Body

### Estrutura JSON

```json
{
  "userId": "string",
  "appliances": [
    {
      "type": "string",
      "brand": "string",
      "model": "string",
      "voltage": "string",
      "serial": "string",
      "customerNote": "string",
      "laborValue": "number"
    }
  ],
  "parts": [
    {
      "componentId": "string",
      "quantity": "number"
    }
  ],
  "nf": "string",
  "returnGuarantee": "boolean",
  "fabricGuarantee": "boolean",
  "discount": "number",
  "receivedAt": "string",
  "deadline": "string",
  "serviceDescription": "string",
  "notes": "string",
  "status": "string",
  "updatedAt": "string"
}
```

## Campos Detalhados

### Campos Obrigatórios

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `userId` | string | ID do cliente (proprietário da OS) |
| `appliances` | array | Lista de eletrodomésticos (mínimo 1 item) |
| `receivedAt` | string | Data de recebimento no formato ISO 8601 ou YYYY-MM-DD |
| `status` | string | Status inicial da OS |

### Campos Opcionais - Nível Raiz

| Campo | Tipo | Default | Descrição |
|-------|------|---------|-----------|
| `parts` | array | [] | Lista de peças utilizadas |
| `discount` | number | 0 | Valor do desconto aplicado |
| `deadline` | string | null | Data de retirada/prazo |
| `serviceDescription` | string | "" | Observações do cliente sobre o serviço |
| `notes` | string | "" | Observações técnicas do responsável |
| `nf` | string | null | Número da nota fiscal (opcional) |
| `returnGuarantee` | boolean | false | Indica se há garantia de retorno |
| `fabricGuarantee` | boolean | false | Indica se há garantia de fábrica |
| `updatedAt` | string | now | Data/hora da última atualização |

**Importante sobre cálculos automáticos**:
- Os campos `partsTotal`, `laborValue` e `totalValue` **não devem ser enviados** pelo frontend
- Estes valores são calculados automaticamente pelo backend por questões de segurança:
  - `partsTotal` = soma de todos os (`part.quantity` × `component.price`)
  - `laborValue` = soma de todos os `appliance.laborValue`
  - `totalValue` = `partsTotal` + `laborValue` - `discount`
- O frontend envia apenas: `appliances` (com `laborValue` individual), `parts` (componentId e quantity) e `discount`
- Cada eletrodoméstico tem seu próprio valor de mão-de-obra (`laborValue`)

### Objeto Appliance (Eletrodoméstico)

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `type` | string | ✅ | Tipo do eletrodoméstico |
| `brand` | string | ❌ | Marca do eletrodoméstico |
| `model` | string | ❌ | Modelo do eletrodoméstico |
| `voltage` | string | ❌ | Voltagem (127 V, 220 V, Bivolt) |
| `serial` | string | ❌ | Número de série |
| `customerNote` | string | ❌ | Observação do cliente sobre este item |
| `laborValue` | number | ✅ | Valor da mão-de-obra para este eletrodoméstico |

**Valores possíveis para `type`:**
- Geladeira
- Micro-ondas
- Cafeteira
- Liquidificador
- Ferro de Passar
- Ar Condicionado
- Máquina de Lavar
- Fogão
- Ventilador
- Outro

### Objeto Part (Peça/Componente)

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `componentId` | string | ✅ | ID do componente existente no estoque |
| `quantity` | number | ✅ | Quantidade utilizada do componente |

**Importante**: 
- As peças devem existir previamente no banco de dados (tabela de componentes)
- O sistema buscará automaticamente as informações do componente (nome, valor unitário) pelo `componentId`
- O valor total será calculado no backend: `quantity × component.price`

### Status Possíveis

| Valor | Descrição |
|-------|-----------|
| `NAO_INICIADO` | Ordem de serviço criada, aguardando início |
| `EM_ANDAMENTO` | Serviço em execução |
| `FINALIZADO` | Serviço concluído |
| `CANCELADO` | Ordem de serviço cancelada |

## Exemplo de Request

```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "appliances": [
    {
      "type": "Geladeira",
      "brand": "Brastemp",
      "model": "BRM45",
      "voltage": "127 V",
      "serial": "ABC123456",
      "customerNote": "Não está gelando a parte de baixo",
      "laborValue": 150.00
    }
  ],
  "parts": [
    {
      "componentId": "comp-001",
      "quantity": 1
    },
    {
      "componentId": "comp-045",
      "quantity": 2
    }
  ],
  "nf": "NF-12345",
  "returnGuarantee": true,
  "fabricGuarantee": false,
  "discount": 25.50,
  "receivedAt": "2025-11-13T10:00:00.000Z",
  "deadline": "2025-11-20T18:00:00.000Z",
  "serviceDescription": "Cliente relata que a geladeira não está gelando adequadamente. Problema começou há 3 dias.",
  "notes": "Verificar sistema de refrigeração e pressão do gás. Possível vazamento identificado.",
  "status": "NAO_INICIADO",
  "updatedAt": "2025-11-13T10:00:00.000Z"
}
```

## Respostas

### Sucesso (201 Created)

```json
{
  "id": "uuid-gerado",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "appliances": [...],
  "parts": [...],
  "partsTotal": 175.50,
  "discount": 25.50,
  "totalValue": 300.00,
  "receivedAt": "2025-11-13T10:00:00.000Z",
  "deadline": "2025-11-20T18:00:00.000Z",
  "serviceDescription": "...",
  "notes": "...",
  "status": "NAO_INICIADO",
  "createdAt": "2025-11-13T10:00:00.000Z",
  "updatedAt": "2025-11-13T10:00:00.000Z"
}
```

### Erro - Dados Inválidos (400 Bad Request)

```json
{
  "error": "Validation Error",
  "message": "Campos obrigatórios ausentes",
  "details": {
    "userId": "Campo obrigatório",
    "appliances": "Deve conter pelo menos um eletrodoméstico",
    "receivedAt": "Data de recebimento é obrigatória"
  }
}
```

### Erro - Componente Não Encontrado (404 Not Found)

```json
{
  "error": "Not Found",
  "message": "Componente com ID 'comp-001' não existe no estoque"
}
```

### Erro - Estoque Insuficiente (422 Unprocessable Entity)

```json
{
  "error": "Insufficient Stock",
  "message": "Estoque insuficiente para o componente 'Termostato'",
  "details": {
    "componentId": "comp-001",
    "requested": 5,
    "available": 2
  }
}
```

### Erro - Cliente Não Encontrado (404 Not Found)

```json
{
  "error": "Not Found",
  "message": "Cliente com ID informado não existe"
}
```

### Erro - Não Autorizado (401 Unauthorized)

```json
{
  "error": "Unauthorized",
  "message": "Token de autenticação inválido ou expirado"
}
```

### Erro - Sem Permissão (403 Forbidden)

```json
{
  "error": "Forbidden",
  "message": "Apenas administradores podem criar ordens de serviço"
}
```

## Regras de Negócio

1. **Cliente deve existir**: O `userId` deve corresponder a um cliente válido no sistema
2. **Eletrodoméstico obrigatório**: Deve haver pelo menos um eletrodoméstico com o campo `type` preenchido
3. **Mão-de-obra obrigatória**: Cada eletrodoméstico **deve** ter um `laborValue` (valor da mão-de-obra)
4. **Componentes devem existir**: Cada `componentId` em `parts` deve corresponder a um componente válido no estoque
5. **Validação de estoque**: O sistema deve verificar se há quantidade suficiente do componente em estoque
6. **Cálculo automático no backend**: 
   - Buscar preço do componente: `component.price`
   - Calcular `part.totalValue` = `part.quantity` × `component.price`
   - Calcular `partsTotal` = soma de todos os `part.totalValue`
   - **Calcular `laborValue` = soma de todos os `appliance.laborValue`**
   - Calcular `totalValue` = `partsTotal` + `laborValue` - `discount`
7. **Status inicial**: Sempre inicializado como `NAO_INICIADO`
8. **Data de recebimento**: Deve ser uma data válida e não pode ser futura
9. **Peças opcionais**: Uma OS pode ser criada sem peças (somente mão-de-obra)
10. **Atualização de estoque**: Ao criar a OS, o estoque dos componentes deve ser decrementado

## Notas Importantes

- Todos os valores monetários devem ser enviados como números decimais (ex: 150.50)
- Datas devem estar no formato ISO 8601 ou YYYY-MM-DD
- Arrays vazios são permitidos para `parts`, mas não para `appliances`
- O campo `updatedAt` é atualizado automaticamente pelo servidor
- Valores negativos não são permitidos para campos monetários
- **Os componentes (parts) devem existir no banco de dados antes de criar a OS**
- **O preço e detalhes dos componentes são buscados automaticamente pelo `componentId`**
- **O estoque é decrementado automaticamente ao criar a OS**
- Se um componente estiver com estoque zerado, a criação da OS será bloqueada
- **Cada eletrodoméstico deve ter seu próprio valor de mão-de-obra (`laborValue`)**
- **O `laborValue` total da OS é a soma de todos os `laborValue` dos eletrodomésticos**
- **Não envie `laborValue` no nível raiz da OS - ele será calculado automaticamente**

## Fluxo de Seleção de Componentes no Frontend

1. **Carregar componentes disponíveis**: 
```javascript
const components = await ComponentService.getAllComponents();
```

2. **Usuário busca componente**: Sistema filtra por nome, código ou descrição

3. **Usuário seleciona componente**: Sistema preenche automaticamente:
   - `componentId`: ID do componente
   - `componentName`: Nome (apenas para exibição no frontend)
   - `unitValue`: Preço do componente (apenas para exibição)

4. **Usuário define quantidade**: Sistema calcula `totalValue` localmente para preview

5. **Ao salvar**: Envia apenas `componentId` e `quantity` para o backend

## Integração com Frontend

O frontend utiliza este endpoint através do `RepairService.createRepair()`:

```javascript
import RepairService from '../../services/RepairService';
import ComponentService from '../../services/ComponentService';

// 1. Carregar componentes disponíveis
const components = await ComponentService.getAllComponents();

// 2. Usuário seleciona componentes e define quantidades
const selectedParts = [
  { componentId: 'comp-001', quantity: 1 },
  { componentId: 'comp-045', quantity: 2 }
];

// 3. Preparar dados da ordem
const orderData = {
  userId: selectedClient.id,
  appliances: appliances.filter(app => app.type.trim()),
  parts: selectedParts, // Apenas ID e quantidade
  receivedAt: '2025-11-13',
  status: 'NAO_INICIADO',
  // ... demais campos
};

// 4. Criar ordem
const newRepair = await RepairService.createRepair(orderData);
```

### Estrutura de Componente no Banco

Para referência, um componente no banco possui:

```json
{
  "id": "comp-001",
  "name": "Termostato",
  "code": "TERM-001",
  "description": "Termostato para geladeiras",
  "price": 85.50,
  "stock": 15,
  "category": "Refrigeração",
  "supplier": "Fornecedor XYZ"
}
```

O backend utilizará estas informações para calcular os valores automaticamente.
