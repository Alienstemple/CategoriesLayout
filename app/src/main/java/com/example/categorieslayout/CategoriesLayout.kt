package com.example.categorieslayout

import android.content.Context
import android.icu.text.MeasureFormat.FormatWidth
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.content.contentValuesOf
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import java.lang.StrictMath.max

class CategoriesLayout(context: Context, attributeSet: AttributeSet) :
    ViewGroup(context, attributeSet) {
    /**
     * Размещает элементы на экране
     * @param changed Вне зависимости от него вызывается onLayout
     * [l] [t] [r] [b] -- координаты Layout
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.d("CategorLayout", "onLayout(): changed = $changed, l = $l, t = $t, r = $r, b = $b")
        // Начальное значение отступа = padding
        var offset = paddingLeft
        val rightBound = r - l - paddingRight  // r - правая граница, вычтем l - расстояние до самой view group, вычтем paddingRight

        for (i in 0 until childCount) {
            // Доступ к ребенку
            var child = getChildAt(i)
            //
            var lp = child.layoutParams as MyLayoutParams

            val childL = offset + lp.leftMargin // учитываем свой padding и margin ребенка
            val childT = paddingTop + lp.topMargin
            val childR = childL + child.measuredWidth
            val childB =
                childT + child.measuredHeight  // paddingRight и paddingBottom учитываются в onMeasure, помещаемся ли в родителя (childMeasuredState)

            // Переходим в это условие, только если в measureChildWithMargins занулили widthUsed
            if (childR <= rightBound) {  // отрисовываем только в случае , если правая граница ребенка не выходит за наш правый край
                child.layout(
                    childL,
                    childT,
                    childR,
                    childB
                )  // другая координатная сетка! Не та, что в onLayout!
                // инкрементируем offset: координата ребенка и margin ребенка
                offset = childR + lp.rightMargin
            }
        }
    }

    /**
     * Срабатывает при нажатии на кнопку и нажатии на view group
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("CategorLayout", "onIntercept: ${ev.toString()}")
        return super.onInterceptTouchEvent(ev)
    }

    /**
     * Срабатывает при нажатии на view group
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("CategorLayout", "onTouch: ${event.toString()}")
        return super.onTouchEvent(event)
    }

    /**
     * Измерение себя и вызов measure у дочерних View
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var requestedWidth = 0
        var requestedHeight = 0
        var childMeasuredState = 0

        for (i in 0 until childCount) {
            // Доступ к ребенку
            var child = getChildAt(i)

            // Доступ к layout params ребенка, кастим к MyLayoutParams
            var lp = child.layoutParams as MyLayoutParams

            // Вызов onMeasure для детей.
            // width-, heightMeasureSpec пробрасываем,
            // requestedWidth - использованая ширина,
            // requestedHeight - в нашем случае не важно, = 0
            // В методе getChildMeasureSpec учитываются padding ViewGroup, margin ребенка (MarginLayoutParams child.getLayoutParams)
            // затем вызываем child.measure
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)  // Занулить widthUsed, чтобы child считал, что ему доступно все свободное место

            // Обязательно учесть margin ребенка
            requestedWidth += child.measuredWidth + lp.leftMargin + lp.rightMargin
            requestedHeight =
                max(requestedHeight, child.measuredHeight + lp.topMargin + lp.bottomMargin)
            childMeasuredState = combineMeasuredStates(childMeasuredState, child.measuredState)
        }

        // Учтем свои padding-и
        requestedWidth += paddingLeft + paddingRight
        requestedHeight += paddingTop + paddingBottom

        requestedWidth = max(suggestedMinimumWidth, requestedWidth)
        requestedHeight = max(suggestedMinimumHeight, requestedHeight)

        // Устанавливаем размеры View с учетом childState, в котором зашиты width и height,
        // который достаем с помощью shl
        setMeasuredDimension(
            resolveSizeAndState(requestedWidth, widthMeasureSpec, childMeasuredState),
            resolveSizeAndState(
                requestedHeight,
                heightMeasureSpec,
                childMeasuredState shl MEASURED_HEIGHT_STATE_SHIFT
            )
        )
    }

    /**
     * Для добавления возможностей layout params
     * Чтение атрибутов [attrs] из xml-файла
     */
    override fun generateLayoutParams(attrs: AttributeSet?): ViewGroup.LayoutParams {
        return MyLayoutParams(context, attrs)
    }

    /**
     * Для добавления возможностей layout params
     */
    override fun generateLayoutParams(p: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
        if (p is MarginLayoutParams) {   // MarginLayoutParams, можно вытащить w, h и margins
            return MyLayoutParams(p)
        }
        return MyLayoutParams(p)    // ViewGroup.LayoutParams, можно вытащить w, h
    }

    /**
     * Для добавления возможностей layout params.
     * Вызывается, когда добавляется Child без layout params (addView)
     */
    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return MyLayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT
        )  // ViewGroup.MarginLayoutParams.WRAP_CONTENT
    }

    /**
     * Для добавления возможностей layout params
     * Является ли [p] наследником определенного нами MyLayoutParams?
     * Необходима дополнительная проверка, т к addView(TextView, ViewGroup.LayoutParams) принимает общий класс
     * Иначе вызывается generateLayoutParams(ViewGroup.LayoutParams)
     */
    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean = p is MyLayoutParams

    /**
     * Кастомные layout params
     */
    class MyLayoutParams : MarginLayoutParams {
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(params: MarginLayoutParams) : super(params)
        constructor(params: ViewGroup.LayoutParams?) : super(params)
    }
}