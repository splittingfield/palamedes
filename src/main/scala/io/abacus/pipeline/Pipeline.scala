package io.abacus.pipeline

// scalastyle:off spaces.after.plus spaces.before.plus
trait Pipeline[-I,+O,+R] { self =>
  def andThen[O2, U >: R](next: Pipeline[O,O2,U]): Pipeline[I,O2,U] = {
    new Pipeline[I,O2,U] {
      override def process(elem: I): O2 = {
        val o = self.process(elem)
        next.process(o)
      }

      override def results: U = next.results
    }
  }

  def alongWith[U <:I, T >: O, R2](and: Pipeline[U,T,R2]): Pipeline[U,T,(R,R2)] = {
    new Pipeline[U,T,(R,R2)] {
      override def process(elem: U): T = {
        self.process(elem)
        and.process(elem)
      }

      override def results: (R,R2) = (self.results, and.results)
    }
  }

  def pipeResults[O2,R2](next: Pipeline[R,O2,R2]): Pipeline[I,O2,R2] = {
    new Pipeline[I,O2,R2] {
      override def process(elem: I): O2 = {
        self.process(elem)
        next.process(self.results)
      }

      override def results: R2 = next.results
    }
  }

  def results: R
  def process(elem: I): O
}

trait Transformer[-I,+O] extends Pipeline[I,O,Nothing] {
  override def results: Nothing = ???
}
