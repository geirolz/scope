package scope

import cats.Id

object ModelMapper {

  private val builder: ModelMapper.Builder[Scope] = new ModelMapper.Builder[Scope]

  def forScope[S <: Scope]: ModelMapper.Builder[S] =
    builder.asInstanceOf[ModelMapper.Builder[S]]

  class Builder[S <: Scope] private[ModelMapper] () {

    def summon[A, B](implicit m: ModelMapper[S, A, B]): ModelMapper[S, A, B] =
      ModelMapperK.forScope[S].summon

    def apply[A, B](f: A => B): ModelMapper[S, A, B] =
      ModelMapperK.forScope[S].apply[Id, A, B](f)

    def pure[A, B](b: B): ModelMapper[S, A, B] =
      ModelMapperK.forScope[S].pure(b)

    def id[A]: ModelMapper[S, A, A] =
      ModelMapperK.forScope[S].id
  }
}
