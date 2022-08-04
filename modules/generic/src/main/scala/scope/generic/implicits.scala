package scope.generic

object implicits extends AllInstances with AllSyntax
object syntax extends AllSyntax

sealed trait AllInstances
sealed trait AllSyntax extends ModelMapperBuilderMacrosSyntax
