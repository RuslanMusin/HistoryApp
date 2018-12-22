package com.summer.itis.summerproject.ui.game.play

//class DebugPlayGameActivity : MvpAppCompatActivity(), PlayGameView {
//    override fun setCardsList(cards: ArrayList<Card>) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun setCardChooseEnabled(enabled: Boolean) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    @InjectPresenter
//    lateinit var presenter: PlayGamePresenter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_debug_play_game)
//
//        btn_choose_random_card.setOnClickListener {
//            RepositoryProvider.cardRepository.findMyCards(UserRepository.currentId).subscribe { list ->
//
//                presenter.chooseCard(list.getRandom()!!)
//            }
//        }
//
//        btn_answer_true.setOnClickListener {
//            presenter.answer(true)
//        }
//
//        btn_answer_false.setOnClickListener {
//            presenter.answer(false)
//        }
//    }
//
//    override fun showEnemyCardChoose(card: Card) {
//        tv_enemy_chooses.text = tv_enemy_chooses.text.toString() + "\r\n" + card.id
//    }
//
//    override fun showQuestionForYou(question: Question) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun showYouCardChoose(choose: Card) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun showEnemyAnswer(correct: Boolean) {
//        tv_enenmy_answers.text = tv_enenmy_answers.text.toString() + "\r\n" + correct
//    }
//
//    companion object {
//        fun start(context: Context) {
//            val intent = Intent(context, DebugPlayGameActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            context.startActivity(intent)
//        }
//    }
//}
