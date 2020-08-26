package com.tommelani.treasurehunt.models;

import com.tommelani.treasurehunt.helpers.Serializer;

import java.util.List;
import java.util.Set;

/**
 * Class Question
 */
public class Question {

    public enum ChoiceType {MULTIPLE_CHOICE, SINGLE_CHOICE}

    public enum TypeQuestion {BUTTON, VISUAL, FREE_INPUT}

    private long id;
    private Milestone milestone;
    private String title;
    private int imageId;
    private Set<Integer> answerSet;
    private List<Object> choiceList;
    private ChoiceType choiceType;
    private TypeQuestion questionType;

    public Question() {
    }

    public Question(String title, String choices, String answers, int imageId, Milestone milestone, TypeQuestion questionType, ChoiceType choiceType) {

        this.title = title;

        switch (questionType) {
            case VISUAL:
                this.choiceList = Serializer.deserializeToListRectF(choices);
                break;
            default:
                this.choiceList = Serializer.deserializeToListString(choices);
                break;
        }

        this.answerSet = Serializer.deserializeToSetInteger(answers);

        this.imageId = imageId;
        this.milestone = milestone;

        this.questionType = questionType;
        this.choiceType = choiceType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Milestone getMilestone() {
        return milestone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public Set<Integer> getAnswerSet() {
        return answerSet;
    }

    public void setAnswerSet(Set<Integer> answerSet) {
        this.answerSet = answerSet;
    }

    public List<Object> getChoiceList() {
        return choiceList;
    }

    public void setChoiceList(List<Object> choiceList) {
        this.choiceList = choiceList;
    }

    public TypeQuestion getType() {
        return questionType;
    }

    public void setType(TypeQuestion type) {
        this.questionType = type;
    }

    public ChoiceType getChoiceType() {
        return choiceType;
    }

    public void setChoiceType(ChoiceType choiceType) {
        this.choiceType = choiceType;
    }

    /**
     * Check if the user answered correctly
     * @param replySet answers
     * @return boolean
     */
    public boolean isPassed(Set<Integer> replySet) {
        return replySet.equals(this.answerSet);
    }


}
