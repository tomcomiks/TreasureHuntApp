package com.tommelani.treasurehunt.layout;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tommelani.treasurehunt.R;
import com.tommelani.treasurehunt.activities.QuestionActivity;
import com.tommelani.treasurehunt.models.Question;

/**
 * Created by Thomas on 12/06/2016.
 */
public class InputQuestionLayout extends AbstractQuestionLayout {

    public InputQuestionLayout(Context context, Question question) {
        super(context, question);
    }

    @Override
    protected void build() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        llTopPane.addView(linearLayout);

        et_input = new EditText(getContext());
        et_input.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        linearLayout.addView(et_input);

        switch (question.getChoiceType()) {
            case MULTIPLE_CHOICE:
                Button btn_input = new Button(getContext());
                btn_input.setText(getResources().getText(R.string.ok));
                btn_input.setOnClickListener(new addInput());
                linearLayout.addView(btn_input);

                //Generate choice list
                glChoices = new GridLayout(getContext());
                glChoices.setOrientation(GridLayout.VERTICAL);
                glChoices.setUseDefaultMargins(true);
                llChoiceList.addView(glChoices);
                break;
            case SINGLE_CHOICE:
                btn_submit.setOnClickListener(new addInputAndSubmit());
                break;
        }
    }


    public void processInputs() {
        String input = et_input.getText().toString();
        int index = inputId++;
        int i = 1;
        for (Object object : question.getChoiceList()) {
            String choice = (String) object;
            if (choice.equals(input)) {
                index = i;
                break;
            }
            i++;
        }
        ((QuestionActivity) getContext()).registerChoices(question.getChoiceType(), index, input);
    }


    private void resetInputs() {
        et_input.getText().clear();
        glChoices.removeAllViews();
        for (int index : replyMap.keySet()) {
            String string = replyMap.get(index).toString();

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            glChoices.addView(linearLayout);

            TextView textView = new TextView(getContext());
            textView.setText(string);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            linearLayout.addView(textView);

            Button button = new Button(getContext());
            button.setId(index);
            button.setText("X");
            button.setOnClickListener(new removeInput());
            linearLayout.addView(button);

        }
    }

    private class addInputAndSubmit implements OnClickListener {
        @Override
        public void onClick(View v) {
            processInputs();
            ((QuestionActivity) getContext()).validateChoices(v);
        }
    }

    private class addInput implements OnClickListener {
        @Override
        public void onClick(View v) {
            processInputs();
            resetInputs();
        }
    }


    private class removeInput implements OnClickListener {
        @Override
        public void onClick(View v) {
            replyMap.remove(v.getId());
            resetInputs();
        }
    }


}
