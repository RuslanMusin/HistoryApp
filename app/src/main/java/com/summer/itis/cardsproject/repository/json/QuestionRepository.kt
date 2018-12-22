package com.summer.itis.cardsproject.repository.json

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.summer.itis.cardsproject.model.Question

import java.util.HashMap

class QuestionRepository {

    var databaseReference: DatabaseReference? = null
        private set

    val TABLE_NAME = "questions"

    private val FIELD_ID = "id"
    private val FIELD_QUESTION = "question"
    private val FIELD_PHOTO = "photoUrl"
    private val FIELD_WIKI = "wikiUrl"
    private val FIELD_ANSWERS = "answers"
    private val FIELD_RIGHT_ANSWERS = "right_answers"

    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun toMap(question: Question): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        val id = databaseReference!!.push().key
        question.id = id
        result[FIELD_ID] = question.id
        result[FIELD_QUESTION] = question.question
        result[FIELD_ANSWERS] = question.answers

        return result
    }

    fun setDatabaseReference(path: String) {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun getKey(crossingId: String): String? {
        return databaseReference!!.child(crossingId).push().key
    }

    /*public void createPoint(BookCrossing crossing, Point point) {
        String pointKey = getKey(crossing.getId());
        Map<String, Object> pointValues = toMap(point);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(TABLE_NAME + SEP + crossing.getId() + SEP + pointKey, pointValues);
        databaseReference.getRoot().updateChildren(childUpdates);
    }

    public DatabaseReference readPoint(String pointId) {
        return databaseReference.child(pointId);
    }

    public void deletePoint(String pointId){
        databaseReference.child(pointId).removeValue();
    }

    public void updateUser(Point point){
        Map<String, Object> updatedValues = new HashMap<>();
        databaseReference.child(point.getId()).updateChildren(updatedValues);
    }

    public DatabaseReference getPoints() {
        return databaseReference.getRoot();
    }

    public Single<Query> loadPoints(String crossingId){
        return Single.just(databaseReference.child(crossingId));

    }

    public Single<Query> findPoint(String crossingId, String userId){
        DatabaseReference reference = databaseReference.child(crossingId);
        Query query = reference.orderByChild(FIELD_EDITOR).equalTo(userId);
        return Single.just(query);

    }*/
}
