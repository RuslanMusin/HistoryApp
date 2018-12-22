package com.summer.itis.cardsproject.repository.json

import android.util.Log
import com.google.firebase.database.*
import com.summer.itis.cardsproject.model.Card
import com.summer.itis.cardsproject.model.User
import com.summer.itis.cardsproject.model.db_dop_models.Relation
import com.summer.itis.cardsproject.model.game.CardChoose
import com.summer.itis.cardsproject.model.game.GameData
import com.summer.itis.cardsproject.model.game.Lobby
import com.summer.itis.cardsproject.model.game.LobbyPlayerData
import com.summer.itis.cardsproject.repository.RepositoryProvider
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.cardRepository
import com.summer.itis.cardsproject.repository.RepositoryProvider.Companion.userRepository
import com.summer.itis.cardsproject.repository.json.UserRepository.Companion.FIELD_LOBBY_ID
import com.summer.itis.cardsproject.utils.ApplicationHelper
import com.summer.itis.cardsproject.utils.Const.BOT_ID
import com.summer.itis.cardsproject.utils.Const.IN_GAME_STATUS
import com.summer.itis.cardsproject.utils.Const.MODE_END_GAME
import com.summer.itis.cardsproject.utils.Const.NOT_ACCEPTED
import com.summer.itis.cardsproject.utils.Const.OFFICIAL_TYPE
import com.summer.itis.cardsproject.utils.Const.OFFLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.ONLINE_GAME
import com.summer.itis.cardsproject.utils.Const.ONLINE_STATUS
import com.summer.itis.cardsproject.utils.Const.QUERY_END
import com.summer.itis.cardsproject.utils.Const.TAG_LOG
import com.summer.itis.cardsproject.utils.Const.USER_TYPE
import com.summer.itis.cardsproject.utils.RxUtils
import com.summer.itis.cardsproject.utils.getRandom
import io.reactivex.Single


class GamesRepository {
    val allDbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    val TABLE_GAMES = "games_v3"
    val gamesDbRef: DatabaseReference = allDbRef.child(TABLE_GAMES)

    val TABLE_SEARCHING = "searching"
    val searchingDbRef: DatabaseReference = gamesDbRef.child(TABLE_SEARCHING)

    val lobbiesDbRef: DatabaseReference = gamesDbRef.child(TABLE_LOBBIES)

    val CREATOR_LOBBY = "creator"
    val LOBBY_TYPE = "type"
    val FIELD_LOWER_TITLE = "lobby_title"
    lateinit var nowSearchingDbRef: DatabaseReference


    lateinit var currentLobbyRef: DatabaseReference
    lateinit var creatorLobbyRef: DatabaseReference
    lateinit var invitedLobbyRef: DatabaseReference

    lateinit var myLobbyRef: DatabaseReference
    lateinit var enemyLobbyRef: DatabaseReference

    var callbacks: InGameCallbacks? = null

    var lastEnemyChoose: CardChoose? = null
    var lastMyChosenCardId: String? = null

    var enemyId: String? = null

    var enemy_answers = 0;
    var enemy_score = 0;

    var my_answers = 0;
    var my_score = 0;

    var onYouLoseCard: Card? = null
    var onEnemyLoseCard: Card? = null

    var listeners = HashMap<DatabaseReference, ValueEventListener>()

    private val databaseReference: DatabaseReference


    val TABLE_NAME = "lobbies"

    private val FIELD_ID = "id"
    private val FIELD_WIKI_URL = "wikiUrl"
    private val FIELD_NAME = "name"
    private val FIELD_LOWER_NAME = "lowerName"
    private val FIELD_PHOTO_URL = "photoUrl"
    private val FIELD_EXTRACT = "extract"
    private val FIELD_DESCRIPTION = "description"


    init {
        this.databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)
    }

    fun setLobbyRefs(lobbyId: String) {
        currentLobbyRef = databaseReference.child(lobbyId)

        ApplicationHelper.currentUser?.let {
            if(it.gameLobby?.gameData?.role.equals(FIELD_INVITED)) {
                myLobbyRef = currentLobbyRef.child(FIELD_INVITED)
                enemyLobbyRef = currentLobbyRef.child(FIELD_CREATOR)
            } else {
                myLobbyRef = currentLobbyRef.child(FIELD_CREATOR)
                enemyLobbyRef = currentLobbyRef.child(FIELD_INVITED)
            }
        }
      /*  creatorLobbyRef = currentLobbyRef.child(FIELD_CREATOR)
        invitedLobbyRef = currentLobbyRef.child(FIELD_INVITED)*/
    }

    fun removeLobby(id: String) {
        Log.d(TAG_LOG,"remove lobby $id")
        databaseReference.child(id).removeValue()
        ApplicationHelper.currentUser?.let {
            it.lobbyId = null
            databaseReference.root.child(UserRepository.TABLE_NAME).child(it.id).child(FIELD_LOBBY_ID).setValue(null)
            databaseReference.root.child(USERS_LOBBIES).child(it.id).child(id).setValue(null)
        }
    }


    fun resetData() {

        callbacks = null
        lastEnemyChoose = null
        lastMyChosenCardId = null
        enemyId = null
        enemy_answers = 0;
        enemy_score = 0;
        my_answers = 0;
        my_score = 0;

        onYouLoseCard = null
        onEnemyLoseCard = null

        removeListeners()


    }

    fun removeListeners() {
        for (l in listeners) {
            l.key.removeEventListener(l.value)
        }
        listeners.clear()
    }


  /*  fun startSearchGame(onFind: () -> (Unit)): Single<Boolean> {
        val single: Single<Boolean> = Single.create() { e ->
            searchingDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        val selected = dataSnapshot.children.first()

                        val lobbyId = selected.value as String

                        selected.ref.removeValue()

                        goToLobby(lobbyId, onFind)

                    } else {
                        Log.d(TAG_LOG, "create lobby")
                        createLobby(onFind)
                    }
                    e.onSuccess(true)

                }
            })
        }
        return single.compose(RxUtils.asyncSingle())
    }*/

    fun joinBot(lobby: Lobby): Single<Boolean> {
        Log.d(TAG_LOG,"join bot")
        val single: Single<Boolean> = Single.create { e ->
            currentLobbyRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                     /*   currentLobbyRef = lobbiesDbRef.child(UserRepository.currentId)

                        val lobbyPlayerData = LobbyPlayerData(BOT_ID, true, null, null, null)
                        invitedLobbyRef!!.setValue(lobbyPlayerData)

                        invitedLobbyRef!!.child(LobbyPlayerData.PARAM_online).onDisconnect().setValue(false)
*/
                    val lobbyPlayerData = LobbyPlayerData()
                    lobbyPlayerData.playerId = BOT_ID
                    lobbyPlayerData.online = true
                    lobby.id?.let {
                        val relation: Relation = Relation()
                        relation.relation = IN_GAME_STATUS
                        relation.id = it
                        databaseReference.root.child(USERS_LOBBIES).child(UserRepository.currentId).child(it).child(FIELD_RELATION).setValue(relation)
                        enemyLobbyRef.setValue(lobbyPlayerData)
                        e.onSuccess(true)
                    }


                }
            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun setRelation(relation: Relation,userId: String) {
        databaseReference.root.child(USERS_LOBBIES).child(userId).child(relation.id).child(FIELD_RELATION).setValue(relation)
    }

    fun createLobby(lobby: Lobby, onFind: () -> (Unit)) {
        val lobbyId: String? = databaseReference.push().key
//        currentLobbyRef!!.setValue(lobby)

     /*   nowSearchingDbRef = searchingDbRef.child(currentLobbyRef!!.key!!)
        nowSearchingDbRef!!.setValue(currentLobbyRef!!.key)

        nowSearchingDbRef!!.onDisconnect().removeValue()

        creatorLobbyRef = currentLobbyRef!!.child(Lobby.PARAM_creator)
        invitedLobbyRef = currentLobbyRef!!.child(Lobby.PARAM_invited)*/

        lobbyId?.let {
            lobby.id = lobbyId
            databaseReference.child(it).setValue(lobby)
            ApplicationHelper.currentUser?.let {
                it.id?.let { it1 -> databaseReference.root.child(UserRepository.TABLE_NAME).child(it1).child(FIELD_LOBBY_ID).setValue(lobbyId)
                    val relation:Relation = Relation()
                    relation.id = lobbyId
                    relation.relation = ONLINE_STATUS
                databaseReference.root.child(USERS_LOBBIES).child(it1).child(lobbyId).setValue(relation)
                }
                it.lobbyId = lobbyId
            }

        }
//        creatorLobbyRef!!.child(LobbyPlayerData.PARAM_online).onDisconnect().setValue(false)
        //TODO remove lobby on disconnect?
        onFind()
    }

    fun removeFastLobby(userId: String, lobby: Lobby): Single<Boolean> {
        Log.d(TAG_LOG,"remove fast lobby")
        val single: Single<Boolean> = Single.create { e ->

            val lobbyId: String? = lobby.id
            lobbyId?.let {
                databaseReference.child(it).removeValue()
                ApplicationHelper.currentUser?.let {
                    it.id.let { it1 ->
                        databaseReference.root.child(USERS_LOBBIES).child(it1).child(lobbyId).removeValue()
                        databaseReference.root.child(USERS_LOBBIES).child(userId).child(lobbyId).removeValue()
                    }
                    e.onSuccess(true)
                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun createFastLobby(userId: String, lobby: Lobby): Single<Lobby> {
        Log.d(TAG_LOG,"create fast lobby")
        val single: Single<Lobby> = Single.create { e ->

            val lobbyId: String? = databaseReference.push().key
            lobbyId?.let {
                lobby.id = lobbyId
                databaseReference.child(it).setValue(lobby)
                ApplicationHelper.currentUser?.let {
                    it.id.let { it1 ->
                        val relation:Relation = Relation()
                        relation.id = lobbyId
                        relation.relation = ONLINE_STATUS
                        databaseReference.root.child(USERS_LOBBIES).child(it1).child(lobbyId).setValue(relation)
                        relation.relation = IN_GAME_STATUS
                        databaseReference.root.child(USERS_LOBBIES).child(userId).child(lobbyId).setValue(relation)
                    }
                    val gameData: GameData = GameData()
                    gameData.enemyId = userId
                    gameData.gameMode = ONLINE_GAME
                    gameData.role = FIELD_CREATOR
                    it.gameLobby = lobby
                    it.gameLobby?.gameData = gameData
                    setLobbyRefs(lobbyId)
                    e.onSuccess(lobby)
                }
            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun createBotLobby(lobby: Lobby, onFind: () -> (Unit)) {
        val lobbyId: String? = databaseReference.push().key

        lobbyId?.let {
            lobby.id = lobbyId
            setLobbyRefs(lobbyId)
//            currentLobbyRef.setValue(lobby)
        }
        onFind()
    }

    fun refuseGame(lobby: Lobby): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->
            Log.d(TAG_LOG, "refuse game")
            val relation: Relation = Relation()
            relation.id = lobby.id
            relation.relation = NOT_ACCEPTED
            lobby.gameData?.enemyId?.let {
                Log.d(TAG_LOG,"refuse in db")
                databaseReference.root.child(USERS_LOBBIES).child(it).child(lobby.id).setValue(relation)
            }
            ApplicationHelper.currentUser.let {
                databaseReference.root.child(USERS_LOBBIES).child(it.id).child(lobby.id).setValue(null).addOnCompleteListener{ e.onSuccess(true)}

            }
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun waitEnemy(): Single<Relation>{
        val single: Single<Relation> = Single.create{ e ->
            val user: User? = ApplicationHelper.currentUser
            user?.id?.let {
                val query: Query = databaseReference.root.child(USERS_LOBBIES).child(it)
                query.addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for(snap in p0.children) {
                            val relation: Relation? = snap.getValue(Relation::class.java)
                            relation?.let {
                                if(it.relation.equals(IN_GAME_STATUS)) {
                                    Log.d(TAG_LOG,"wait enemy in game")
                                    user.status = IN_GAME_STATUS
                                    query.removeEventListener(this)
                                    e.onSuccess(relation)
                                    /*findLobby(relation.id).subscribe { lobby ->
                                        query.removeEventListener(this)
                                        e.onSuccess(lobby)
                                    }*/
                                }
                                if(it.relation.equals(NOT_ACCEPTED)) {
                                    Log.d(TAG_LOG,"enemy refused")
                                    query.removeEventListener(this)
                                    e.onSuccess(relation)
                                }
                            }
                        }
                    }

                })
            }

        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun disconnectMe(): Single<Boolean> {
        val single: Single<Boolean> = Single.create { e ->
            Log.d(TAG_LOG, "disconnect me")
            val myConnect = databaseReference.root.child(UserRepository.TABLE_NAME).child(UserRepository.currentId).child(UserRepository.FIELD_STATUS)
            myConnect.setValue(OFFLINE_STATUS)
            e.onSuccess(true)
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findLobby(lobbyId: String): Single<Lobby> {
        val single: Single<Lobby> = Single.create{ e ->
                val query: Query = databaseReference.child(lobbyId)
                query.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()) {
                            Log.d(TAG_LOG,"lobby finded")
                            val lobby: Lobby? = p0.getValue(Lobby::class.java)
                            lobby?.let {
                                e.onSuccess(lobby)
                            }
                        } else {
                            Log.d(TAG_LOG,"lobby not exist")
                        }
                    }

                })


        }
        return single.compose(RxUtils.asyncSingle())
    }

 /*   fun createNotification(gameMode: String): Notification {
        // Create PendingIntent
        val resultIntent = Intent(com.summer.itis.cardsproject.Application.getContext(), PlayGameActivity::class.java)
        resultIntent.putExtra(PlayGameActivity.GAME_MODE,gameMode)
        val resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

// Create Notification
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Title")
                .setContentText("Notification text")
                .setContentIntent(resultPendingIntent)

        return builder.build()


    }*/

    fun goToLobby(lobby: Lobby, onFind: () -> (Unit), onNotAccepted: () -> (Unit)) {
       /* currentLobbyRef = lobbiesDbRef.child(lobbyId)

        invitedLobbyRef = currentLobbyRef!!.child(Lobby.PARAM_creator)
        creatorLobbyRef = currentLobbyRef!!.child(Lobby.PARAM_invited)
        */

        setLobbyRefs(lobby.id)

        val playerData = LobbyPlayerData()
        playerData.playerId = UserRepository.currentId
        playerData.online = true

        myLobbyRef!!.setValue(playerData)

//        invitedLobbyRef!!.child(LobbyPlayerData.PARAM_online).onDisconnect().setValue(false)

        val relation: Relation = Relation()
        relation.id = lobby.id
        relation.relation = ONLINE_STATUS
        lobby.creator?.playerId?.let {
            ApplicationHelper.currentUser?.let {user ->
               /* val gameData: GameData = GameData()
                gameData.gameMode = ONLINE_GAME
                gameData.enemyId = it
                gameData.role = FIELD_INVITED
                user.gameLobby = lobby
                user.gameLobby?.gameData = gameData*/
                databaseReference.root.child(USERS_LOBBIES).child(user.id).child(lobby.id).setValue(relation)
            }
            relation.relation = IN_GAME_STATUS
            databaseReference.root.child(USERS_LOBBIES).child(it).child(lobby.id).setValue(relation)



        }



        databaseReference.root.child(USERS_LOBBIES).child(UserRepository.currentId).child(lobby.id).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snap: DataSnapshot) {
                val relation: Relation? = snap.getValue(Relation::class.java)
                relation?.let {
                    if(it.relation.equals(IN_GAME_STATUS)) {
                        ApplicationHelper.currentUser?.let { it1 ->
                            it1.status = IN_GAME_STATUS
                            userRepository.changeUserStatus(it1).subscribe() }
                        onFind()
                    } else if(it.relation.equals(NOT_ACCEPTED)) {
                        onNotAccepted()
                    }
                }
            }

        })



       /* invitedLobbyRef!!.child(LobbyPlayerData.PARAM_playerId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                enemyId = dataSnapshot.value as String

                onFind()
            }
        })*/
    }

    fun notAccepted(lobby: Lobby) {
        ApplicationHelper.currentUser?.let {
            Log.d(TAG_LOG,"not accept")
                    databaseReference.root.child(USERS_LOBBIES).child(it.id).child(lobby.id).setValue(null)
                    myLobbyRef.setValue(null)

            }
    }

    fun acceptMyGame(lobby: Lobby): Single<Boolean> {
        return Single.create { e ->
            val relation: Relation = Relation()
            relation.id = lobby.id
            relation.relation = IN_GAME_STATUS
            lobby.gameData?.enemyId?.let {
                databaseReference.root.child(USERS_LOBBIES).child(it).child(lobby.id).setValue(relation)
                e.onSuccess(true)
            }
        }
    }

    fun cancelSearchGame(onCanceled: () -> (Unit)) {
        creatorLobbyRef!!.child(LobbyPlayerData.PARAM_online).onDisconnect().cancel()
        //TODO for parent ?

        nowSearchingDbRef!!.removeValue().addOnSuccessListener { onCanceled() }
        currentLobbyRef!!.removeValue()
    }

    fun getPlayerId(): String? {
        return UserRepository.currentId
    }

    //in game


    fun startGame(lobby: Lobby, callbacks: InGameCallbacks) {
        this.callbacks = callbacks

        selectOnLoseCard(lobby)

//        invitedLobbyRef!!.child(LobbyPlayerData.PARAM_choosedCards)

                enemyLobbyRef.child(LobbyPlayerData.PARAM_choosedCards)
                        .addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

                    override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
//                        val cardChoose: CardChoose = dataSnapshot.getValue(CardChoose::class.java)!!
//                        callbacks.onEnemyCardChosen(cardChoose)
//                        lastEnemyChoose=cardChoose

                        lastEnemyChoose = dataSnapshot.getValue(CardChoose::class.java)!!
                        callbacks.onEnemyCardChosen(lastEnemyChoose!!)
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {}
                })

        enemyLobbyRef.child(LobbyPlayerData.PARAM_answers)
                .addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

                    override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
                        Log.d("Alm", "onChildAdded to enemy answers")
                        val correct = dataSnapshot.value as Boolean
                        callbacks.onEnemyAnswered(correct)

                        enemy_answers++
                        if (correct) {
                            enemy_score++
                        }
                        Log.d(TAG_LOG,"enemyAnswers = $enemy_answers")
                        Log.d(TAG_LOG,"enemyScroe = $enemy_score")
                        checkGameEnd(lobby)
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {}
                })



        val myConnect = databaseReference.root.child(UserRepository.TABLE_NAME).child(UserRepository.currentId).child(UserRepository.FIELD_STATUS)
        val connectedRef = myLobbyRef.child(FIELD_ONLINE)
        connectedRef.setValue(true)
        myConnect.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(OFFLINE_STATUS.equals(snapshot.value)) {
                    Log.d(TAG_LOG,"my disconnect")
                    onDisconnectAndLose(true)
                }
            }

        })
        myConnect.onDisconnect().setValue(OFFLINE_STATUS)

        lobby.gameData?.enemyId?.let {
            val enemyConnect = databaseReference.root.child(UserRepository.TABLE_NAME).child(it).child(UserRepository.FIELD_STATUS)
            enemyConnect.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && OFFLINE_STATUS.equals(snapshot.value)) {
                        Log.d(TAG_LOG, "enemy disconnect")
                        onEnemyDisconnectAndYouWin(lobby)
                    }
                }
            })
        }



        /*val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

          val myConnectingLisener = connectedRef.addValueEventListener(object : ValueEventListener {
              override fun onDataChange(snapshot: DataSnapshot) {
                  val connected = snapshot.getValue(Boolean::class.java)!!
                  if (!connected) {
                      onDisconnectAndLose()
                  }
              }

              override fun onCancelled(error: DatabaseError) {
  //                System.err.println("Listener was cancelled")
              }
          })
        listeners.put(connectedRef, myConnectingLisener)


        val enemyConnectionListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.value == false) {
                    onEnemyDisconnectAndYouWin(lobby)
                }
            }
        }
        enemyLobbyRef!!.child(LobbyPlayerData.PARAM_online)
                .addValueEventListener(enemyConnectionListener)
        listeners.put(enemyLobbyRef!!.child(LobbyPlayerData.PARAM_online), enemyConnectionListener)*/
    }

    private fun readCardsByType(lobbyId: String, type: String): Single<List<Card>> {
        val singleCards: Single<List<Card>>
        if(type.equals(OFFICIAL_TYPE)) {
            singleCards = cardRepository.findOfficialMyCards(lobbyId)
        } else {
            singleCards = cardRepository.findOfficialMyCards(lobbyId)
        }
        return singleCards
    }

    private fun selectOnLoseCard(lobby: Lobby) {

        lobby.gameData?.enemyId?.let {
            readCardsByType(it,lobby.type).subscribe { enemyCards: List<Card>? ->
            readCardsByType(UserRepository.currentId,lobby.type).subscribe { myCards: List<Card>? ->
                onYouLoseCard = ArrayList(myCards).minus(enemyCards!!).getRandom()
                if(onYouLoseCard == null) {
                    onYouLoseCard = myCards?.getRandom()
                }

                Log.d(TAG_LOG,"onYouLoseCard = ${onYouLoseCard?.id}")
                //TODO если нет подходящей карты
                // возможно стоит обрабатывать уже при входе в лобби

                myLobbyRef
                        .child(LobbyPlayerData.PARAM_randomSendOnLoseCard)
                        .setValue(onYouLoseCard!!.id)
            }
        }
        }

        enemyLobbyRef!!
                .child(LobbyPlayerData.PARAM_randomSendOnLoseCard)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            RepositoryProvider.cardRepository
                                    .readCard(dataSnapshot.value as String)
                                    .subscribe { t: Card? ->
                                        onEnemyLoseCard = t!!
                                    }
                        }
                    }
                })

    }

    fun selectOnBotLoseCard(cards: List<Card>) {
        onEnemyLoseCard = cards.getRandom()
    }

    fun chooseNextCard(lobby: Lobby,cardId: String) {
        lastMyChosenCardId = cardId;
        RepositoryProvider.cardRepository.readCard(cardId).subscribe { card: Card? ->

            val questionId = card!!.test.questions.getRandom()!!.id


            val choose = CardChoose(cardId, questionId!!)

//            creatorLobbyRef!!.child(LobbyPlayerData.PARAM_choosedCards).push().setValue(choose)
           myLobbyRef.child(LobbyPlayerData.PARAM_choosedCards).push().setValue(choose)

        }
    }

    fun botNextCard(lobby: Lobby, cardId: String) {
        RepositoryProvider.cardRepository.readCard(cardId).subscribe { card: Card? ->

            val questionId = card!!.test.questions.getRandom()!!.id


            val choose = CardChoose(cardId, questionId!!)

            enemyLobbyRef.child(LobbyPlayerData.PARAM_choosedCards).push().setValue(choose)
        }
    }

    fun answerOnLastQuestion(lobby: Lobby, correct: Boolean) {
        val query: Query =  myLobbyRef
                .child(LobbyPlayerData.PARAM_choosedCards)
                .orderByKey()
                .limitToLast(1)

        my_answers++
        if (correct) {
            my_score++
        }
        Log.d(TAG_LOG,"myAnswers = $my_answers")
        Log.d(TAG_LOG,"myScore = $my_score")

        checkGameEnd(lobby)

        query.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val key = dataSnapshot.key!!

                myLobbyRef.child(LobbyPlayerData.PARAM_answers)
                        .child(key)
                        .setValue(correct)

                query.removeEventListener(this)
            }

            override fun onChildRemoved(p0: DataSnapshot) {}
        })

    }

    fun botAnswer(lobby: Lobby, correct: Boolean) {
        val query: Query = enemyLobbyRef
                .child(LobbyPlayerData.PARAM_choosedCards)
                .orderByKey()
                .limitToLast(1)

        query.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val key = dataSnapshot.key!!

                enemyLobbyRef.child(LobbyPlayerData.PARAM_answers)
                        .child(key)
                        .setValue(correct)

                query.removeEventListener(this)
            }

            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }

    private fun checkGameEnd(lobby: Lobby) {
        if (enemy_answers == lobby.cardNumber && my_answers == lobby.cardNumber) {
            Log.d("Alm", "repo: GAME END!!!")

            Log.d("Alm", "repo: GAME END onEnemyLoseCard: " + onEnemyLoseCard!!.id)
            Log.d("Alm", "repo: GAME END onYouLoseCard: " + onYouLoseCard!!.id)
            changeGameMode(MODE_END_GAME).subscribe{ changed ->
                waitGameMode(MODE_END_GAME).subscribe{ waited ->
                    //TODO
                    Log.d(TAG_LOG,"gameEnd and removeLobby")
                    removeLobbyAndRelations(lobby)

                    if (my_score > enemy_score) {
                        onWin(lobby)

                    } else if (enemy_score > my_score) {
                        onLose()

                    } else {
                        //TODO
                        compareLastCards(lobby)
                    }
                }
            }

        }
    }

    private fun removeLobbyAndRelations(lobby: Lobby) {
        if(lobby.isFastGame) {
            currentLobbyRef.setValue(null)
            val reference: DatabaseReference = databaseReference.root.child(USERS_LOBBIES).child(UserRepository.currentId)
            reference.child(lobby.id).setValue(null)
        }
        if(!lobby.creator?.playerId.equals(UserRepository.currentId)) {
            val reference: DatabaseReference = databaseReference.root.child(USERS_LOBBIES).child(UserRepository.currentId)
            reference.child(lobby.id).setValue(null)
        } else {
            val playerData: LobbyPlayerData = LobbyPlayerData()
            playerData.online = true
            playerData.playerId = UserRepository.currentId
            myLobbyRef.setValue(playerData)
            enemyLobbyRef.setValue(null)
            databaseReference.root.child(USERS_LOBBIES).child(UserRepository.currentId).child(lobby.id).setValue(null)
        }

     /*   ApplicationHelper.currentUser?.let{
            it.status = ONLINE_STATUS
            userRepository.changeUserStatus(it).subscribe()
        }*/
    }

    fun removeRedundantLobbies(shouldFind: Boolean) {
        Log.d(TAG_LOG,"removeRedundantLobbies")
        if(ApplicationHelper.userInSession) {
            ApplicationHelper.currentUser.let { user ->
                databaseReference.root.child(USERS_LOBBIES).child(user.id).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (snap in snapshot.children) {
                            val relation: Relation? = snap.getValue(Relation::class.java)
                            relation?.let {
                                if (user.lobbyId.equals(it.id)) {
                                    val playerData: LobbyPlayerData = LobbyPlayerData()
                                    playerData.online = true
                                    playerData.playerId = UserRepository.currentId
                                    if (user.gameLobby == null) {
                                        user.gameLobby = Lobby()
                                        user.gameLobby?.gameData?.role = FIELD_CREATOR

                                    }
                                    setLobbyRefs(it.id)
                                    myLobbyRef.setValue(playerData)
                                    enemyLobbyRef.setValue(null)
                                }
                                databaseReference.root.child(USERS_LOBBIES).child(user.id).child(it.id).setValue(null)
                                if (shouldFind) {
                                    findLobby(it.id).subscribe { lobby ->
                                        if (lobby.isFastGame) {
                                            databaseReference.child(lobby.id).setValue(null)
                                        }
                                    }
                                }
                            }
                        }
                    }


                })
            }
        }
    }

    fun waitGameMode(mode: String): Single<Boolean> {
        val single: Single<Boolean> = Single.create{ e ->
            enemyLobbyRef.child(FIELD_MODE).addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        val modeStr: String = snapshot.value as String
                        if (mode.equals(modeStr)) {
                            Log.d(TAG_LOG, "modeStr = $modeStr")
                            e.onSuccess(true)
                        }
                    }
                }

            })
        }
        return single.compose(RxUtils.asyncSingle())
    }

    fun changeGameMode(mode: String): Single<Boolean> {
        val single: Single<Boolean> = Single.create{e ->
            myLobbyRef.child(FIELD_MODE).setValue(mode)
            e.onSuccess(true)
        }
        return single.compose(RxUtils.asyncSingle())
    }

    private fun compareLastCards(lobby: Lobby) {
        RepositoryProvider.cardRepository
                .readCard(lastMyChosenCardId!!).subscribe { myLastCard: Card? ->
                    RepositoryProvider.cardRepository
                            .readCard(lastEnemyChoose!!.cardId).subscribe { enemyLastCard: Card? ->
                                var c = 0

                                c += compareCardsParameter({ card -> card.intelligence!! }, myLastCard!!, enemyLastCard!!)
                                c += compareCardsParameter({ card -> card.support!! }, myLastCard!!, enemyLastCard!!)
                                c += compareCardsParameter({ card -> card.prestige!! }, myLastCard!!, enemyLastCard!!)
                                c += compareCardsParameter({ card -> card.hp!! }, myLastCard!!, enemyLastCard!!)
                                c += compareCardsParameter({ card -> card.strength!! }, myLastCard!!, enemyLastCard!!)

                                if (c > 0) {
                                    onWin(lobby)
                                } else if (c < 0) {
                                    onLose()
                                } else {
                                    onDraw()
                                }

                            }
                }
    }

    private fun onDraw() {
        callbacks!!.onGameEnd(GameEndType.DRAW, onYouLoseCard!!)
    }

    fun compareCardsParameter(f: ((card: Card) -> Int), card1: Card, card2: Card): Int {
        return f(card1).compareTo(f(card2))
    }

    private fun onWin(lobby: Lobby) {
        //TODO move card
        moveCardAfterWin(lobby)

        callbacks!!.onGameEnd(GameEndType.YOU_WIN, onEnemyLoseCard!!)

        removeListeners()

    }

    private fun onLose() {
        callbacks!!.onGameEnd(GameEndType.YOU_LOSE, onYouLoseCard!!)

        removeListeners()

    }

    fun watchMyStatus() {
        val myConnect = databaseReference.root.child(UserRepository.TABLE_NAME).child(UserRepository.currentId).child(UserRepository.FIELD_STATUS)
        myConnect.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(OFFLINE_STATUS.equals(snapshot.value)) {
                    Log.d(TAG_LOG,"my disconnect")
                    removeRedundantLobbies(true)
                }
            }

        })
        myConnect.onDisconnect().setValue(OFFLINE_STATUS)
    }

    fun onDisconnectAndLose(shouldFind: Boolean) {
        callbacks!!.onGameEnd(GameEndType.YOU_DISCONNECTED_AND_LOSE, onYouLoseCard!!)
        removeRedundantLobbies(shouldFind)
        removeListeners()
    }

    fun onEnemyDisconnectAndYouWin(lobby: Lobby) {
        moveCardAfterWin(lobby)

        callbacks!!.onGameEnd(GameEndType.ENEMY_DISCONNECTED_AND_YOU_WIN, onEnemyLoseCard!!)
        removeRedundantLobbies(true)
        removeListeners()

    }

    private fun moveCardAfterWin(lobby: Lobby) {

        lobby.gameData?.enemyId?.let {
            RepositoryProvider.cardRepository.addCardAfterGame(onEnemyLoseCard!!.id!!, getPlayerId()!!, it)
                .subscribe()
        }
    }

    fun findOfficialTests(userId: String): Single<List<Lobby>> {
        return findTestsByType(userId, OFFICIAL_TYPE)
    }

    fun findUserTests(userId: String): Single<List<Lobby>> {
        return findTestsByType(userId, USER_TYPE)
    }

    fun findOfficialTestsByQuery(query: String, userId: String): Single<List<Lobby>> {
        return findTestsByTypeByQuery(query, userId, OFFICIAL_TYPE)
    }

    fun findUserTestsByQuery(query: String, userId: String): Single<List<Lobby>> {
        return findTestsByTypeByQuery(query, userId, USER_TYPE)
    }

    fun findTestsByType(userId: String, type: String): Single<List<Lobby>> {
        val query: Query = databaseReference.orderByChild(LOBBY_TYPE).equalTo(type)
        val single: Single<List<Lobby>> = Single.create { e ->
            val cardSingle: Single<List<Card>>
            if(type.equals(OFFICIAL_TYPE)) {
                cardSingle = cardRepository.findOfficialMyCards(userId)
            } else {
                cardSingle = cardRepository.findMyCards(userId)
            }
            cardSingle.subscribe{ myCards ->
                    val myNumber = myCards.size
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val cards: MutableList<Lobby> = ArrayList()
                            for (snapshot in dataSnapshot.children) {
                                val card = snapshot.getValue(Lobby::class.java)
                                card?.let {
                                    if ((ONLINE_STATUS.equals(card.status) && card.type.equals(type)) && !card.isFastGame
                                            && (myNumber >= card.cardNumber || card.id.equals(ApplicationHelper.currentUser.lobbyId))) {
                                        if(card.id.equals(ApplicationHelper.currentUser.lobbyId)) {
                                            it.isMyCreation = true
                                        }
                                        cards.add(it)
                                    }
                                }
                            }
                            e.onSuccess(cards)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
            }
        return single.compose(RxUtils.asyncSingle())
    }

    fun findTestsByTypeByQuery(userQuery: String, userId: String, type: String): Single<List<Lobby>> {
        val queryPart = userQuery.toLowerCase()
        var query = databaseReference.orderByChild(FIELD_LOWER_TITLE).startAt(queryPart).endAt(queryPart + QUERY_END)
        val single: Single<List<Lobby>> = Single.create { e ->
            val cardSingle: Single<List<Card>>
            if (type.equals(OFFICIAL_TYPE)) {
                cardSingle = cardRepository.findOfficialMyCards(userId)
            } else {
                cardSingle = cardRepository.findMyCards(userId)
            }
            cardSingle.subscribe { myCards ->
                val myNumber = myCards.size
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val cards: MutableList<Lobby> = ArrayList()
                        for (snapshot in dataSnapshot.children) {
                            val card = snapshot.getValue(Lobby::class.java)
                            card?.let {
                                if ((ONLINE_STATUS.equals(card.status) && card.type.equals(type)) && !card.isFastGame
                                    && (myNumber >= card.cardNumber || card.id.equals(ApplicationHelper.currentUser.lobbyId))) {
                                        if(it.id.equals(ApplicationHelper.currentUser.lobbyId)) {
                                            it.isMyCreation = true
                                        }
                                    cards.add(it)
                                }
                            }

                        }
                        e.onSuccess(cards)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })

            }
        }
        return single.compose(RxUtils.asyncSingle())
    }


    //


    interface InGameCallbacks {
        fun onGameEnd(type: GameEndType, card: Card)

        fun onEnemyCardChosen(choose: CardChoose)
        fun onEnemyAnswered(correct: Boolean)
    }

    enum class GameEndType {
        YOU_WIN, YOU_LOSE, YOU_DISCONNECTED_AND_LOSE, ENEMY_DISCONNECTED_AND_YOU_WIN,
        DRAW
    }


    companion object {
        val ROUNDS_COUNT = 5

        const val TABLE_LOBBIES = "lobbies"
        const val USERS_LOBBIES = "users_lobbies"


        const val FIELD_RELATION = "relation"

        const val FIELD_INVITED = "invited"
        const val FIELD_CREATOR = "creator"

        const val FIELD_MODE = "mode"
        const val FIELD_ONLINE = "online"

    }
}
