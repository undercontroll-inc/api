# Yaak Collection

Workspace Git-sync da Core API. Não é um export Postman: o Yaak espera um arquivo YAML por recurso.

## Auth

Auth é `Authorization: Bearer`. A pasta **Authenticated** puxa o token (`$.accessToken`) de **Auth / Sign in password**. Login não precisa de pasta Setup nem de cookie/CSRF.

Na prática: suba a API e mande qualquer request autenticado. O Yaak dispara o login com as credenciais da seed (`admin@undercontroll.com` / `123`) se ainda não houver resposta.

**Auth / Sign in (Google)** e **Refresh token** não alimentam o Bearer default.

Não commite `cookie_jar` nem tokens no YAML. O jar local é criado ao abrir o workspace.

## Environments

| Environment | Uso |
|---|---|
| Global | ids da seed, email, senha, `chat_content`, `base_url` default |
| Local | sobrescreve `base_url` para `http://localhost:8080` |

Variáveis de path: `user_id`, `order_id`, `order_item_id`, `demand_id`, `component_id`, `announcement_id`.
