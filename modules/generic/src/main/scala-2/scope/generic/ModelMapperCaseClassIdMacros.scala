package scope.generic

import cats.Show
import scope.{ModelMapper, Scope}

import scala.reflect.macros.blackbox

object ModelMapperCaseClassIdMacros {

  import cats.Show.*

  def deriveCaseClassIdMap[S <: Scope: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag](
    c: blackbox.Context
  ): c.Expr[ModelMapper[S, A, B]] = {

    import c.universe.*

    implicit val symbolNameTypeShow: Show[Symbol#NameType] =
      Show.fromToString

    implicit val termNameShow: Show[TermName] =
      Show.fromToString

    implicit val typeShow: Show[Type] =
      Show.fromToString

    def getCaseClassFields[TT: c.WeakTypeTag]: Map[c.universe.TermName, c.universe.Type] = {
      weakTypeOf[TT].members.collect {
        case m: MethodSymbol if m.isGetter && m.isPublic => (m.name, m.returnType)
      }.toMap
    }

    val a                            = weakTypeOf[A]
    val b                            = weakTypeOf[B]
    val aFields: Map[TermName, Type] = getCaseClassFields[A]
    val bFields: Map[TermName, Type] = getCaseClassFields[B]
    val diff: Map[TermName, Type]    = (aFields.toSet diff bFields.toSet).toMap

    if (diff.isEmpty) {

      def constructionFunc: c.Expr[A => B] =
        c.Expr[A => B](
          q"""
               (a: ${a.resultType}) => {
                  new ${b.typeSymbol}(
                      ..${aFields.keys.map(name => q"$name = a.$name")}
                  )
               }
            """
        )

      c.Expr[ModelMapper[S, A, B]](
        q"""
         ModelMapper
            .scoped[${weakTypeOf[S].typeSymbol}]
            .apply[${a.typeSymbol}, ${b.typeSymbol}]($constructionFunc)    
         """
      )
    } else {
      c.abort(
        c.enclosingPosition,
        ModelMapperCaseClassIdMessages.abortDueDifferentTypesFields(
          a.typeSymbol.name,
          b.typeSymbol.name,
          aFields,
          bFields,
          diff
        )
      )
    }
  }
}
