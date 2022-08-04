package scope.generic

import scope.{ModelMapper, Scope}

import scala.annotation.unused

object implicits extends AllInstances with AllSyntax
object syntax extends AllSyntax

sealed trait AllInstances
sealed trait AllSyntax extends ModelMapperMacrosSyntax

private[generic] trait ModelMapperMacrosSyntax {

  implicit class ModelMapperCompanionGenericOps(@unused co: ModelMapper.type) {
    def deriveIdMap[S <: Scope, A, B]: ModelMapper[S, A, B] =
      macro ModelMapperIdMacros.deriveIdMapImpl[S, A, B]
  }
}
