# no-secure-api

APIs inseguras para uso local em testes de segurança. Todas expõem o mesmo
endpoint `GET /object/{id}` e consultam `base_objects`; cada API varia nas
verificações de autenticação/autorização:

| API | Porta local no Compose |
|---|---:|
| `crapi` | 8070 |
| `no-roles-api` | 8071 |
| `no-roles-no-validation-api` | 8072 |
| `no-validation-api` | 8073 |

`crapi` não exige autenticação. As outras APIs exigem um JWT válido:

- `no-roles-api`: verifica se o usuário tem acesso ao objeto pela hierarquia,
  sem validar roles.
- `no-roles-no-validation-api`: valida o JWT, mas não confere roles nem acesso
  ao objeto.
- `no-validation-api`: valida a role `objectrole`, mas não confere acesso ao
  objeto.

Use com a pilha Docker local configurada em `api-authtest`. Não exponha essas
APIs em redes ou ambientes públicos.
