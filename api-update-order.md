# API - Editar Ordem de Serviço (OS)

## Endpoints

### Atualização Completa
```
PUT /repairs/{id}
```

### Atualização Parcial
```
PATCH /repairs/{id}
```

## Descrição
Estes endpoints permitem atualizar uma ordem de serviço existente. O método `PUT` substitui completamente o registro, enquanto o `PATCH` atualiza apenas os campos enviados.

**Recomendação**: Utilize `PATCH` para atualizações parciais (ex: alterar apenas o status ou remover itens).

## Autenticação
- **Requerido**: Sim
- **Tipo**: Bearer Token
- **Nível de acesso**: ADMINISTRATOR

## Parâmetros de URL

| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `id` | string/number | ID único da ordem de serviço |

## Request Body (PATCH)

### Estrutura JSON

```json
{
  "status": "string",
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
      "name": "string",
      "quantity": "number",
      "unitValue": "number",
      "totalValue": "number"
    }
  ],
  "partsTotal": "number",
  "laborValue": "number",
  "discount": "number",
  "totalValue": "number",
  "deadline": "string",
  "warranty": "string",
  "serviceDescription": "string",
  "notes": "string"
}
```

## Campos Editáveis

### Campos de Status

| Campo | Tipo | Descrição | Editável via UI |
|-------|------|-----------|-----------------|
| `status` | string | Status da ordem de serviço | ✅ Sim |

**Transições de Status Permitidas:**
- `NAO_INICIADO` → `EM_ANDAMENTO`
- `NAO_INICIADO` → `CANCELADO`
- `EM_ANDAMENTO` → `FINALIZADO`
- `EM_ANDAMENTO` → `CANCELADO`

### Campos de Eletrodomésticos

| Campo | Tipo | Descrição | Editável via UI |
|-------|------|-----------|-----------------|
| `appliances` | array | Lista completa de eletrodomésticos | ✅ Sim (remoção) |

**Operações permitidas:**
- ✅ Remover eletrodomésticos existentes
- ❌ Adicionar novos eletrodomésticos (não implementado na UI atual)
- ❌ Editar campos de eletrodomésticos existentes (não implementado na UI atual)

### Campos de Peças

| Campo | Tipo | Descrição | Editável via UI |
|-------|------|-----------|-----------------|
| `parts` | array | Lista completa de peças | ✅ Sim (remoção) |

**Operações permitidas:**
- ✅ Remover peças existentes
- ❌ Adicionar novas peças (não implementado na UI atual)
- ❌ Editar campos de peças existentes (não implementado na UI atual)

### Campos Não Editáveis

Os seguintes campos **NÃO** podem ser alterados após a criação:

| Campo | Motivo |
|-------|--------|
| `userId` | Cliente não pode ser alterado |
| `receivedAt` | Data de recebimento é fixa |
| `createdAt` | Data de criação é imutável |
| `id` | Identificador único imutável |

### Campos Opcionais para Atualização

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `deadline` | string | Data de retirada/prazo |
| `warranty` | string | Período de garantia |
| `serviceDescription` | string | Observações do cliente |
| `notes` | string | Observações técnicas |
| `discount` | number | Valor do desconto |
| `laborValue` | number | Valor da mão-de-obra |
| `partsTotal` | number | Valor total das peças |
| `totalValue` | number | Valor total da OS |

## Exemplos de Request

### Exemplo 1: Alterar apenas o Status

```http
PATCH /repairs/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json

{
  "status": "EM_ANDAMENTO"
}
```

### Exemplo 2: Remover um Eletrodoméstico

```http
PATCH /repairs/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json

{
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
  ]
}
```

**Nota**: Para remover, envie apenas os itens que devem permanecer. O array enviado substitui completamente o array anterior.

### Exemplo 3: Remover uma Peça

```http
PATCH /repairs/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json

{
  "parts": [
    {
      "name": "Termostato",
      "quantity": 1,
      "unitValue": 85.50,
      "totalValue": 85.50
    }
  ]
}
```

### Exemplo 4: Atualização Múltipla (Status + Remoções)

```http
PATCH /repairs/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json

{
  "status": "FINALIZADO",
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
      "name": "Termostato",
      "quantity": 1,
      "unitValue": 85.50,
      "totalValue": 85.50
    }
  ],
  "notes": "Serviço concluído com sucesso. Testado e funcionando normalmente."
}
```

## Respostas

### Sucesso (200 OK)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "status": "EM_ANDAMENTO",
  "appliances": [...],
  "parts": [...],
  "partsTotal": 175.50,
  "laborValue": 150.00,
  "discount": 25.50,
  "totalValue": 300.00,
  "receivedAt": "2025-11-13T10:00:00.000Z",
  "deadline": "2025-11-20T18:00:00.000Z",
  "warranty": "90 dias",
  "serviceDescription": "...",
  "notes": "...",
  "createdAt": "2025-11-13T10:00:00.000Z",
  "updatedAt": "2025-11-13T14:30:00.000Z"
}
```

### Erro - OS Não Encontrada (404 Not Found)

```json
{
  "error": "Not Found",
  "message": "Ordem de serviço com ID informado não existe"
}
```

### Erro - Dados Inválidos (400 Bad Request)

```json
{
  "error": "Validation Error",
  "message": "Dados de atualização inválidos",
  "details": {
    "status": "Status inválido. Valores permitidos: NAO_INICIADO, EM_ANDAMENTO, FINALIZADO, CANCELADO",
    "appliances": "Lista de eletrodomésticos não pode estar vazia"
  }
}
```

### Erro - Transição de Status Inválida (422 Unprocessable Entity)

```json
{
  "error": "Invalid Transition",
  "message": "Não é possível alterar status de FINALIZADO para EM_ANDAMENTO"
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
  "message": "Apenas administradores podem editar ordens de serviço"
}
```

## Regras de Negócio

### Validações Gerais
1. **OS deve existir**: O ID informado deve corresponder a uma OS válida
2. **Campos imutáveis**: `userId`, `receivedAt`, `createdAt` e `id` não podem ser alterados
3. **Atualização automática**: O campo `updatedAt` é atualizado automaticamente pelo servidor

### Validações de Status
1. **Status válido**: Deve ser um dos valores: `NAO_INICIADO`, `EM_ANDAMENTO`, `FINALIZADO`, `CANCELADO`
2. **Transições lógicas**: Certas transições de status podem ser bloqueadas (ex: FINALIZADO → NAO_INICIADO)

### Validações de Eletrodomésticos
1. **Não pode ficar vazio**: Ao editar `appliances`, deve haver pelo menos 1 item
2. **Tipo obrigatório**: Cada eletrodoméstico deve ter o campo `type` preenchido
3. **Remoção completa**: Para remover, envie apenas os itens que devem permanecer

### Validações de Peças
1. **Pode ficar vazio**: Uma OS pode não ter peças (apenas mão-de-obra)
2. **Campos obrigatórios**: Se houver peças, cada uma deve ter `name`, `quantity`, `unitValue` e `totalValue`
3. **Cálculo correto**: `totalValue` deve ser igual a `quantity` × `unitValue`

### Validações de Valores
1. **Valores positivos**: Campos monetários não podem ser negativos
2. **Recálculo de totais**: Ao remover peças, `partsTotal` e `totalValue` devem ser recalculados

## Fluxo de Edição no Frontend

O componente `OrderDetailModal` gerencia as edições da seguinte forma:

### 1. Ativar Modo de Edição
```javascript
const [isEditing, setIsEditing] = useState(false);
const [editedRepair, setEditedRepair] = useState(null);

// Clique no botão "Editar"
setIsEditing(true);
```

### 2. Realizar Alterações
```javascript
// Alterar status
const handleStatusChange = (newStatus) => {
  setEditedRepair(prev => ({ ...prev, status: newStatus }));
};

// Remover eletrodoméstico
const handleRemoveAppliance = (index) => {
  setEditedRepair(prev => ({
    ...prev,
    appliances: prev.appliances.filter((_, i) => i !== index)
  }));
};

// Remover peça
const handleRemovePart = (index) => {
  setEditedRepair(prev => ({
    ...prev,
    parts: prev.parts.filter((_, i) => i !== index)
  }));
};
```

### 3. Salvar Alterações
```javascript
const handleSave = async () => {
  try {
    setIsSaving(true);
    
    const dataToUpdate = {
      status: editedRepair.status,
      appliances: editedRepair.appliances,
      parts: editedRepair.parts
    };

    await RepairService.patchRepair(repair.id, dataToUpdate);
    
    toast.success('Ordem de serviço atualizada com sucesso!');
    setIsEditing(false);
    
    if (onUpdate) {
      await onUpdate(); // Atualiza a lista
    }
    
    onClose(); // Fecha o modal
  } catch (error) {
    toast.error('Erro ao atualizar ordem de serviço');
  } finally {
    setIsSaving(false);
  }
};
```

### 4. Cancelar Edição
```javascript
const handleCancelEdit = () => {
  // Restaura dados originais
  setEditedRepair({
    ...repair,
    appliances: Array.isArray(repair.appliances) ? [...repair.appliances] : [],
    parts: Array.isArray(repair.parts) ? [...repair.parts] : []
  });
  setIsEditing(false);
};
```

## Integração com Frontend

```javascript
import RepairService from '../../services/RepairService';

// Atualização parcial (recomendado)
const dataToUpdate = {
  status: 'EM_ANDAMENTO',
  appliances: filteredAppliances,
  parts: filteredParts
};

const updatedRepair = await RepairService.patchRepair(repairId, dataToUpdate);
```

## Diferença entre PUT e PATCH

### PUT (Substituição Completa)
- Substitui **todos** os campos da OS
- Requer envio de **todos** os campos obrigatórios
- Usado raramente para OS

```javascript
await RepairService.updateRepair(id, completeOrderData);
```

### PATCH (Atualização Parcial) - **Recomendado**
- Atualiza apenas os campos enviados
- Não requer campos obrigatórios se não forem alterados
- Usado para alterações pontuais

```javascript
await RepairService.patchRepair(id, { status: 'FINALIZADO' });
```

## Casos de Uso Comuns

### 1. Iniciar Serviço
```json
PATCH /repairs/{id}
{
  "status": "EM_ANDAMENTO"
}
```

### 2. Finalizar Serviço
```json
PATCH /repairs/{id}
{
  "status": "FINALIZADO",
  "notes": "Serviço concluído. Equipamento testado e funcionando."
}
```

### 3. Cancelar Serviço
```json
PATCH /repairs/{id}
{
  "status": "CANCELADO",
  "notes": "Cliente desistiu do serviço."
}
```

### 4. Ajustar Lista de Peças (remover item)
```json
PATCH /repairs/{id}
{
  "parts": [/* apenas as peças que devem permanecer */],
  "partsTotal": 85.50,
  "totalValue": 235.50
}
```

## Notas Importantes

- O campo `updatedAt` é atualizado automaticamente em toda edição
- Para remover itens de arrays, envie o array completo sem os itens removidos
- Não é possível adicionar novos eletrodomésticos ou peças via UI atual (funcionalidade futura)
- Todas as edições são registradas com timestamp de atualização
- Recomenda-se sempre buscar a OS atualizada após a edição para garantir sincronização
