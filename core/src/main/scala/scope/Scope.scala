package scope

trait Scope
object Scope {
  trait Domain extends Scope
  trait Persistence extends Scope
  trait PersistenceRead extends Scope
  trait PersistenceWrite extends Scope
  trait Endpoint extends Scope
  trait Event extends Scope
  trait Command extends Scope
  trait Read extends Scope
  trait Write extends Scope
}
