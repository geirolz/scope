# Scope
[![Build Status](https://github.com/geirolz/@PRJ_NAME@/actions/workflows/cicd.yml/badge.svg)](https://github.com/geirolz/@PRJ_NAME@/actions)
[![codecov](https://img.shields.io/codecov/c/github/geirolz/@PRJ_NAME@)](https://codecov.io/gh/geirolz/@PRJ_NAME@)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/db3274b55e0c4031803afb45f58d4413)](https://www.codacy.com/manual/david.geirola/@PRJ_NAME@?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=geirolz/@PRJ_NAME@&amp;utm_campaign=Badge_Grade)
[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/@ORG@/scope-core_2.13?nexusVersion=2&server=https%3A%2F%2Foss.sonatype.org)](https://mvnrepository.com/artifact/com.github.geirolz/@PRJ_NAME@-core)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/geirolz/@PRJ_NAME@&style=flat)](https://mergify.io)
[![GitHub license](https://img.shields.io/github/license/geirolz/@PRJ_NAME@)](https://github.com/geirolz/@PRJ_NAME@/blob/main/LICENSE)

---

## How to install

```sbt
libraryDependencies += "@ORG@" % "scope-core" % "@VERSION@"
libraryDependencies += "@ORG@" % "scope-generic" % "@VERSION@"//optional - for scala 2 and 3
```


## How to use

### Defining the ModelMapper

Given
```scala mdoc
import scope.*
import scope.syntax.*

//datatypes
case class UserId(value: Long)
case class Name(value: String)
case class Surname(value: String)

//doman models
case class User(id: UserId, name: Name, surname: Surname)

//http rest contracts
case class UserContract(id: Long, name: String, surname: String)
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

##### Side effects
If the conversion has side effects you can use `ModelMapperK` instead.
```scala mdoc:nest
import scala.util.Try

implicit val modelMapperKForUserContract: ModelMapperK[Try, Scope.Endpoint, User, UserContract] =
  ModelMapperK.scoped[Scope.Endpoint](user => Try {
    UserContract(
        user.id.value,
        user.name.value,
        user.surname.value,
    )
  })
```

##### Same fields different model
Often in order to decouple things we just duplicate the same model changing just the name. 
For example we could find `UserContract` form the endpoint and `User` from the domain that are actually equals deferring only on the name.

In these case macros can same us some boilerplate, importing the `scope-generic` module you can use `deriveCaseClassIdMap` to derive
the `ModelMapper` that map the object using the same fields. If the objects aren't equals from the signature point of view the compilation will fail.
Keep in mind that this macro only supports the primary constructor, smart constructors are not supported.

```scala mdoc:nest

case class User(id: UserId, name: Name, surname: Surname)

case class UserContract(id: UserId, name: Name, surname: Surname)
object UserContract{    
        
    import scope.*
    import scope.generic.syntax.*
        
    implicit val modelMapperForUserContract: ModelMapper[Scope.Endpoint, User, UserContract] =
      ModelMapper.scoped[Scope.Endpoint].deriveCaseClassIdMap[User, UserContract]
}

```

### Using the ModelMapper
To use the ModelMapper you have to provide the right `ScopeContext` implicitly

Given
```scala mdoc:silent
val user: User = User(
    UserId(1),
    Name("Foo"),
    Surname("Bar"),
)
```

```scala mdoc:nest
implicit val scopeCtx: TypedScopeContext[Scope.Endpoint] = ScopeContext.of[Scope.Endpoint]

user.scoped.as[UserContract]
```

##### Side effects
If the conversion has side effects you have to write 
```scala mdoc:nest
import scala.util.Try

user.scoped.as[Try[UserContract]]
```

In this case if you don't have a `ModelMapperK` defined but just a `ModelMapper` if an `Applicative` instance 
is available in the scope for your effect `F[_]` the pure `ModelMapper` will be lifted using `Applicative[F].pure(...)`


### ScopeContext
If the `ScopeContext` is wrong or is missing the compilation will fail
```scala mdoc:nest:fail
implicit val scopeCtx: TypedScopeContext[Scope.Event] = ScopeContext.of[Scope.Event]

user.scoped.as[UserContract]
```

