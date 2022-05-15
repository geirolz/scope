package scope

object ModelMapper {

  import cats.*

  private val builder: ModelMapper.Builder[Scope] = new ModelMapper.Builder[Scope]

  def scoped[S <: Scope]: ModelMapper.Builder[S] =
    builder.asInstanceOf[ModelMapper.Builder[S]]

  class Builder[S <: Scope] private[ModelMapper] {

    def summon[A, B](implicit m: ModelMapper[S, A, B]): ModelMapper[S, A, B] =
      ModelMapperK.scoped[S].summon

    def apply[A, B](f: A => B): ModelMapper[S, A, B] =
      ModelMapperK.scoped[S].apply[Id, A, B](f)

    def pure[A, B](b: B): ModelMapper[S, A, B] =
      ModelMapperK.scoped[S].pure(b)

    def id[A]: ModelMapper[S, A, A] =
      ModelMapperK.scoped[S].id[Id, A]
  }
}
