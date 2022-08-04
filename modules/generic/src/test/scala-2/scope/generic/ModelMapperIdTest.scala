package scope.generic

import scope.{ModelMapper, Scope, ScopeContext, TypedScopeContext}

class ModelMapperIdTest extends munit.FunSuite {

  import scope.generic.syntax.*

  test("ModelMapper.deriveId") {

    case class Foo(a: String)
    case class Bar(a: String)

    implicit val scopeCtx: TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    implicit val m: ModelMapper[Scope.Domain, Foo, Bar] =
      ModelMapper.deriveIdMap[Scope.Domain, Foo, Bar]

    assertEquals(
      obtained = ModelMapper
        .scoped[Scope.Domain]
        .summon[Foo, Bar]
        .apply(Foo("test")),
      expected = Bar("test")
    )
  }

  test(
    "ModelMapper.deriveId should not compile if the 2 types doesn't have the same constructor"
  ) {
    assertNoDiff(
      obtained = compileErrors(
        """
          import scope.generic.syntax.*
 
          case class Foo(a: String)
          case class Bar(b: Int)
          
          implicit val scopeCtx: TypedScopeContext[Scope.Domain] =
             ScopeContext.of[Scope.Domain]
          
          implicit val m: ModelMapper[Scope.Domain, Foo, Bar] =
             ModelMapper.deriveIdMap[Scope.Domain, Foo, Bar]
          """
      ),
      expected = """
          |error:
          |
          |Type Foo and Bar doesn't have the same constructor.
          |Keep in mind that this macro only support the construction using `new`, smart constructors are not supported yet.
          |## Type Foo public members: 
          |Map(a -> String)
          |
          |## Type Bar public members: 
          |Map(b -> Int)
          |
          |------------------------------
          |Differences:
          |Map(a -> String)
          |
          |             ModelMapper.deriveIdMap[Scope.Domain, Foo, Bar]
          |                                    ^""".stripMargin
    )
  }
}
