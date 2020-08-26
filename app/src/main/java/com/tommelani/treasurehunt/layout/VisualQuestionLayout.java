package com.tommelani.treasurehunt.layout;

import android.content.Context;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.tommelani.treasurehunt.activities.QuestionActivity;
import com.tommelani.treasurehunt.components.VisualView;
import com.tommelani.treasurehunt.models.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 12/06/2016.
 */
public class VisualQuestionLayout extends AbstractQuestionLayout {

    //public final String TAG = "VisualQuestionView";

    private List<Object> regionList = new ArrayList<>();

    public VisualQuestionLayout(Context context, Question question) {
        super(context, question);
    }

    @Override
    protected void build() {
        if (question.getImageId() != 0) {
            //Listener once imageView is drawn
            iv_image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    iv_image.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    for (Object region : question.getChoiceList()) {
                        if (region instanceof RectF) {
                            RectF rectangle = (RectF) region;
                            float newLeft = rectangle.left * iv_image.getWidth() / 100;
                            float newTop = rectangle.top * iv_image.getHeight() / 100;
                            float newRight = rectangle.right * iv_image.getWidth() / 100;
                            float newBottom = rectangle.bottom * iv_image.getHeight() / 100;
                            RectF newRectF = new RectF(newLeft, newTop, newRight, newBottom);
                            regionList.add(newRectF);
                        }
                    }

                }
            });
        }
        visualView = new VisualView(getContext());
        visualView.setOnTouchListener(new onTouchRegion());
        fl_frame.addView(visualView);


    }

    private class onTouchRegion implements OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            //Log.d(DEBUG_TAG, "onTouch");
            int action = event.getAction();
            float touchX = event.getX();
            float touchY = event.getY();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    int i = 1;
                    for (Object region : regionList) {
                        if (region instanceof RectF) {
                            RectF rectangle = (RectF) region;
                            if (rectangle.contains(touchX, touchY)) {
                                ((QuestionActivity) getContext()).registerChoices(question.getChoiceType(), i, rectangle);
                            }
                        }
                        i++;
                    }
                    visualView.setSelectSet(replyMap.values());
                    visualView.invalidate();
                    break;
            }
            return false;
        }
    }

}