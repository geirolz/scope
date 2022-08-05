package scope.generic

import scope.{ModelMapper, Scope}

import scala.reflect.macros.blackbox

object ModelMapperIdMacros {

  def deriveIdMapImpl[S <: Scope: c.WeakTypeTag, A: c.WeakTypeTag, B: c.WeakTypeTag](
    c: blackbox.Context
  ): c.Expr[ModelMapper[S, A, B]] = {
    import c.universe.*

    def getMembers[TT: c.WeakTypeTag]: Map[c.universe.TermName, c.universe.Type] = {
      weakTypeOf[TT].members.collect {
        case m: MethodSymbol if m.isGetter && m.isPublic => (m.name, m.returnType)
      }.toMap
    }

    val a                            = weakTypeOf[A]
    val b                            = weakTypeOf[B]
    val aParams: Map[TermName, Type] = getMembers[A]
    val bParams: Map[TermName, Type] = getMembers[B]
    val diff: Map[TermName, Type]    = (aParams.toSet diff bParams.toSet).toMap

    if (diff.isEmpty) {

      def constructionFunc: c.Expr[A => B] =
        c.Expr[A => B](
          q"""
               (a: ${a.resultType}) => {
                  new ${b.typeSymbol}(
                      ..${aParams.keys.map(name => q"$name = a.$name")}
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
    } else
      c.abort(
        c.enclosingPosition,
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
  }
}
