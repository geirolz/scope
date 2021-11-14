package scope

class ScopeTest extends munit.FunSuite {

  test("Cannot create a ScopeContext for the wrong Scope type") {
    assertNoDiff(
      obtained = compileErrors("""
         import scope.Scope
         trait MyCustomScope extends Scope
     """),
      expected = ""
    )
  }
}
