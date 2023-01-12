package com.example.categorieslayout

import android.content.Context
import android.icu.text.MeasureFormat.FormatWidth
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.content.contentValuesOf

class CategoriesLayout(context: Context, attributeSet: AttributeSet) :
    ViewGroup(context, attributeSet) {
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        addView(TextView(context))
    }

    /**
     * Для добавления возможностей layout params
     * Чтение атрибутов из xml-файла
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
     * Является ли p наследником определенного нами MyLayoutParams?
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