package com.example.android_practice.listener.dragListener

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper

/**
 * DragHelperView 是一个自定义的 ViewGroup，实现了子视图的拖拽功能
 * 
 * 主要功能：
 * 1. 使用 ViewDragHelper 实现子视图的拖拽交互
 * 2. 将子视图以网格形式布局（2列）
 * 3. 拖拽时提升被拖拽视图的层级（elevation）
 * 4. 释放时视图自动回到原始位置
 */
class DragHelperView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

  // ViewDragHelper 是 Android 提供的辅助类，用于简化拖拽手势的处理
  private var viewDragHelper = ViewDragHelper.create(this, DragHelperCallback())

  /**
   * DragHelperCallback 是 ViewDragHelper.Callback 的实现类
   * 用于定义拖拽行为的具体逻辑，包括：
   * - 哪些视图可以被拖拽
   * - 拖拽时的位置限制
   * - 拖拽开始、进行中、结束时的处理
   */
  inner class DragHelperCallback : ViewDragHelper.Callback() {
    /**
     * 判断指定的子视图是否可以被拖拽
     * @param child 被触摸的子视图
     * @param pointerId 触摸点的 ID
     * @return true 表示允许拖拽该视图，false 表示不允许
     */
    override fun tryCaptureView(
      child: View,
      pointerId: Int,
    ): Boolean {
      return true
    }

    /**
     * 当拖拽状态发生变化时调用
     * @param state 新的拖拽状态（STATE_IDLE、STATE_DRAGGING、STATE_SETTLING）
     */
    override fun onViewDragStateChanged(state: Int) {
      // 当拖拽结束（状态变为 IDLE）时，将被拖拽视图的 elevation 恢复原状
      if (state == ViewDragHelper.STATE_IDLE) {
        val capturedView = viewDragHelper.capturedView
        capturedView?.elevation--
      }
    }

    /**
     * 限制子视图在水平方向上的位置
     * @param child 被拖拽的子视图
     * @param left 建议的左边距位置
     * @param dx 水平方向的移动距离
     * @return 实际允许的左边距位置
     */
    override fun clampViewPositionHorizontal(
      child: View,
      left: Int,
      dx: Int,
    ): Int {
      // 不限制水平位置，允许自由拖拽
      return left
    }

    /**
     * 限制子视图在垂直方向上的位置
     * @param child 被拖拽的子视图
     * @param top 建议的上边距位置
     * @param dy 垂直方向的移动距离
     * @return 实际允许的上边距位置
     */
    override fun clampViewPositionVertical(
      child: View,
      top: Int,
      dy: Int,
    ): Int {
      // 不限制垂直位置，允许自由拖拽
      return top
    }

    // 保存被捕获视图的原始位置，用于释放时恢复位置
    var capturedLeft = 0f
    var capturedTop = 0f
    
    /**
     * 当子视图被捕获（开始拖拽）时调用
     * @param capturedChild 被捕获的子视图
     * @param activePointerId 活动触摸点的 ID
     */
    override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
      // 提升被拖拽视图的层级，使其显示在其他视图之上
      capturedChild.elevation = elevation + 1
      // 保存原始位置，用于释放时恢复
      capturedLeft = capturedChild.left.toFloat()
      capturedTop = capturedChild.top.toFloat()
    }

    /**
     * 当被拖拽视图的位置发生变化时调用
     * @param changedView 位置发生变化的视图
     * @param left 新的左边距
     * @param top 新的上边距
     * @param dx 水平方向的移动距离
     * @param dy 垂直方向的移动距离
     */
    override fun onViewPositionChanged(
      changedView: View,
      left: Int,
      top: Int,
      dx: Int,
      dy: Int,
    ) {
      // 当前实现中不处理位置变化事件
    }

    /**
     * 当用户释放被拖拽的视图时调用
     * @param releasedChild 被释放的子视图
     * @param xvel 水平方向的速度
     * @param yvel 垂直方向的速度
     */
    override fun onViewReleased(
      releasedChild: View,
      xvel: Float,
      yvel: Float,
    ) {
      // 使用平滑动画将视图恢复到原始位置
      viewDragHelper.settleCapturedViewAt(capturedLeft.toInt(), capturedTop.toInt())
      // 请求重绘以执行动画
      postInvalidateOnAnimation()
    }
  }

  /**
   * 计算滚动位置，用于支持 ViewDragHelper 的平滑动画
   * 当视图正在恢复到原始位置时，此方法会被持续调用以更新动画进度
   */
  override fun computeScroll() {
    // 如果视图仍在动画中，继续请求重绘以更新动画帧
    if (viewDragHelper.continueSettling(true)) {
      ViewCompat.postInvalidateOnAnimation(this)
    }
  }


  // 网格布局的行列数
  private var columns: Int = 0
  private var rows: Int = 0

  /**
   * 测量 ViewGroup 及其子视图的大小
   * 实现网格布局的测量逻辑：将可用空间平均分配给子视图
   * @param widthMeasureSpec 父视图提供的宽度约束
   * @param heightMeasureSpec 父视图提供的高度约束
   */
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    // 设置为 2 列布局
    columns = 2
    // 计算需要的行数（向上取整）
    rows = childCount / columns + 1

    // 获取父视图提供的尺寸
    val widthSpec = MeasureSpec.getSize(widthMeasureSpec)
    val heightSpec = MeasureSpec.getSize(heightMeasureSpec)
    // 计算每个子视图的宽度（平均分配）
    val childWidthMeasureSpec =
      MeasureSpec.makeMeasureSpec(widthSpec / columns, MeasureSpec.EXACTLY)
    // 计算每个子视图的高度（平均分配）
    val childHeightMeasureSpec =
      MeasureSpec.makeMeasureSpec(heightSpec / rows, MeasureSpec.EXACTLY)
    // 测量所有子视图
    measureChildren(childWidthMeasureSpec, childHeightMeasureSpec)
    // 设置 ViewGroup 自身的尺寸
    setMeasuredDimension(widthSpec, heightSpec)
  }

  /**
   * 布局子视图的位置
   * 将子视图按照网格形式排列：第 0 个在左上角，第 1 个在右上角，第 2 个在第二行左，以此类推
   * @param changed 布局是否发生变化
   * @param l 左边距
   * @param t 上边距
   * @param r 右边距
   * @param b 下边距
   */
  override fun onLayout(
    changed: Boolean,
    l: Int,
    t: Int,
    r: Int,
    b: Int,
  ) {
    // 遍历所有子视图，按照网格布局排列
    for (i in 0 until childCount) {
      val child = getChildAt(i)
      // 计算子视图的位置：
      // - 列位置：i % columns（取模运算得到列索引）
      // - 行位置：i / columns（整数除法得到行索引）
      child.layout(
        i % columns * child.measuredWidth,  // left：列索引 × 子视图宽度
        i / columns * child.measuredHeight,  // top：行索引 × 子视图高度
        i % columns * child.measuredWidth + child.measuredWidth,  // right：left + 宽度
        i / columns * child.measuredHeight + child.measuredHeight  // bottom：top + 高度
      )
    }
  }

  /**
   * 拦截触摸事件，判断是否应该由 ViewDragHelper 处理
   * @param ev 触摸事件
   * @return true 表示拦截事件，由 ViewDragHelper 处理；false 表示不拦截
   */
  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
    // 让 ViewDragHelper 判断是否应该拦截此触摸事件
    return viewDragHelper.shouldInterceptTouchEvent(ev)
  }

  /**
   * 处理触摸事件，将事件传递给 ViewDragHelper
   * @param event 触摸事件
   * @return true 表示事件已被处理
   */
  override fun onTouchEvent(event: MotionEvent): Boolean {
    // 将触摸事件传递给 ViewDragHelper 处理
    viewDragHelper.processTouchEvent(event)
    return true
  }

}
