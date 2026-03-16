# huper-digital-auth (IAM)

Módulo de autenticação e Identity and Access Management (IAM) da Huper Digital.

## Pacote

`huper.digital.iam`

## Uso como dependência

Este repositório pode ser usado como **submódulo Git** no projeto principal:

```bash
git submodule add git@github.com:duartevinicius91/huper-digital-auth.git huper-digital-auth
```

No `settings.gradle` do projeto principal:

```groovy
includeBuild 'huper-digital-auth'
```

No `build.gradle` do projeto principal:

```groovy
dependencies {
    implementation project(':huper-digital-auth')
}
```

## Configuração

Propriedades de configuração (ex.: `application.yaml`):

- `huper.digital.iam.jwt.access-token-ttl-seconds` (default: 3600)
- `huper.digital.iam.jwt.refresh-token-ttl-seconds` (default: 2592000)
- `mp.jwt.verify.issuer` (default: huper.digital)

## Requisitos

- Java 21
- Gradle 8+
- Quarkus 3.26.x
