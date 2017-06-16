package com.gensee.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.gensee.playerdemo.R;


/**
 * 自定义ImageView形状
 * llxue
 * 2015-4-13
 * heiyulong@xdf.cn
 */
public class CustomShapeImage extends MaskedImage {

    private static final int DEFAULT_CORNER_RADIUS = 6;
    private int cornerRadius = DEFAULT_CORNER_RADIUS;
    private double mHeightRatio;
    private Canvas localCanvas;

    public CustomShapeImage(Context paramContext) {
        super(paramContext);
    }

    public CustomShapeImage(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        setWillNotDraw(false);
        TypedArray a = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CustomImageViewShape);
        setType(a.getInteger(R.styleable.CustomImageViewShape_shape_type, type));
        setCornerRadius(a.getInteger(R.styleable.CustomImageViewShape_corner_radius, DEFAULT_CORNER_RADIUS));
    }

    private void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;

    }

    public CustomShapeImage(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        TypedArray a = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CustomImageViewShape);
        setType(a.getInteger(R.styleable.CustomImageViewShape_shape_type, type));
    }

    public Bitmap createMask() {
        int i = getWidth();
        int j = getHeight();
        Bitmap.Config localConfig = Bitmap.Config.ARGB_8888;
        Bitmap localBitmap = Bitmap.createBitmap(i, j, localConfig);

        Paint localPaint = new Paint(1);
        localPaint.setColor(-16777216);
        float f1 = getWidth();
        float f2 = getHeight();
        localCanvas = null;
        localCanvas = new Canvas(localBitmap);
        if (getType() == TYPE_CIRCULAR) {//圆的
            RectF localRectF = new RectF(0.0F, 0.0F, f1, f2);
            localCanvas.drawOval(localRectF, localPaint);
        } else if (getType() == TYPE_SQUARE) {//方的
            RectF localRectF = new RectF(0.0F, 0.0F, f1, f2);
            localCanvas.drawRect(localRectF, localPaint);
        } else if (getType() == TYPE_RHOMBUS) {//菱形
            localPaint.setStrokeWidth(3);
            Path path = new Path();
            path.moveTo(f1 / 2, 0);// 此点为多边形的起点
            path.lineTo(f1, f2 / 2);
            path.lineTo(f1 / 2, f2);
            path.lineTo(0, f2 / 2);
            path.close(); // 使这些点构成封闭的多边形
            localCanvas.drawPath(path, localPaint);
        } else if (getType() == TYPE_ROUNDRECT) {//圆角矩形
            RectF localRectF = new RectF(0.0F, 0.0F, f1, f2);
            localCanvas.drawRoundRect(localRectF, this.cornerRadius, this.cornerRadius, localPaint);
        }
        return localBitmap;
    }

    public void setHeightRatio(double ratio) {
        if (ratio != mHeightRatio) {
            mHeightRatio = ratio;
            requestLayout();
        }
    }

    public double getHeightRatio() {
        return mHeightRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mHeightRatio > 0.0) {
            // set the image views size
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) (width * mHeightRatio);
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
