package scope.generic

import scope.{ModelMapper, Scope}
import scala.quoted.*

object ModelMapperCaseClassIdMacros:

  def deriveCaseClassIdMap[S <: Scope: Type, A: Type, B: Type](using
    Quotes
  ): Expr[ModelMapper[S, A, B]] =
    new ModelMapperCaseClassIdMacros().deriveCaseClassIdMapImpl[S, A, B]

class ModelMapperCaseClassIdMacros(using q: Quotes) extends Scala3MacroShowInstances:

  import q.reflect.*
  import cats.syntax.show.*

  def deriveCaseClassIdMapImpl[S <: Scope: Type, A: Type, B: Type]: Expr[ModelMapper[S, A, B]] =

    val a                            = TypeRepr.of[A]
    val b                            = TypeRepr.of[B]
    val aFields: Map[String, Symbol] = getCaseClassFields(a)
    val bFields: Map[String, Symbol] = getCaseClassFields(b)
    val diff: Set[String] =
      aFields.keys.toSet.diff(bFields.keys.toSet)

    if (diff.isEmpty) {

      def createFunctionBody(aExpr: Expr[A]): Expr[B] = {

        val args: List[Term] =
          aFields.map { case (_, symbol) =>
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
        ModelMapperCaseClassIdMessages.abortDueDifferentTypesFields(
          a.typeSymbol.name,
          b.typeSymbol.name,
          aFields,
          bFields,
          diff
        )
      )
  end deriveCaseClassIdMapImpl

  private def getCaseClassFields(repr: TypeRepr): Map[String, Symbol] =
    repr.typeSymbol.caseFields
      .map(symbol => (symbol.name, symbol))
      .toMap
