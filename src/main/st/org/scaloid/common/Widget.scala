$license()$

package org.scaloid.common

import android.app._
import android.content._
import android.view._
import android.view.View._
import android.widget._
import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions
import WidgetImplicits._


$wholeClassDef(base=android.view.View, mixin="ConstantsSupport")$

$wholeClassDef(android.view.ViewGroup)$

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

$wholeClassDef(android.widget.TextView)$

$wholeClassDef(android.widget.AbsListView)$

$wholeClassDef(android.widget.FrameLayout)$

$wholeClassDef(android.widget.RelativeLayout)$

$wholeClassDef(android.widget.LinearLayout)$

class SVerticalLayout(implicit context: Context, parentVGroup: TraitViewGroup[_] = null) extends SLinearLayout {
  orientation = VERTICAL
}

object SVerticalLayout {

  def apply[LP <: ViewGroupLayoutParams[_, SVerticalLayout]]()
        (implicit context: android.content.Context, defaultLayoutParam: SLinearLayout => LP): SVerticalLayout = {
    val v = new SVerticalLayout
    v.<<.parent.+=(v)
    v
  }
}

$wholeClassDef(android.widget.EditText)$
$wholeClassDef(android.inputmethodservice.ExtractEditText)$
$wholeClassDef(android.widget.AutoCompleteTextView)$
$wholeClassDef(android.widget.ListView)$
$wholeClassDef(android.widget.Button)$
$wholeClassDef(android.widget.CompoundButton)$
$wholeClassDef(android.widget.CheckBox)$
$wholeClassDef(android.widget.RadioButton)$
$wholeClassDef(android.widget.ToggleButton)$
$wholeClassDef(android.widget.CheckedTextView)$
$wholeClassDef(android.widget.Chronometer)$
$wholeClassDef(android.widget.DigitalClock)$
$richClassDef(android.inputmethodservice.KeyboardView)$
$wholeClassDef(android.widget.ImageView)$
$wholeClassDef(android.widget.ImageButton)$
$wholeClassDef(android.widget.QuickContactBadge)$
$wholeClassDef(android.widget.ZoomButton)$
$wholeClassDef(android.widget.ProgressBar)$
$wholeClassDef(android.widget.AnalogClock)$
$wholeClassDef(android.view.SurfaceView)$
$wholeClassDef(android.opengl.GLSurfaceView)$
$wholeClassDef(android.widget.VideoView)$
$wholeClassDef(android.view.ViewStub)$
$wholeClassDef(android.widget.GridView)$
$wholeClassDef(android.widget.ExpandableListView)$
$wholeClassDef(android.widget.BaseAdapter)$
$wholeClassDef(android.widget.BaseExpandableListAdapter)$
$wholeClassDef(android.widget.AdapterView)$
$wholeClassDef(android.widget.AbsSpinner)$
$wholeClassDef(android.widget.Spinner)$
$wholeClassDef(android.widget.Gallery)$
$wholeClassDef(android.widget.AbsSeekBar)$
$wholeClassDef(android.widget.SeekBar)$
$wholeClassDef(android.widget.RatingBar)$
$wholeClassDef(android.appwidget.AppWidgetHostView)$
$wholeClassDef(android.widget.HorizontalScrollView)$
$wholeClassDef(android.widget.MediaController)$
$wholeClassDef(android.widget.ScrollView)$
$wholeClassDef(android.widget.TabHost)$
$wholeClassDef(android.widget.TimePicker)$
$wholeClassDef(android.widget.ViewAnimator)$
$wholeClassDef(android.widget.ViewFlipper)$
$wholeClassDef(android.widget.ViewSwitcher)$
$wholeClassDef(android.widget.ImageSwitcher)$
$wholeClassDef(android.widget.TextSwitcher)$
$wholeClassDef(android.widget.DatePicker)$
$wholeClassDef(android.gesture.GestureOverlayView)$
$wholeClassDef(android.widget.PopupWindow)$
$wholeClassDef(android.widget.ArrayAdapter)$
$wholeClassDef(android.widget.AbsoluteLayout)$
$wholeClassDef(android.widget.MultiAutoCompleteTextView)$
$wholeClassDef(android.widget.TableLayout)$
$wholeClassDef(android.widget.RadioGroup)$
$wholeClassDef(android.widget.SimpleExpandableListAdapter)$
$wholeClassDef(android.widget.AlphabetIndexer)$
$wholeClassDef(android.widget.TwoLineListItem)$
$wholeClassDef(android.widget.HeaderViewListAdapter)$
$wholeClassDef(android.widget.Toast)$
$wholeClassDef(android.widget.ZoomButtonsController)$
$wholeClassDef(android.widget.SlidingDrawer)$
$wholeClassDef(android.widget.ZoomControls)$
$wholeClassDef(android.widget.DialerFilter)$
$wholeClassDef(android.database.DataSetObserver)$
$wholeClassDef(android.widget.TableRow)$
$wholeClassDef(android.widget.TabWidget)$
$wholeClassDef(android.widget.CursorAdapter)$
$wholeClassDef(android.widget.ResourceCursorAdapter)$
$wholeClassDef(android.widget.SimpleCursorAdapter)$
$wholeClassDef(android.widget.Scroller)$
$wholeClassDef(android.widget.SimpleAdapter)$
$wholeClassDef(android.widget.RemoteViews)$

