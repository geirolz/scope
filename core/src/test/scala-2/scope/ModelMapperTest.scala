package scope

import scala.util.{Success, Try}

class ModelMapperTest extends munit.FunSuite {

  test("Summon an implicit ModelMapper") {

    implicit val scopeCtx: TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    implicit val m: ModelMapper[Scope.Domain, Int, String] =
      ModelMapper.scoped[Scope.Domain](_.toString)

    assertEquals(
      obtained = ModelMapper.scoped[Scope.Domain].summon[Int, String].apply(1),
      expected = "1"
    )
  }

  test("Creating a ModelMapper from function A => B") {

    implicit val scopeCtx: TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    val m: ModelMapper[Scope.Domain, Int, String] =
      ModelMapper.scoped[Scope.Domain](_.toString)

    assertEquals(
      obtained = m(1),
      expected = "1"
    )
  }

  test("Creating a ModelMapper with Pure") {

    implicit val scopeCtx: TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    val m: ModelMapper[Scope.Domain, Int, String] = ModelMapper
      .scoped[Scope.Domain]
      .pure("FOO")

    assertEquals(
      obtained = m(1),
      expected = "FOO"
    )
  }

  test("Creating a ModelMapper with Id") {

    implicit val scopeCtx: TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    val m: ModelMapper[Scope.Domain, Int, Int] =
      ModelMapper
        .scoped[Scope.Domain]
        .id

    assertEquals(
      obtained = m(1),
      expected = 1
    )
  }

  test("Lifting a ModelMapper to Try") {

    implicit val scopeCtx: TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    val m: ModelMapperK[Try, Scope.Domain, Int, Int] =
      ModelMapper
        .scoped[Scope.Domain]
        .id
        .lift[Try]

    assertEquals(obtained = m(1), expected = Success(1))
  }

  test("Using ModelMapper with a ScopeContext provided") {

    import scope.syntax.*

    implicit val m: ModelMapper[Scope.Domain, Int, String] =
      ModelMapper.scoped[Scope.Domain](_.toString)

    implicit val scopeCtx: TypedScopeContext[Scope.Domain] =
      ScopeContext.of[Scope.Domain]

    assertEquals(
      obtained = 1.scoped.as[String],
      expected = "1"
    )
  }

  test("Using ModelMapper wit a wrong ScopeContext in scope should not compile") {
    assertNoDiff(
      obtained = compileErrors("""  
          import scope.syntax.*
          import scope.Scope
          import scope.ModelMapper
          
          implicit val m: ModelMapper[Scope.Domain, Int, String] = 
              ModelMapper.scoped[Scope.Domain](_.toString)
              
          implicit val scopeCtx: TypedScopeContext[Scope.Event] =
              ScopeContext.of[Scope.Event]
          
          1.scoped.as[String]                       
     """),
      expected = """
           |error:
           |diverging implicit expansion for type scope.ModelMapper[scopeCtx.ScopeType,Int,String]
           |starting with method liftPureModelMapper in trait ModelMapperKInstances
           |          1.scoped.as[String]                       
           |                     ^
           |""".stripMargin
    )
  }

  test("Using ModelMapper without a ScopeContext in scope should not compile") {
    assertNoDiff(
      obtained = compileErrors("""  
          import scope.syntax.*
          import scope.Scope
          import scope.ModelMapper
          
          implicit val m: ModelMapper[Scope.Domain, Int, String] = ModelMapper.scoped[Scope.Domain](_.toString)
          
          1.scoped.as[String]                       
     """),
      expected = """
           |error: could not find implicit value for parameter ctx: scope.ScopeContext
           |          1.scoped.as[String]                       
           |            ^
           |""".stripMargin
    )
  }

  test("ModelMapperK with Id is assignable to ModelMapper") {
    assertNoDiff(
      obtained = compileErrors("""  
         import cats.Id                               
         import scope.ModelMapperK                    
         import scope.ModelMapper
                             
         val a : ModelMapperK[Id, Scope.Domain, String, String] = ModelMapperK.scoped[Scope.Domain].id                        
         val b : ModelMapper[Scope.Domain, String, String] = a                         
     """),
      expected = ""
    )
  }
}
