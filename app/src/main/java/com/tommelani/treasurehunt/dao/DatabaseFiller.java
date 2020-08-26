package com.tommelani.treasurehunt.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tommelani.treasurehunt.R;
import com.tommelani.treasurehunt.helpers.Serializer;
import com.tommelani.treasurehunt.models.Milestone;
import com.tommelani.treasurehunt.models.Question;
import com.tommelani.treasurehunt.models.Track;
import com.tommelani.treasurehunt.models.Treasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseFiller extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "project";
    public static final int DATABASE_VERSION = 14;
    public static final String TABLE_QUESTION = "question";
    public static final String TABLE_MILESTONE = "milestone";
    public static final String TABLE_TRACK = "track";
    public static final String TABLE_TREASURE = "treasure";

    private List<Track> trackList;
    private Map<Track, Long> trackIds;
    private List<Milestone> milestoneList;
    private Map<Milestone, Long> milestoneIds;
    private List<Question> questionList;
    private List<Treasure> treasureList;

    public DatabaseFiller(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + TABLE_TRACK + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title VARCHAR(255) " +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_MILESTONE + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title VARCHAR(255), " +
                        "latitude DOUBLE," +
                        "longitude DOUBLE, " +
                        "track_id INTEGER, " +
                        "description TEXT " +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_QUESTION + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title VARCHAR(255), " +
                        "answers TEXT, " +
                        "choices TEXT, " +
                        "image VARCHAR(255), " +
                        "milestone_id INTEGER, " +
                        "question_type VARCHAR(255), " +
                        "choice_type VARCHAR(255) " +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_TREASURE + " (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title VARCHAR(255), " +
                        "latitude DOUBLE," +
                        "longitude DOUBLE, " +
                        "image VARCHAR(255), " +
                        "milestone_id INTEGER" +
                        " ) "
        );

        populateTracks(db);
        populateMilestones(db);
        populateQuestions(db);
        populateTreasures(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MILESTONE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TREASURE + ";");
        onCreate(db);
    }


    public void populateTracks(SQLiteDatabase db) {

        trackList = new ArrayList<>();
        trackList.add(new Track("first track"));
        trackList.add(new Track("second track"));

        trackIds = new HashMap<>();
        ContentValues values = new ContentValues();
        for (Track track : trackList) {
            values.clear();
            values.put("title", track.getTitle());
            long id = db.insertOrThrow(TABLE_TRACK, null, values);
            trackIds.put(track, id);
        }
    }

    public void populateMilestones(SQLiteDatabase db) {

        milestoneList = new ArrayList<>();
        milestoneList.add(new Milestone("Réaumur-Sébastopol", 48.86638, 2.352567, "Réaumur - Sébastopol is a subway station for the lines 3 and 4, in Paris.", trackList.get(0)));
        milestoneList.add(new Milestone("Arts et Métiers", 48.865522, 2.355965, "Arts et Métiers is a subway station for the lines 3 and 11, in Paris.", trackList.get(0)));
        milestoneList.add(new Milestone("Temple", 48.866519, 2.360648, "Temple is a subway station for the line 3, in Paris.", trackList.get(0)));
        milestoneList.add(new Milestone("République", 48.867498, 2.363807, "République is a subway station for several lines, in Paris.", trackList.get(0)));
        milestoneList.add(new Milestone("Oberkampf", 48.864786, 2.368654, "No description", trackList.get(0)));
        milestoneList.add(new Milestone("Parmentier", 48.865497, 2.374383, "No description", trackList.get(0)));

        milestoneIds = new HashMap<>();
        ContentValues values = new ContentValues();
        for (Milestone milestone : milestoneList) {
            values.clear();
            values.put("title", milestone.getTitle());
            values.put("latitude", milestone.getPosition().latitude);
            values.put("longitude", milestone.getPosition().longitude);
            values.put("description", milestone.getDescription());
            values.put("track_id", trackIds.get(milestone.getTrack()));
            long id = db.insertOrThrow(TABLE_MILESTONE, null, values);
            milestoneIds.put(milestone, id);
        }
    }

    public void populateQuestions(SQLiteDatabase db) {

        questionList = new ArrayList<>();

        //Question 1
        questionList.add(new Question(
                "Here is an example of a single choice question, with buttons.",
                "[\"This is the right answer\",\"This is not the right answer\",\"This is not the right answer\",\"This is not the right answer\"]",
                "[1]",
                R.drawable.cnam,
                milestoneList.get(0),
                Question.TypeQuestion.BUTTON,
                Question.ChoiceType.SINGLE_CHOICE
        ));

        //Question 2
        questionList.add(new Question(
                "Here is an example of a multiple choice question, with buttons.",
                "[\"This is one right answer\",\"This is one right answer\",\"This is not the right answer\",\"This is one right answer\",\"This is not the right answer\"]",
                "[1,2,4]",
                0,
                milestoneList.get(1),
                Question.TypeQuestion.BUTTON,
                Question.ChoiceType.MULTIPLE_CHOICE
        ));

        //Question 3
        questionList.add(new Question(
                "Here is an example of a free input question with a single answer. Input the word \"rabbit\".",
                "[\"rabbit\"]",
                "[1]",
                0,
                milestoneList.get(2),
                Question.TypeQuestion.FREE_INPUT,
                Question.ChoiceType.SINGLE_CHOICE
        ));

        //Question 4
        questionList.add(new Question(
                "Here is an example of a free input question with multiple answers. Input the numbers 42 and 56.",
                "[\"42\",\"56\"]",
                "[1,2]",
                0,
                milestoneList.get(3),
                Question.TypeQuestion.FREE_INPUT,
                Question.ChoiceType.MULTIPLE_CHOICE
        ));

        //Question 5
        questionList.add(new Question(
                "Here is an example of a multiple choice question, with an image. Click on the first and third statue.",
                "[{\"bottom\":90.0,\"left\":10.0,\"right\":30.0,\"top\":10.0}," +
                        "{\"bottom\":90.0,\"left\":32.0,\"right\":45.0,\"top\":10.0}," +
                        "{\"bottom\":90.0,\"left\":47.0,\"right\":65.0,\"top\":10.0}," +
                        "{\"bottom\":90.0,\"left\":67.0,\"right\":85.0,\"top\":10.0}]",
                "[1,3]",
                R.drawable.stdenis,
                milestoneList.get(4),
                Question.TypeQuestion.VISUAL,
                Question.ChoiceType.MULTIPLE_CHOICE
        ));

        //Question 6
        questionList.add(new Question(
                "Here is an example of a single choice question with an image. Click on the head of the statue at the bottom left.",
                "[{\"bottom\":40.0,\"left\":35.0,\"right\":65.0,\"top\":10.0},\n" +
                        "{\"bottom\":95.0,\"left\":5.0,\"right\":30.0,\"top\":70.0},\n" +
                        "{\"bottom\":95.0,\"left\":70.0,\"right\":95.0,\"top\":70.0}]",
                "[2]",
                R.drawable.cnam2,
                milestoneList.get(5),
                Question.TypeQuestion.VISUAL,
                Question.ChoiceType.SINGLE_CHOICE
        ));

        ContentValues values = new ContentValues();
        for (Question question : questionList) {
            values.clear();
            values.put("title", question.getTitle());
            switch (question.getType()) {
                case VISUAL:
                    values.put("choices", Serializer.serializeFromListRectF(question.getChoiceList()));
                    break;
                default:
                    values.put("choices", Serializer.serializeFromListString(question.getChoiceList()));
                    break;
            }
            values.put("answers", Serializer.serializeFromSetInteger(question.getAnswerSet()));
            values.put("image", question.getImageId());
            values.put("question_type", question.getType().toString());
            values.put("choice_type", question.getChoiceType().toString());
            values.put("milestone_id", milestoneIds.get(question.getMilestone()));
            long id = db.insertOrThrow(TABLE_QUESTION, null, values);
        }
    }

    public void populateTreasures(SQLiteDatabase db) {

        treasureList = new ArrayList<>();
        treasureList.add(new Treasure("a blue rabbit", 40.4228, 3.707696, R.drawable.icon_blue, milestoneList.get(0)));
        treasureList.add(new Treasure("a kind rabbit", 0.4228, 20.707696, R.drawable.icon_green, milestoneList.get(0)));
        treasureList.add(new Treasure("a nice rabbit", 80.4228, -40.707696, R.drawable.icon_yellow, milestoneList.get(0)));
        treasureList.add(new Treasure("a sympathetic rabbit", -100.4228, 60.707696, R.drawable.icon_red, milestoneList.get(0)));
        treasureList.add(new Treasure("a pink rabbit", 40.4228, 3.707696, R.drawable.icon_red, milestoneList.get(1)));
        treasureList.add(new Treasure("a yellow rabbit", 40.4228, 3.707696, R.drawable.icon_yellow, milestoneList.get(2)));
        treasureList.add(new Treasure("an awesome rabbit", 33, 90, R.drawable.icon_yellow, milestoneList.get(2)));
        treasureList.add(new Treasure("a blue rabbit", 40.4228, 3.707696, R.drawable.icon_blue, milestoneList.get(3)));
        treasureList.add(new Treasure("a green rabbit", 40.4228, 3.707696, R.drawable.icon_green, milestoneList.get(4)));
        treasureList.add(new Treasure("a pink rabbit", 40.4228, 3.707696, R.drawable.icon_red, milestoneList.get(5)));

        ContentValues values = new ContentValues();
        for (Treasure treasure : treasureList) {
            values.clear();
            values.put("title", treasure.getTitle());
            values.put("latitude", treasure.getPosition().latitude);
            values.put("longitude", treasure.getPosition().longitude);
            values.put("image", treasure.getImageId());
            values.put("milestone_id", milestoneIds.get(treasure.getMilestone()));
            long id = db.insertOrThrow(TABLE_TREASURE, null, values);
        }
    }
}