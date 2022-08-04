package scope

class ScopeContextTest extends munit.FunSuite {

  test("Multiple ScopeContext for same Scope must be equals") {
    assertEquals(ScopeContext.of[Scope.Domain], ScopeContext.of[Scope.Domain])
  }

  test("Cannot create a ScopeContext for the wrong Scope type") {
    assertNoDiff(
      obtained = compileErrors("""
         implicit val a: TypedScopeContext[Scope.Domain] = ScopeContext.of[Scope.Endpoint]
     """),
      expected = """
         |error:
         |type mismatch;
         | found   : scope.TypedScopeContext[scope.Scope.Endpoint]
         | required: scope.TypedScopeContext[scope.Scope.Domain]
         |         implicit val a: TypedScopeContext[Scope.Domain] = ScopeContext.of[Scope.Endpoint]
         |                                                                          ^
         |""".stripMargin
    )
  }

  test("Cannot have multiple ScopeContext in the same scope") {
    assertNoDiff(
      obtained = compileErrors("""
         implicit val a: TypedScopeContext[Scope.Domain] = ScopeContext.of[Scope.Domain]
         implicit val b: TypedScopeContext[Scope.Endpoint] = ScopeContext.of[Scope.Endpoint]

         implicitly[ScopeContext]
     """),
      expected = """
           |error:
           |ambiguous implicit values:
           | both value b of type scope.TypedScopeContext[scope.Scope.Endpoint]
           | and value a of type scope.TypedScopeContext[scope.Scope.Domain]
           | match expected type scope.ScopeContext
           |         implicitly[ScopeContext]
           |                   ^
           |""".stripMargin
    )
  }
}
