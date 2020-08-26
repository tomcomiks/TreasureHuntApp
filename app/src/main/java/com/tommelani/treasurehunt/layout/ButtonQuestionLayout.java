package com.tommelani.treasurehunt.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tommelani.treasurehunt.R;
import com.tommelani.treasurehunt.activities.QuestionActivity;
import com.tommelani.treasurehunt.models.Question;

/**
 * Created by Thomas on 12/06/2016.
 */

public class ButtonQuestionLayout extends AbstractQuestionLayout {

    //public final String TAG = "ButtonQuestionView Log";

    public ButtonQuestionLayout(Context context, Question question) {
        super(context, question);
    }

    @Override
    protected void build() {
        llChoiceList.removeAllViews();
        int i = 1;
        for (Object object : question.getChoiceList()) {
            String choice = (String) object;
            Button button = new Button(getContext());
            button.setText(choice);
            button.setId(i++);
            button.setTransformationMethod(null);
            button.setBackgroundResource(R.drawable.btn_default);
            button.setOnClickListener(new onClickButton());
            LinearLayout.LayoutParams buttonLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonLayout.setMargins(2, 2, 2, 6);
            button.setLayoutParams(buttonLayout);
            llChoiceList.addView(button);
        }
    }

    protected void resetButtons() {
        for (int i = 0; i < llChoiceList.getChildCount(); i++) {
            View button = llChoiceList.getChildAt(i);
            if (replyMap.containsValue(button)) {
                button.setSelected(true);
            } else {
                button.setSelected(false);
            }
        }
    }

    private class onClickButton implements OnClickListener {
        public void onClick(View v) {
            ((QuestionActivity) getContext()).registerChoices(question.getChoiceType(), v.getId(), v);
            resetButtons();
        }
    }

}