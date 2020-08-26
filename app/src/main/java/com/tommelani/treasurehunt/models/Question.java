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

    private long id; //Unique identifier
    private Milestone milestone; //Related milestone
    private String title; //Title
    private int imageId; //Image Id
    private Set<Integer> answerSet; //Unordered set of answers
    private List<Object> choiceList; //Ordered list of choices
    private ChoiceType choiceType; //Type of choice (multiple or single)
    private TypeQuestion questionType; //Type of question (with buttons, image or free input)

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
