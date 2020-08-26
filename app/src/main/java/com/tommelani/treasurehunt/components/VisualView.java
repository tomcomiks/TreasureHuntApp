package com.tommelani.treasurehunt.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.Collection;
import java.util.HashSet;

/**
 * This view defines the behavior of the camera view for questions of type "VISUAL" in the QuestionActivity activity.
 * It extends the View class.
 */
public class VisualView extends View {

    //public static final String TAG = "VisualView";

    private Paint targetPaint;

    private Collection<Object> selectSet;

    public VisualView(Context context) {
        super(context);

        // paint for target
        targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        targetPaint.setStyle(Paint.Style.STROKE);
        targetPaint.setColor(Color.RED);
        targetPaint.setStrokeWidth(5);

        selectSet = new HashSet<>();

    }

    /**
     * Draws the list of rectangles contained in the variable selectSet on the Canvas
     * @param canvas screen
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Object region : selectSet) {
            RectF rectangle = (RectF) region;
            canvas.drawRect(rectangle, targetPaint);
        }
    }

    /**
     * Update the selectSet variable
     * @param selectSet collection
     */
    public void setSelectSet(Collection<Object> selectSet) {
        this.selectSet = selectSet;
    }

}
