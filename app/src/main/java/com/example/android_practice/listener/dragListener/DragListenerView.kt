package com.example.android_practice.listener.dragListener

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible

class DragListenerView(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {

  private var columns = 0
  private var rows = 0

  private val dragListener = OnDragListener { v, event ->
    when (event.action) {
      DragEvent.ACTION_DRAG_STARTED -> {
        if (event.localState == v) {
          v.visibility = INVISIBLE
        }
      }
      // 拖拽进入某个View的区域，初始拖拽时也会触发，因为初始进入了被拖拽的View的区域
      DragEvent.ACTION_DRAG_ENTERED -> {
        if (event.localState !== v) {
          // 此时 v 为拖拽进入的目标view
          sortChildView(v)
        }
      }

      // 拖拽离开某个View的区域
      DragEvent.ACTION_DRAG_EXITED -> {}

      // 拖拽结束
      DragEvent.ACTION_DRAG_ENDED -> {
        if (event.localState === v) {
          v.visibility = VISIBLE
        }
      }
    }
    true
  }

  private var draggedView: View? = null
  private var orderedChildView: MutableList<View> = mutableListOf()

  /**
   * 触发时机：
   * view首次添加到布局
   * 父容器尺寸变化、子view数量变化
   * 调用requesetLayout
   * 布局参数 LayoutParams 改变
   */
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    columns = 2
    rows = childCount / columns + 1

    val widthSpec = MeasureSpec.getSize(widthMeasureSpec)
    val heightSpec = MeasureSpec.getSize(heightMeasureSpec)
    val childWidthMeasureSpec =
      MeasureSpec.makeMeasureSpec(widthSpec / columns, MeasureSpec.EXACTLY)
    val childHeightMeasureSpec =
      MeasureSpec.makeMeasureSpec(heightSpec / rows, MeasureSpec.EXACTLY)

    measureChildren(childWidthMeasureSpec, childHeightMeasureSpec) // 设定子view尺寸
    setMeasuredDimension(widthSpec, heightSpec) // 设定最终尺寸
  }

  override fun onLayout(
    changed: Boolean,
    l: Int,
    t: Int,
    r: Int,
    b: Int,
  ) {
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      child.layout(
        0,
        0,
        child.measuredWidth,
        child.measuredHeight
      )
      child.translationX = (i % columns * child.measuredWidth).toFloat()  // 列偏移
      child.translationY = (i / columns * child.measuredHeight).toFloat() // 行偏移
    }
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      orderedChildView.add(child)

      child.setOnLongClickListener { v ->
        draggedView = v
        v.startDragAndDrop(null, DragShadowBuilder(v), v, 0)
        true // 消费长按事件
      }

      child.setOnDragListener(dragListener)
    }
  }

  fun sortChildView(targetView: View) {
    // 重新排序View列表
    var targetViewIndex = 0
    var draggedViewIndex = 0
    orderedChildView.forEachIndexed { index, view ->
      if (view === targetView) {
        targetViewIndex = index
      }
      if (view === draggedView) {
        draggedViewIndex = index
      }
    }
    swapElements(orderedChildView, targetViewIndex, draggedViewIndex)
    // 使用动画重新布局
    for (i in 0 until orderedChildView.size) {
      val child = orderedChildView[i]
      val x = (i % columns * child.measuredWidth).toFloat()
      val y = (i / columns * child.measuredHeight).toFloat()
      child.animate()
        .translationX(x)
        .translationY(y)
        .setDuration(300)
        .start()
    }
  }

  private fun swapElements(list: MutableList<View>, index1: Int, index2: Int) {
    val temp = list[index1]
    list[index1] = list[index2]
    list[index2] = temp
  }
}