package scope

class ModelMapperKTest extends munit.FunSuite:

  test("Using ModelMapperK with a wrong ScopeContext in scope should not compile") {
    assert(
      compileErrors("""  
          import scope.syntax.*
          import scope.Scope
          import scope.ModelMapper
          import scala.util.*
          
          given ModelMapperK[Try, Scope.Domain, Int, String] = 
              ModelMapperK.scoped[Scope.Domain](i => Success(i.toString))
              
          given TypedScopeContext[Scope.Event] =
              ScopeContext.of[Scope.Event]
          
          1.scoped.as[Try[String]]                       
     """).nonEmpty
    )
  }

  test("Using ModelMapperK without a ScopeContext in scope should not compile") {
    assertNoDiff(
      obtained = compileErrors("""  
          import scope.syntax.*
          import scope.Scope
          import scope.ModelMapper
          import scala.util.*
          
          given ModelMapperK[Try, Scope.Domain, Int, String] = 
                ModelMapperK.scoped[Scope.Domain](i => Success(i.toString))
          
          1.scoped.as[String]                       
     """),
      expected = """
           |error: No given instance of type scope.ScopeContext was found for parameter ctx of method scoped in class ModelScopeMapperSyntaxOps
           |          1.scoped.as[String]
           |                 ^
           |""".stripMargin
    )
  }

  test("ModelMapper is assignable to ModelMapperK with Id") {
    assertNoDiff(
      obtained = compileErrors("""  
         import cats.Id                               
         import scope.ModelMapperK                    
         import scope.ModelMapper
                             
         val a : ModelMapper[Scope.Domain, String, String] = ModelMapper.scoped[Scope.Domain].id[String]                       
         val b : ModelMapperK[Id, Scope.Domain, String, String] = a                         
     """),
      expected = ""
    )
  }
