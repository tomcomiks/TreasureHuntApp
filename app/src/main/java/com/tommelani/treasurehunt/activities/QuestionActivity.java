package com.tommelani.treasurehunt.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tommelani.treasurehunt.R;
import com.tommelani.treasurehunt.dao.DatabaseAccess;
import com.tommelani.treasurehunt.helpers.Serializer;
import com.tommelani.treasurehunt.models.Question;
import com.tommelani.treasurehunt.layout.ButtonQuestionLayout;
import com.tommelani.treasurehunt.layout.InputQuestionLayout;
import com.tommelani.treasurehunt.layout.VisualQuestionLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * The question database is managed locally on the phone.
 */
public class QuestionActivity extends Activity {

    //public final String TAG = "Question";

    public final static String SESSION_MILESTONE_ID = MainActivity.SESSION_MILESTONE_ID;
    public final static String SESSION_PASSED_MILESTONES = MainActivity.SESSION_PASSED_MILESTONES;
    public final static String SESSION = MainActivity.SESSION;
    long milestoneId;
    SharedPreferences preferences;
    private DatabaseAccess database;
    private Question question;
    private Map<Integer, Object> replyMap = new HashMap<>();

    /**
     * Initialize the SharedPreferences
     * Open the database
     * Select a random question in the Milestone and load the layout according to the type of the question.
     * @param savedInstanceState the instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, "OnCreate");
        super.onCreate(savedInstanceState);

        //Get Preferences
        preferences = getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        milestoneId = preferences.getLong(SESSION_MILESTONE_ID, 1);

        //Open Database
        database = DatabaseAccess.getInstance(this);
        database.open();

        //Select a random Question
        question = database.getRandomQuestionFromMilestone(milestoneId);

        switch (question.getType()) {
            case BUTTON:
                setContentView(new ButtonQuestionLayout(this, question));
                break;
            case VISUAL:
                setContentView(new VisualQuestionLayout(this, question));
                break;
            case FREE_INPUT:
                setContentView(new InputQuestionLayout(this, question));
                break;
        }


    }

    /**
     * Close the database
     */
    @Override
    protected void onPause() {
        database.close();
        super.onPause();
    }

    /**
     * Open the database
     */
    @Override
    protected void onResume() {
        //Log.d(DEBUG_TAG, "OnResume");
        super.onResume();
        database.open();
    }

    /**
     * Retrieves the answer submitted by the user
     * @return map of the answers
     */
    public Map<Integer, Object> getReplyMap() {
        return replyMap;
    }

    /**
     * Registers user answers
     * @param choiceType choice type
     * @param index id of the question
     * @param object answer of the user
     */
    public void registerChoices(Question.ChoiceType choiceType, int index, Object object) {

        //Log.d(DEBUG_TAG, "RegisterChoice");
        switch (choiceType) {

            case SINGLE_CHOICE:
                replyMap.clear();
                replyMap.put(index, object);
                break;

            case MULTIPLE_CHOICE:
                if (replyMap.containsValue(object)) {
                    replyMap.remove(index);
                } else {
                    replyMap.put(index, object);
                }
                break;
        }
    }

    /**
     *  If user answers correspond to the expected answers of the question, the Milestone is added
     *  as passed in the SharedPreferences,
     *  the activity QuestionActivity is closed and the HuntActivity activity is launched.
     *  Otherwise, a message tells user to try again.
     * @param view the view
     */
    public void validateChoices(View view) {
        //Log.d(DEBUG_TAG, replyMap.toString());
        if (question.isPassed(replyMap.keySet())) {
            SharedPreferences.Editor editor = preferences.edit();
            String json = preferences.getString(SESSION_PASSED_MILESTONES, "");
            editor.putString(SESSION_PASSED_MILESTONES, Serializer.addLongToList(json, milestoneId));
            editor.apply();
            Intent i = new Intent(this, HuntActivity.class);
            startActivity(i);
            finish();
        } else {
            Context context = getApplicationContext();
            CharSequence text = getResources().getText(R.string.try_again);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}