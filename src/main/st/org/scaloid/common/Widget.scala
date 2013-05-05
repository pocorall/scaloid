$license()$

package org.scaloid.common

import android.app._
import admin.DevicePolicyManager
import android.view._
import android.net._
import android.os._
import android.media._
import collection.mutable.ArrayBuffer
import android.util.Log

import android.text._
import android.view.accessibility._
import android.accounts._
import android.view.inputmethod._
import android.location._
import android.hardware._
import android.telephony._
import android.net.wifi._
import android.content._
import android.widget._
import android.inputmethodservice._
import android.preference._
import android.preference.Preference._
import android.view.WindowManager.LayoutParams._
import android.view.View._
import android.graphics.drawable.Drawable
import java.lang.CharSequence
import scala.Int
import android.view.ContextMenu.ContextMenuInfo
import android.text.method._
import android.gesture._
import android.appwidget._
import annotation.target.{beanGetter, getter}
import android.view.ViewGroup.LayoutParams
import android.widget.TextView.OnEditorActionListener
import android.graphics._
import android.opengl._

import language.implicitConversions


trait WidgetFamily {

  class ObjectSView[O <: View with TraitView[O] : Manifest] {
    def apply[LP <: ViewGroupLayoutParams[_, O]]()(implicit m: Manifest[O], context: Context, defaultLayoutParam: O => LP): O =  {
      val v = m.erasure.getConstructor(classOf[Context]).newInstance(context).asInstanceOf[O]
      v.<<.parent.+=(v)
      v
    }
  }
  
  class ObjectSTextView[O <: TextView with TraitTextView[O] : Manifest] extends ObjectSView[O] {
    def apply[LP <: ViewGroupLayoutParams[_, O]](txt: CharSequence)(implicit m: Manifest[O], context: Context, defaultLayoutParam: O => LP): O =  {
      val v = m.erasure.getConstructor(classOf[Context]).newInstance(context).asInstanceOf[O]
      v text txt
      v.<<.parent.+=(v)
      v
    }
  }
  
  class ObjectSButton[O <: Button with TraitButton[O] : Manifest] extends ObjectSTextView[O] {
    def apply[LP <: ViewGroupLayoutParams[_, O]](txt: CharSequence, onClickListener: (View) => Unit)(implicit m: Manifest[O], context: Context, defaultLayoutParam: O => LP): O =  {
      val v = m.erasure.getConstructor(classOf[Context]).newInstance(context).asInstanceOf[O]
      v text txt
      v.setOnClickListener(func2ViewOnClickListener(onClickListener))
      v.<<.parent.+=(v)
      v
    }
	
	def apply[LP <: ViewGroupLayoutParams[_, O]](txt: CharSequence, onClickListener: OnClickListener = {})(implicit m: Manifest[O], context: Context, defaultLayoutParam: O => LP): O =  {
      val v = m.erasure.getConstructor(classOf[Context]).newInstance(context).asInstanceOf[O]
      v text txt
      v.setOnClickListener(onClickListener)
      v.<<.parent.+=(v)
      v
    }
  }

  $openRichClassDef(android.view.View, "ConstantsSupport")$

    def find[V <: View](id: Int): V = basis.findViewById(id).asInstanceOf[V]

    def uniqueId(implicit activity: Activity): Int = {
      if(basis.getId < 0) {
        basis.setId(getUniqueId)
      }
      basis.getId
    }

    val FILL_PARENT = ViewGroup.LayoutParams.FILL_PARENT
$if(ver.gte_8)$
    val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
$else$
    val MATCH_PARENT = ViewGroup.LayoutParams.FILL_PARENT
$endif$
    val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT

    def <<[LP <: ViewGroupLayoutParams[_,_]](implicit defaultLayoutParam: (V) => LP): LP =
      defaultLayoutParam(basis)

    protected def parentViewGroupIfExists[LP <: ViewGroupLayoutParams[_,_]]
        (implicit defaultLayoutParam: (V) => LP = (v:V)=> null): TraitViewGroup[_] = {
      val lp = defaultLayoutParam(basis)
      if(lp==null) null else lp.parent
    }

    def <<[LP <: ViewGroupLayoutParams[_,_]](width:Int, height:Int)(implicit defaultLayoutParam: (V) => LP): LP = {
      val lp = defaultLayoutParam(basis)
      lp.height = height
      lp.width = width
      lp
    }

    def basis: V

    val parentViewGroup: TraitViewGroup[_] = null

    @inline def padding_=(p: Int) = {
      basis.setPadding(p, p, p, p)
      basis
    }

    @inline def padding(p: Int) = padding_=(p)

    @noEquivalentGetterExists
    @inline def padding: Int = 0

  $closeRichClassDef(android.view.View)$

  $wholeClassDef(android.widget.TextView, "TraitView[V]")$

  trait TraitAbsListView[V <: AbsListView] extends TraitView[V] {
    $properties(android.widget.AbsListView)$
  }

  
  $openRichClassDef(android.view.ViewGroup, "TraitView[V]")$

    implicit val pagentVG = this

    def applyStyle(v: View): View = {
      var viw = v
      if (parentViewGroup != null) viw = parentViewGroup.applyStyle(viw)
      styles.foreach { st =>
        if (st.isDefinedAt(viw)) viw = st(viw)
      }
      viw
    }

    def +=(v: View) = {
      var viw = v
      viw = applyStyle(viw)
      basis.addView(viw)
      basis
    }

    val styles = new ArrayBuffer[View PartialFunction View]

    def style(stl: View PartialFunction View) = {
      styles += stl
      basis
    }
  
  $closeRichClassDef(android.view.ViewGroup)$



  trait ViewGroupLayoutParams[LP <: ViewGroupLayoutParams[_,_], V <: View] extends ViewGroup.LayoutParams {
    def basis: LP

    def fill = {
      width = ViewGroup.LayoutParams.MATCH_PARENT
      height = ViewGroup.LayoutParams.MATCH_PARENT
      basis
    }
    def wrap = {
      width = ViewGroup.LayoutParams.WRAP_CONTENT
      height = ViewGroup.LayoutParams.WRAP_CONTENT
      basis
    }

    def parent : TraitViewGroup[_]

    def >> : V
  }

  trait ViewGroupMarginLayoutParams[LP <: ViewGroupMarginLayoutParams[_,_], V <: View] extends ViewGroup.MarginLayoutParams with ViewGroupLayoutParams[LP, V] {

    def marginBottom(size: Int) = {
      bottomMargin = size
      basis
    }

    def marginTop(size: Int) = {
      topMargin = size
      basis
    }

    def marginLeft(size: Int) = {
      leftMargin = size
      basis
    }

    def marginRight(size: Int) = {
      rightMargin = size
      basis
    }

    def margin(size:Int) = {
      bottomMargin = size
      topMargin = size
      leftMargin = size
      rightMargin = size
      basis
    }

    def margin(top:Int, right:Int, bottom:Int, left:Int) = {
      bottomMargin = bottom
      topMargin = top
      leftMargin = left
      rightMargin = right
      basis
    }
  }


  $richClassDef(android.widget.FrameLayout, "TraitViewGroup[V]")$

  $openConcreteClassDef(android.widget.FrameLayout)$

    implicit def defaultLayoutParams[V <: View](v: V): LayoutParams[V] = new LayoutParams(v)
    <<

    class LayoutParams[V <: View](v: V) extends FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams[V], V] {

      def basis = this

      v.setLayoutParams(this)

      def Gravity(g: Int) = {
        gravity = g
        this
      }

      def parent = SFrameLayout.this

      def >> : V = v

    }

  $closeConcreteClassDef()$


  $richClassDef(android.widget.RelativeLayout, "TraitViewGroup[V]")$

  $openConcreteClassDef(android.widget.RelativeLayout)$

    implicit def defaultLayoutParams[V <: View](v: V): LayoutParams[V] = new LayoutParams(v)
    <<

    class LayoutParams[V <: View](v: V) extends RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams[V], V] {
      def basis = this

      v.setLayoutParams(this)

      def Gravity(g: Int) = {
        gravity = g
        this
      }

      def above(otherView: View)(implicit activity: Activity) = {
        addRule(RelativeLayout.ABOVE, otherView.uniqueId)
        this
      }

      def alignBaseline = {
        addRule(RelativeLayout.ALIGN_BASELINE)
        this
      }

      def alignBottom = {
        addRule(RelativeLayout.ALIGN_BOTTOM)
        this
      }

      //def alignEnd = {
      //  addRule(RelativeLayout.ALIGN_END)
      //  this
      //}

      def alignLeft = {
        addRule(RelativeLayout.ALIGN_LEFT)
        this
      }

      def alignParentBottom = {
        addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        this
      }

      //def alignParentEnd = {
      //  addRule(RelativeLayout.ALIGN_PARENT_END)
      //  this
      //}

      def alignParentLeft = {
        addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        this
      }

      def alignParentRight = {
        addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        this
      }

      //def alignParentStart = {
      //  addRule(RelativeLayout.ALIGN_PARENT_START)
      //  this
      //}

      def alignParentTop = {
        addRule(RelativeLayout.ALIGN_PARENT_TOP)
        this
      }

      def alignRight = {
        addRule(RelativeLayout.ALIGN_RIGHT)
        this
      }

      //def alignStart = {
      //  addRule(RelativeLayout.ALIGN_START)
      //  this
      //}

      def alignTop = {
        addRule(RelativeLayout.ALIGN_TOP)
        this
      }

      def below(otherView: View)(implicit activity: Activity) = {
        addRule(RelativeLayout.BELOW, otherView.uniqueId)
        this
      }

      def leftOf(otherView: View)(implicit activity: Activity) = {
        addRule(RelativeLayout.LEFT_OF, otherView.uniqueId)
        this
      }

      def rightOf(otherView: View)(implicit activity: Activity) = {
        addRule(RelativeLayout.RIGHT_OF, otherView.uniqueId)
        this
      }

      def centerHorizontal = {
        addRule(RelativeLayout.CENTER_HORIZONTAL)
        this
      }

      def centerInParent = {
        addRule(RelativeLayout.CENTER_IN_PARENT)
        this
      }

      def centerVertical = {
        addRule(RelativeLayout.CENTER_VERTICAL)
        this
      }

      def parent = SRelativeLayout.this

      def >> : V = v

    }

  $closeConcreteClassDef()$


  $richClassDef(android.widget.LinearLayout, "TraitViewGroup[V]")$

  $openConcreteClassDef(android.widget.LinearLayout)$

    val VERTICAL = LinearLayout.VERTICAL
    val HORIZONTAL = LinearLayout.HORIZONTAL

    implicit def defaultLayoutParams[V <: View](v: V): LayoutParams[V] = new LayoutParams(v)
    <<

    class LayoutParams[V <: View](v: V) extends LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) with ViewGroupMarginLayoutParams[LayoutParams[V], V] {

      def basis = this

      v.setLayoutParams(this)

      def Gravity(g: Int) = {
        gravity = g
        this
      }

      def Weight(w: Float) = {
        weight = w
        this
      }
      def parent = SLinearLayout.this

      def >> : V = v

    }

  $closeConcreteClassDef()$

  class SVerticalLayout(implicit context: Context, parentVGroup: TraitViewGroup[_] = null) extends SLinearLayout {
    orientation = VERTICAL
  }

  $wholeClassDef(android.widget.EditText, "TraitTextView[V]", textViewBody)$
  $wholeClassDef(android.inputmethodservice.ExtractEditText, "TraitEditText[V]", textViewBody)$
  $wholeClassDef(android.widget.AutoCompleteTextView, "TraitEditText[V]", textViewBody)$
  $wholeClassDef(android.widget.ListView, "TraitAbsListView[V]")$
  $wholeClassDef(android.widget.Button, "TraitTextView[V]", buttonObjectBody)$
  $wholeClassDef(android.widget.CompoundButton, "TraitButton[V]")$
  $wholeClassDef(android.widget.CheckBox, "TraitCompoundButton[V]", buttonObjectBody)$
  $wholeClassDef(android.widget.RadioButton, "TraitCompoundButton[V]", buttonObjectBody)$
  $wholeClassDef(android.widget.ToggleButton, "TraitCompoundButton[V]", buttonObjectBody)$
  $wholeClassDef(android.widget.CheckedTextView, "TraitTextView[V]", textviewBody)$
  $wholeClassDef(android.widget.Chronometer, "TraitTextView[V]", textviewBody)$
  $wholeClassDef(android.widget.DigitalClock, "TraitTextView[V]", textviewBody)$
  $richClassDef(android.inputmethodservice.KeyboardView, "TraitView[V]")$
  $wholeClassDef(android.widget.ImageView, "TraitView[V]")$
  $wholeClassDef(android.widget.ImageButton, "TraitImageView[V]")$
  $wholeClassDef(android.widget.QuickContactBadge, "TraitImageView[V]")$
  $wholeClassDef(android.widget.ZoomButton, "TraitImageButton[V]")$
  $wholeClassDef(android.widget.ProgressBar, "TraitView[V]")$
  $wholeClassDef(android.widget.AnalogClock, "TraitView[V]")$
  $wholeClassDef(android.view.SurfaceView, "TraitView[V]")$
  $wholeClassDef(android.opengl.GLSurfaceView, "TraitSurfaceView[V]")$
  $wholeClassDef(android.widget.VideoView, "TraitSurfaceView[V]")$
  $richClassDef(android.view.ViewStub, "TraitView[V]")$
  $wholeClassDef(android.widget.GridView, "TraitAbsListView[V]")$
  $wholeClassDef(android.widget.ExpandableListView, "TraitListView[V]")$

  trait TraitAdapterView[V <: AdapterView[_]] extends TraitView[V] {
    import android.widget.AdapterView.OnItemClickListener
    import android.widget.AdapterView.OnItemLongClickListener

    $listeners(android.widget.AdapterView)$
  }

  trait TraitAbsSpinner[V <: AbsSpinner] extends TraitAdapterView[V] {
    $properties(android.widget.AbsSpinner)$
  }

  $wholeClassDef(android.widget.Spinner, "TraitAbsSpinner[V]")$
  $wholeClassDef(android.widget.Gallery, "TraitAbsSpinner[V]")$
  $richClassDef(android.widget.AbsSeekBar, "TraitProgressBar[V]")$
  $wholeClassDef(android.widget.SeekBar, "TraitAbsSeekBar[V]")$
  $wholeClassDef(android.widget.RatingBar, "TraitAbsSeekBar[V]")$
  $richClassDef(android.appwidget.AppWidgetHostView, "TraitFrameLayout[V]")$
  $wholeClassDef(android.widget.HorizontalScrollView, "TraitFrameLayout[V]")$
  $wholeClassDef(android.widget.MediaController, "TraitFrameLayout[V]")$
  $wholeClassDef(android.widget.ScrollView, "TraitFrameLayout[V]")$
  $wholeClassDef(android.widget.TabHost, "TraitFrameLayout[V]")$
  $wholeClassDef(android.widget.TimePicker, "TraitFrameLayout[V]")$
  $wholeClassDef(android.widget.ViewAnimator, "TraitFrameLayout[V]")$
  $wholeClassDef(android.widget.ViewFlipper, "TraitViewAnimator[V]")$
  $wholeClassDef(android.widget.ViewSwitcher, "TraitViewAnimator[V]")$
  $wholeClassDef(android.widget.ImageSwitcher, "TraitViewSwitcher[V]")$
  $wholeClassDef(android.widget.TextSwitcher, "TraitViewSwitcher[V]")$
  $wholeClassDef(android.widget.DatePicker, "TraitFrameLayout[V]")$
  $wholeClassDef(android.gesture.GestureOverlayView, "TraitFrameLayout[V]")$
}
