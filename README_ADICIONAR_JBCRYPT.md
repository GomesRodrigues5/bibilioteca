Instruções para adicionar jBCrypt ao projeto

1) Copiar o JAR para a pasta `src/lib`
- Você pode executar o script PowerShell fornecido:

```powershell
cd "src\scripts"
.\add-jbcrypt.ps1
```

O script tentará copiar `jbcrypt-0.4.jar` de `C:\Users\<seu_usuario>\Downloads` ou permitirá selecionar o arquivo.

2) Verifique no NetBeans
- Abra Project → Properties → Libraries. O JAR `jbcrypt-0.4.jar` deverá aparecer em `src/lib`.
- Se necessário, remova referências antigas com caminho absoluto em `nbproject/project.properties`.

3) Teste rápido
- Rode a classe `util.MigracaoSenhas` no IDE. Se o JAR estiver presente, o `PasswordHasher` usará BCrypt automaticamente para gerar novos hashes.

Observação: sempre faça backup do banco antes de migrar senhas.
