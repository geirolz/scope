package scope.generic

import scope.{ModelMapper, Scope, ScopeContext, TypedScopeContext}

class ModelMapperIdTest extends munit.FunSuite {

  import scope.generic.syntax.*

  test("ModelMapper.deriveId") {

    case class Test(value: String)
    case class Foo(a: String, b: Int, c: Test)
    case class Bar(a: String, b: Int, c: Test)

    implicit val scopeCtx: TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    implicit val m: ModelMapper[Scope.Domain, Foo, Bar] =
      ModelMapper.scoped[Scope.Domain].deriveIdMap[Foo, Bar]

    assertEquals(
      obtained = ModelMapper
        .scoped[Scope.Domain]
        .summon[Foo, Bar]
        .apply(Foo("test", 1, Test("my_test"))),
      expected = Bar("test", 1, Test("my_test"))
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
             ModelMapper.scoped[Scope.Domain].deriveIdMap[Foo, Bar]
          """
      ),
      expected = """error:
                   |
                   |Type Foo and Bar doesn't have the same constructor.
                   |Keep in mind that this macro only support the construction using `new`, smart constructors are not supported yet.
                   |## Type Foo params: 
                   |Map(a -> val a)
                   |
                   |## Type Bar params: 
                   |Map(b -> val b)
                   |
                   |------------------------------
                   |Differences:
                   |Set(a)
                   |      obtained = compileErrors(
                   |                             ^""".stripMargin
    )
  }
}
