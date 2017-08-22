@file:Suppress("UNCHECKED_CAST")

package com.airbnb.paris

import android.support.annotation.StyleRes
import android.support.annotation.UiThread
import android.util.AttributeSet
import android.view.View
import com.airbnb.paris.proxy.Proxy

@UiThread
abstract class StyleApplier<S : StyleApplier<S, P, V>, P, V : View> private constructor(val proxy: P?, val view: V?) {

    protected constructor(proxy: Proxy<P, V>) : this(proxy.proxy, proxy.view)
    constructor(view: V) : this(view as P, view)
    constructor() : this(null, null)

    private var appliedStyles = ArrayList<Style>()

    /**
     * Passing a null [AttributeSet] will apply default values, if any
     */
    fun apply(attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            apply(SimpleStyle(attributeSet))
        } else {
            apply(SimpleStyle.EMPTY)
        }
    }

    fun apply(@StyleRes styleRes: Int) {
        apply(SimpleStyle(styleRes))
    }

    fun apply(styleApplier: S) {
        for (style in styleApplier.appliedStyles) {
            apply(style)
        }
    }

    open fun apply(style: Style) {
        appliedStyles.add(style)

        if (view == null) {
            // If the view is null then this StyleApplier is only used as a builder of styles, no
            // need to actually apply or process anything
        } else {
            // Assumes that if the Style has an AttributeSet it's being applied during the View
            // initialization, in which case parents should be making the call themselves
            if (style.shouldApplyParent) {
                applyParent(style)
            }

            applyDependencies(style)

            val attributes = attributes()
            if (attributes != null) {
                val typedArray = style.obtainStyledAttributes(view.context, attributes)

                processStyleableFields(style, typedArray)

                // For debug purposes
                if (style.debugListener != null) {
                    style.debugListener!!.beforeTypedArrayProcessed(style, typedArray)
                } else {
                    processAttributes(style, typedArray)
                }

                typedArray.recycle()
            }
        }
    }

    protected open fun attributes(): IntArray? {
        return null
    }

    /**
     * Visible for debug
     */
    open fun attributesWithDefaultValue(): IntArray? {
        return null
    }

    protected open fun applyParent(style: Style) {}

    protected open fun applyDependencies(style: Style) {}

    protected open fun processStyleableFields(style: Style, a: TypedArrayWrapper) {}

    protected open fun processAttributes(style: Style, a: TypedArrayWrapper) {}

    /**
     * For debug purposes.
     *
     * Asserts that the attributes applied when using [styleRes] are the same as the aggregated
     * attributes already applied by this [StyleApplier]
     */
    fun assertAppliedSameAttributes(@StyleRes styleRes: Int) {
        assertAppliedSameAttributes(SimpleStyle(styleRes))
    }

    /**
     * For debug purposes.
     *
     * Asserts that the attributes applied when using [style] are the same as the aggregated
     * attributes already applied by this [StyleApplier]
     */
    fun assertAppliedSameAttributes(style: Style) {
        StyleApplierUtils.assertSameAttributes(this, style, MultiStyle(appliedStyles, null))
    }
}
