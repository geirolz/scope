package scope.generic

import scope.{ModelMapper, Scope}
import scala.quoted.*

object ModelMapperIdMacros:

  def deriveIdMapImpl[S <: Scope: Type, A: Type, B: Type](using
    Quotes
  ): Expr[ModelMapper[S, A, B]] =
    new ModelMapperIdMacros().deriveIdMapImpl[S, A, B]

class ModelMapperIdMacros(using q: Quotes):

  import q.reflect.*

  def deriveIdMapImpl[S <: Scope: Type, A: Type, B: Type]: Expr[ModelMapper[S, A, B]] =

    val a                            = TypeRepr.of[A]
    val b                            = TypeRepr.of[B]
    val aParams: Map[String, Symbol] = getParams(a)
    val bParams: Map[String, Symbol] = getParams(b)
    val diff: Set[String] =
      aParams.keys.toSet.diff(bParams.keys.toSet)

    if (diff.isEmpty) {

      def createFunctionBody(aExpr: Expr[A]): Expr[B] = {

        val args: List[Term] =
          aParams.map { case (_, symbol) =>
            Select(aExpr.asTerm, symbol)
          }.toList

        New(Inferred(b))
          .select(b.classSymbol.get.primaryConstructor)
          .appliedToArgs(args)
          .asExprOf[B]
      }

      '{
        ModelMapper.scoped[S].apply[A, B]((a: A) => ${ createFunctionBody('{ a }) })
      }
    } else
      report.errorAndAbort(
        s"""
           |Type ${a.typeSymbol.name.toString} and ${b.typeSymbol.name.toString} doesn't have the same constructor.
           |Keep in mind that this macro only support the construction using `new`, smart constructors are not supported yet.
           |## Type ${a.typeSymbol.name.toString} params: 
           |$aParams
           |
           |## Type ${b.typeSymbol.name.toString} params: 
           |$bParams
           |
           |------------------------------
           |Differences:
           |$diff
           |""".stripMargin
      )
  end deriveIdMapImpl

  private def getParams(repr: TypeRepr): Map[String, Symbol] =
    repr.typeSymbol.fieldMembers.collect {
      case symbol: Symbol if symbol.flags.is(Flags.ParamAccessor) => (symbol.name, symbol)
    }.toMap
