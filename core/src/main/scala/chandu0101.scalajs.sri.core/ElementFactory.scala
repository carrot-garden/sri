package chandu0101.scalajs.sri.core

import scala.scalajs.js
import scala.scalajs.js.JSConverters.genTravConvertible2JSRichGenTrav


/**
 *  ReactElement builder ,creates ReactElements for ReactComponents
 *
 *  instance - react component instance ReactComponent[P,S]
 *  props - component props
 *  key  - key for react element to be created
 *  ref  - ref for react element to be created
 *  children - element children
 *
 */

sealed trait ISPropsDefined

sealed trait PropsDefined extends ISPropsDefined

sealed trait PropsNotDefined extends ISPropsDefined


final class ElementFactory[HasProps <:ISPropsDefined,P , S ] private(instance: => ReactComponent[P, S],
                                                       keyV: js.UndefOr[String] = js.undefined,
                                                       refV: RefType = null,
                                                       childrenV: Seq[ReactElement] = Nil) {
  def copy[HasProps <: ISPropsDefined](instance: => ReactComponent[P, S] = instance,
                                       keyV: js.UndefOr[String] = keyV,
                                       refV: RefType = refV,
                                       childrenV: Seq[ReactElement] = childrenV) = new ElementFactory[HasProps,P,S](instance,keyV,refV,childrenV)

  private[core] var  _propsV : P = _

  def props(props: P) = {
    val c =  copy[PropsDefined]()
    c._propsV = props
    c
  }

  def key(key: js.UndefOr[String]) = copy[HasProps](keyV = key)

  def ref(ref: RefType) = copy[HasProps](refV = ref)

  def children(c: ReactElement*) = copy[HasProps](childrenV = c.toSeq)

  private def noProps(p: => P)  = props(p).build

  def buildNoProps(implicit ev: Unit =:= P) = noProps(ev(()))

  def build(implicit ev: HasProps =:= PropsDefined) = {
    React.createElement(() => instance,
      JSProps(keyV, if (refV != null) refV else js.undefined, _propsV),
      childrenV.toJSArray).asInstanceOf[ReactElementU[P, S]]
  }

}

object ElementFactory {
  def apply[P ,S ](instance : => ReactComponent[P,S]) = new ElementFactory[PropsNotDefined,P,S](instance)
}
