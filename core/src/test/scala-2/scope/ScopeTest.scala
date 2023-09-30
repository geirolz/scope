package scope

class ScopeTest extends munit.FunSuite {

  test("Can create custom scope") {
    assertNoDiff(
      obtained = compileErrors("""
         import scope.Scope
         trait MyCustomScope extends Scope
     """),
      expected = ""
    )
  }

  test("Works with subtypes") {

    import scope.syntax.*

    sealed trait EndpointADT
    case class EndpointA() extends EndpointADT
    case class EndpointB() extends EndpointADT

    sealed trait ADT
    case class A() extends ADT
    case class B() extends ADT

    implicit val scopeCtx: TypedScopeContext[Scope.Endpoint] = ScopeContext.of[Scope.Endpoint]
    implicit val scopeMapper: ModelMapper[Scope.Endpoint, EndpointADT, ADT] =
      ModelMapper.scoped[Scope.Endpoint] {
        case EndpointA() => A()
        case EndpointB() => B()
      }

    assertEquals(
      obtained = EndpointA().scoped.as[ADT],
      expected = A()
    )
  }
}
