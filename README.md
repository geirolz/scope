# Scope
[![Build Status](https://github.com/geirolz/scope/actions/workflows/cicd.yml/badge.svg)](https://github.com/geirolz/scope/actions)
[![codecov](https://img.shields.io/codecov/c/github/geirolz/scope)](https://codecov.io/gh/geirolz/scope)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/db3274b55e0c4031803afb45f58d4413)](https://www.codacy.com/manual/david.geirola/scope?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=geirolz/scope&amp;utm_campaign=Badge_Grade)
[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/com.github.geirolz/scope-core_2.13?server=https%3A%2F%2Foss.sonatype.org)](https://mvnrepository.com/artifact/com.github.geirolz/scope-core)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://gh.mergify.io/badges/geirolz/scope&style=flat)](https://mergify.io)
[![GitHub license](https://img.shields.io/github/license/geirolz/scope)](https://github.com/geirolz/scope/blob/main/LICENSE)

---

## How to install

```sbt
libraryDependencies += "com.github.geirolz" % "scope" % "0.0.1"
```


## How to use

Work in progress

### Defining the ModelMapper
```scala
import scope.*
import scope.syntax.*

//datatypes
case class UserId(value: Long)
case class Name(value: String)
case class Surname(value: String)

//doman models
case class User(id: UserId, name: Name, surname: Surname)

//http rest contracts
case class UserContract private(id: Long, name: String, surname: String)
object UserContract{    
    implicit val modelMapperForUserContract: ModelMapper[Scope.Endpoint, User, UserContract] =
      ModelMapper.scoped[Scope.Endpoint](user => {
        UserContract(
            user.id.value,
            user.name.value,
            user.surname.value,
        )
      })
}
```

### Using the ModelMapper
To use the ModelMapper you have to provide the right `ScopeContext` implicitly

Given
```scala
val user: User = User(
    UserId(1),
    Name("Foo"),
    Surname("Bar"),
)
```

```scala
implicit val scopeCtx: TypedScopeContext[Scope.Endpoint] = ScopeContext.of[Scope.Endpoint]
// scopeCtx: TypedScopeContext[Scope.Endpoint] = scope.TypedScopeContext@4ecde

user.scoped.as[UserContract]
// res0: UserContract = UserContract(id = 1L, name = "Foo", surname = "Bar")
```


If the `ScopeContext` is wrong or is missing the compilation will fail
```scala
implicit val scopeCtx: TypedScopeContext[Scope.Event] = ScopeContext.of[Scope.Event]

user.scoped.as[UserContract]
// error: diverging implicit expansion for type scope.ModelMapper[scopeCtx.ScopeType,repl.MdocSession.App.User,repl.MdocSession.App.UserContract]
// starting with method liftPureModelMapper in trait ModelMapperKInstances
// user.scoped.as[UserContract]
//               ^
```

