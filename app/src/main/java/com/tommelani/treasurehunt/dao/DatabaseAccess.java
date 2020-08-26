package com.tommelani.treasurehunt.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.tommelani.treasurehunt.helpers.Serializer;
import com.tommelani.treasurehunt.models.Milestone;
import com.tommelani.treasurehunt.models.Question;
import com.tommelani.treasurehunt.models.Track;
import com.tommelani.treasurehunt.models.Treasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.valueOf;

public class DatabaseAccess {

    private SQLiteOpenHelper databaseHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;
    private Context context;

    protected DatabaseAccess(Context context) {
        this.databaseHelper = new DatabaseFiller(context);
        this.context = context;
    }


    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }


    public void open() {
        this.database = databaseHelper.getWritableDatabase();
    }


    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
    * Transform a cursor
    * */
    public List<Map<String, String>> readQuery(Cursor cursor) {
        List<Map<String, String>> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> data = new HashMap<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    data.put(cursor.getColumnName(i), cursor.getString(i));
                }
                list.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
    * Transform a data list into a question
    * */
    public List<Question> convertToQuestionList(List<Map<String, String>> data) {
        List<Question> list = new ArrayList<>();
        for (Map<String, String> item : data) {

            Question question = new Question();

            question.setType(Question.TypeQuestion.valueOf(item.get("question_type")));
            question.setId(Long.parseLong(item.get("_id")));
            question.setTitle(item.get("title"));

            List<Object> choiceList;
            switch (question.getType()) {
                default:
                    choiceList = Serializer.deserializeToListString(item.get("choices"));
                    question.setChoiceList(choiceList);
                    break;
                case VISUAL:
                    choiceList = Serializer.deserializeToListRectF(item.get("choices"));
                    question.setChoiceList(choiceList);
                    break;
            }

            question.setChoiceType(Question.ChoiceType.valueOf(item.get("choice_type")));

            Set<Integer> answerSet = Serializer.deserializeToSetInteger(item.get("answers"));
            question.setAnswerSet(answerSet);

            if (item.get("image") != null) {
                question.setImageId(context.getResources().getIdentifier(item.get("image"), "drawable", context.getPackageName()));
            }


            list.add(question);

        }
        return list;
    }

    /**
    * Transform a data list into a milestone
    * */
    public List<Milestone> convertToMilestoneList(List<Map<String, String>> data) {
        List<Milestone> list = new ArrayList<>();
        for (Map<String, String> item : data) {
            Milestone milestone = new Milestone();
            milestone.setId(Long.parseLong(item.get("_id")));
            milestone.setTitle(item.get("title"));
            milestone.setDescription(item.get("description"));

            LatLng position = new LatLng(
                    Double.parseDouble(item.get("latitude")),
                    Double.parseDouble(item.get("longitude"))
            );
            milestone.setPosition(position);

            list.add(milestone);
        }
        return list;
    }

    /**
    * Transform a data list into a track
    * */
    public List<Track> convertToTrackList(List<Map<String, String>> data) {
        List<Track> list = new ArrayList<>();
        for (Map<String, String> item : data) {
            Track track = new Track();
            track.setId(Long.parseLong(item.get("_id")));
            track.setTitle(item.get("title"));

            list.add(track);
        }
        return list;
    }

    /**
    *  Transform a data list into a treasure
    * */
    public List<Treasure> convertToTreasureList(List<Map<String, String>> data) {
        List<Treasure> list = new ArrayList<>();
        for (Map<String, String> item : data) {
            Treasure treasure = new Treasure();
            treasure.setId(Long.parseLong(item.get("_id")));
            treasure.setTitle(item.get("title"));

            LatLng position = new LatLng(
                    Double.parseDouble(item.get("latitude")),
                    Double.parseDouble(item.get("longitude"))
            );
            treasure.setPosition(position);

            treasure.setImageId(context.getResources().getIdentifier(item.get("image"), "drawable", context.getPackageName()));

            list.add(treasure);
        }
        return list;
    }

    public Question getRandomQuestionFromMilestone(long milestoneId) {
        String query = "SELECT * FROM " + DatabaseFiller.TABLE_QUESTION + " WHERE milestone_id = ? ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = database.rawQuery(query, new String[]{valueOf(milestoneId)});
        return convertToQuestionList(readQuery(cursor)).get(0);
    }

    public List<Treasure> getTreasuresFromMilestone(long milestoneId) {
        String query = "SELECT * FROM " + DatabaseFiller.TABLE_TREASURE + " WHERE milestone_id = ? ";
        Cursor cursor = database.rawQuery(query, new String[]{valueOf(milestoneId)});
        return convertToTreasureList(readQuery(cursor));
    }

    public List<Milestone> getMilestonesFromTrack(long trackId) {
        String query = "SELECT * FROM " + DatabaseFiller.TABLE_MILESTONE + " WHERE track_id = ? ORDER BY _id ASC";
        Cursor cursor = database.rawQuery(query, new String[]{valueOf(trackId)});
        return convertToMilestoneList(readQuery(cursor));
    }

    public List<Track> getAllTracks() {
        String query = "SELECT * FROM " + DatabaseFiller.TABLE_TRACK;
        Cursor cursor = database.rawQuery(query, null);
        return convertToTrackList(readQuery(cursor));
    }


}