package com.summer.itis.cardsproject.repository.json;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.summer.itis.cardsproject.model.GameOne;
import com.summer.itis.cardsproject.ui.start.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;

import static com.summer.itis.cardsproject.utils.Const.SEP;

public class OldGameRepository {

    private DatabaseReference databaseReference;

    public final String TABLE_NAME = "games";

    private final String FIELD_ID = "id";
    private final String FIELD_QUESTION = "question";
    private final String FIELD_SCORE = "score";
    private final String FIELD_CARD = "card";
    private final String FIELD_ENEMY_ID = "enemyId";
    private final String FIELD_GAME_ID = "gameId";


    public OldGameRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME);
    }

    public Map<String, Object> toMap(GameOne card) {
        HashMap<String, Object> result = new HashMap<>();

        result.put(FIELD_ID,card.getId());
        result.put(FIELD_QUESTION, card.getQustionId());
        result.put(FIELD_SCORE, card.getScore());
        result.put(FIELD_CARD, card.getCardId());
        result.put(FIELD_ENEMY_ID, card.getEnemyId());
        result.put(FIELD_GAME_ID, card.getGameId());

        return result;
    }




    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(String path) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME);
    }

    public String getKey(String crossingId){
        return databaseReference.child(crossingId).push().getKey();
    }

    public void createGameTwo(GameOne crossing, String gameId, LoginActivity loginActivity) {
        crossing.setId(databaseReference.child(gameId).push().getKey());
        crossing.setGameId(gameId);
        Map<String, Object> pointValues = toMap(crossing);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(TABLE_NAME + SEP + gameId + SEP + crossing.getId(), pointValues);
        databaseReference.getRoot().updateChildren(childUpdates);
    }

    public String createGameOne(GameOne crossing) {
        String gameId = databaseReference.push().getKey();
        crossing.setId(databaseReference.child(gameId).push().getKey());
        crossing.setGameId(gameId);
        Map<String, Object> pointValues = toMap(crossing);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(TABLE_NAME + SEP + gameId + SEP + crossing.getId(), pointValues);
        databaseReference.getRoot().updateChildren(childUpdates);

        return gameId;
    }

    public DatabaseReference readPoint(String pointId) {
        return databaseReference.child(pointId);
    }

    public void setEnemy(GameOne gameOne) {
        Map<String, Object> childUpdates = new HashMap<>();
        DatabaseReference query = databaseReference.child(gameOne.getGameId() + SEP + gameOne.getId() + SEP +  FIELD_ENEMY_ID);
        query.setValue(gameOne.getEnemyId());
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

