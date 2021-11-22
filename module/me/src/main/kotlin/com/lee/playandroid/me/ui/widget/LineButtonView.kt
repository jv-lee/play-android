package com.lee.playandroid.me.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.lee.library.extensions.dimensToSp
import com.lee.library.extensions.setImageTintCompat
import com.lee.playandroid.me.R

/**
 * @author jv.lee
 * @date 2020/4/10
 * @description
 */
class LineButtonView : ConstraintLayout {

    private var iconSize: Int = 0
    private var leftDrawableId = 0
    private var rightDrawableId = 0
    private var leftTint: Int = 0
    private var rightTint: Int = 0

    private var leftTextSize: Float = 0F
    private var leftTextMargin: Int = 0
    private var leftTextColor: Int = 0
    private lateinit var leftText: String

    private var rightTextSize: Float = 0F
    private var rightTextMargin: Int = 0
    private var rightTextColor: Int = 0
    private lateinit var rightText: String

    private lateinit var tvLeftText: TextView
    private lateinit var tvRightText: TextView
    private lateinit var ivLeftDrawable: ImageView
    private lateinit var ivRightDrawable: ImageView

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributes: AttributeSet) : this(context, attributes, 0)
    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributes,
        defStyleAttr
    ) {
        initAttribute(attributes!!)
        initView()
    }

    private fun initAttribute(attrs: AttributeSet) {

        context.obtainStyledAttributes(attrs, R.styleable.LineButtonView).run {
            //左侧文字
            leftTextSize = context.dimensToSp(
                getDimension(
                    R.styleable.LineButtonView_leftTextSize,
                    resources.getDimension(R.dimen.view_line_text_size)
                )
            )
            leftTextMargin = getDimensionPixelSize(
                R.styleable.LineButtonView_leftTextMargin,
                resources.getDimension(R.dimen.view_line_text_margin).toInt()
            )
            leftTextColor = getColor(
                R.styleable.LineButtonView_leftTextColor,
                ContextCompat.getColor(context, R.color.colorThemeAccent)
            )
            leftText = getString(R.styleable.LineButtonView_leftText) ?: ""

            //右侧文字
            rightTextSize = context.dimensToSp(
                getDimension(
                    R.styleable.LineButtonView_rightTextSize,
                    resources.getDimension(R.dimen.view_line_text_size)
                )
            )
            rightTextMargin = getDimensionPixelSize(
                R.styleable.LineButtonView_rightTextMargin,
                resources.getDimension(R.dimen.view_line_text_margin).toInt()
            )
            rightTextColor = getColor(
                R.styleable.LineButtonView_rightTextColor,
                ContextCompat.getColor(context, R.color.colorThemeAccent)
            )
            rightText = getString(R.styleable.LineButtonView_rightText) ?: ""

            //图片资源
            iconSize =
                getDimensionPixelSize(
                    R.styleable.LineButtonView_iconSize,
                    resources.getDimensionPixelSize(R.dimen.view_line_icon_size)
                )
            leftDrawableId = getResourceId(R.styleable.LineButtonView_leftDrawable, 0)
            rightDrawableId = getResourceId(R.styleable.LineButtonView_rightDrawable, 0)
            leftTint = getColor(
                R.styleable.LineButtonView_leftTint,
                0
            )
            rightTint = getColor(
                R.styleable.LineButtonView_rightTint,
                0
            )

            recycle()
        }
    }

    private fun initView() {
        tvLeftText = TextView(context)
        tvLeftText.run {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            updateLayoutParams<ConstraintLayout.LayoutParams> {
                startToStart = 0
                topToTop = 0
                bottomToBottom = 0
                leftMargin = leftTextMargin
            }
            setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                leftTextSize
            )
            setTextColor(leftTextColor)
            text = leftText
            addView(this)
        }

        tvRightText = TextView(context)
        tvRightText.run {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            updateLayoutParams<ConstraintLayout.LayoutParams> {
                endToEnd = 0
                topToTop = 0
                bottomToBottom = 0
                rightMargin = rightTextMargin
            }
            setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                rightTextSize
            )
            setTextColor(rightTextColor)
            text = rightText
            addView(this)
        }

        ivLeftDrawable = ImageView(context)
        ivLeftDrawable.run {
            layoutParams = LayoutParams(iconSize, iconSize)
            updateLayoutParams<ConstraintLayout.LayoutParams> {
                startToStart = 0
                topToTop = 0
                bottomToBottom = 0
            }
            //设置兼容资源及调色
            setImageTintCompat(leftDrawableId, leftTint)
            addView(this)
        }

        ivRightDrawable = ImageView(context)
        ivRightDrawable.run {
            layoutParams = LayoutParams(iconSize, iconSize)
            updateLayoutParams<ConstraintLayout.LayoutParams> {
                endToEnd = 0
                topToTop = 0
                bottomToBottom = 0
            }
            //设置兼容资源及调色
            setImageTintCompat(rightDrawableId, rightTint)
            addView(this)
        }
    }

    fun setLeftText(text: String?) {
        text ?: return
        tvLeftText.text = text
    }

    fun setRightText(text: String?) {
        text ?: return
        tvRightText.text = text
    }

    fun getLeftTextView(): TextView? {
        return tvLeftText
    }

    fun getRightTextView(): TextView? {
        return tvRightText
    }

}