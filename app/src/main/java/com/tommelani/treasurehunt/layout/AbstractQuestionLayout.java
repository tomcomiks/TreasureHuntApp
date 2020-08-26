package com.tommelani.treasurehunt.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tommelani.treasurehunt.R;
import com.tommelani.treasurehunt.activities.QuestionActivity;
import com.tommelani.treasurehunt.components.VisualView;
import com.tommelani.treasurehunt.models.Question;

import java.util.Map;

/**
 * Created by Thomas on 15/06/2016.
 */
public abstract class AbstractQuestionLayout extends LinearLayout {

    //public final String TAG = "QuestionView";

    protected Question question;
    protected View layout;
    protected LinearLayout llChoiceList;
    protected ImageView iv_image;
    protected TextView tvQuestion;
    protected LinearLayout llTopPane;
    protected EditText et_input;
    protected GridLayout glChoices;
    protected int inputId;
    protected VisualView visualView;
    protected FrameLayout fl_frame;
    protected Button btn_submit;

    protected Map<Integer, Object> replyMap;


    public AbstractQuestionLayout(Context context, Question question) {
        super(context);
        this.question = question;
        init();
    }

    protected void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.activity_question, this);

        //Get Views
        iv_image = (ImageView) layout.findViewById(R.id.image);
        tvQuestion = (TextView) layout.findViewById(R.id.tv_question);
        llChoiceList = (LinearLayout) layout.findViewById(R.id.ll_choice_list);
        llTopPane = (LinearLayout) layout.findViewById(R.id.ll_top_pane);
        fl_frame = (FrameLayout) layout.findViewById(R.id.fl_frame);
        btn_submit = (Button) layout.findViewById(R.id.btn_submit);

        //Set Views
        tvQuestion.setText(question.getTitle());

        if (question.getImageId() != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), question.getImageId());
            iv_image.setImageBitmap(bitmap);
        } else {
            ((View) iv_image.getParent()).setVisibility(View.GONE);
        }

        inputId = question.getChoiceList().size() + 1;

        replyMap = ((QuestionActivity) getContext()).getReplyMap();

        build();
    }

    protected abstract void build();


}
