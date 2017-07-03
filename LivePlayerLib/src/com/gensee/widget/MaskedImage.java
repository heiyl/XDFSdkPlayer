package com.gensee.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public abstract class MaskedImage extends ImageView {
    private static final Xfermode MASK_XFERMODE;
    private Bitmap mask;
    private Paint paint;
    protected int type;
    public static final int TYPE_SQUARE = 0;
    public static final int TYPE_CIRCULAR = 1;
    public static final int TYPE_RHOMBUS = 2;
    public static final int TYPE_ROUNDRECT = 3;
    static {
        PorterDuff.Mode localMode = PorterDuff.Mode.DST_IN;
        MASK_XFERMODE = new PorterDuffXfermode(localMode);
    }

    public MaskedImage(Context paramContext) {
        super(paramContext);
    }

    public MaskedImage(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public MaskedImage(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public void setType(int type) {
        this.type = type;
        mask = null;
    }

    int getType() {
        return this.type;
    }

    public abstract Bitmap createMask();

    protected void onDraw(Canvas paramCanvas) {
        Drawable localDrawable = getDrawable();
        if (localDrawable == null)
            return;
        try {
            if (this.paint == null) {
                this.paint = new Paint();
                this.paint.setFilterBitmap(false);
                Xfermode localXfermode1 = MASK_XFERMODE;
                @SuppressWarnings("unused")
                Xfermode localXfermode2 = this.paint.setXfermode(localXfermode1);
            }
            float f1 = getWidth();
            float f2 = getHeight();
            int i = paramCanvas.saveLayer(0.0F, 0.0F, f1, f2, null, 31);
            int j = getWidth();
            int k = getHeight();
            localDrawable.setBounds(0, 0, j, k);
            localDrawable.draw(paramCanvas);
            if ((this.mask == null) || (this.mask.isRecycled())) {
                this.mask = createMask();
            }
            paramCanvas.drawBitmap(this.mask, 0.0F, 0.0F, this.paint);
            paramCanvas.restoreToCount(i);
            if (this.mask != null && !this.mask.isRecycled()) {
                this.mask.recycle();
                this.mask = null;
                System.gc();
            }
            return;
        } catch (Exception localException) {
            StringBuilder localStringBuilder = new StringBuilder()
                    .append("Attempting to draw with recycled bitmap. View ID = ");
        }
    }

    //添加点击效果
    /*******************************************************************************************************/
    /*@Override
    public void setImageBitmap(Bitmap bm) {
        Drawable d = createStateDrawable(getContext(), new BitmapDrawable(getContext().getResources(), bm));
        setImageDrawable(d);
    }

    public StateListDrawable createStateDrawable(Context context, Drawable normal) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(View.PRESSED_ENABLED_STATE_SET, createPressDrawable(normal));
        drawable.addState(View.ENABLED_STATE_SET, normal);
        drawable.addState(View.EMPTY_STATE_SET, normal);
        return drawable;
    }

    public Drawable createPressDrawable(Drawable d) {
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColor(0x60000000);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        new Canvas(bitmap).drawRoundRect(rect, 4, 4, paint);
        return new BitmapDrawable(getContext().getResources(), bitmap);
    }*/
    /*******************************************************************************************************/
}
