package com.example.quiplash.game

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.quiplash.LandingActivity
import com.example.quiplash.R
import com.example.quiplash.Sounds
import com.example.quiplash.database.Callback
import com.example.quiplash.database.DBMethods
import com.example.quiplash.database.DBMethods.Companion.deleteGame
import com.example.quiplash.database.DBMethods.Companion.getCurrentGame
import com.example.quiplash.database.DBMethods.Companion.getUserWithID
import com.example.quiplash.game.GameManager.Companion.game
import com.example.quiplash.user.PlayersListAdapter
import com.example.quiplash.user.UserQP
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/**
 * While the Game hasn't enough Players and isn't started by the host, the players remain in the 'Waiting'-View.
 * The Host can End Game, Invite Players and Start the Game in this View.
 * Players can Join the Game and leave it in this View.
 * **/
class WaitingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    //Firestore
    lateinit var db: CollectionReference
    private val dbGamesPath = DBMethods.gamesPath

    private lateinit var selectedQuestions: ArrayList<Question>
    private lateinit var awaitGamestart: ListenerRegistration
    private var gameQuestions = arrayListOf<Question>()

    //Party Mode
    private var drinkChallenges = arrayListOf<String>()
    private var beerBool: Boolean = false
    private var wineBool: Boolean = false
    private var cocktailBool: Boolean = false
    private var shotBool: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance().collection(dbGamesPath)
        auth = FirebaseAuth.getInstance()



        setContentView(R.layout.activity_waiting)

        //add Questions to Game
        selectedQuestions = arrayListOf()
        getQuestionsForGame(game.rounds, game.playerNumber, game.category)


        //If Game is started by the Host the Game info will be updated. This Listener switches
        // the View either to the View for Players who have to answer the Question or to vote
        // for an answer. But if the Host ends the Game the Players switch bck to the Landing-View.
        awaitGamestart = db.document(game.gameID).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("ERROR", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                game = snapshot.toObject(Game::class.java)!!
                if (game.playrounds.size > 0) {
                    gotoGameLaunch()
                }

            } else {
                gotoGameLanding()
            }
        }


        //Set View-Elements
        val btnInvitePlayers = findViewById<Button>(R.id.invite_players_btn)
        val btnStartGame = findViewById<Button>(R.id.start_game_btn)
        val btnEndGame = findViewById<Button>(R.id.end_game)
        val btnLeaveGame = findViewById<Button>(R.id.leave_game)
        val btnJoinGame = findViewById<Button>(R.id.join_game_btn)
        val playersListView = findViewById<ListView>(R.id.players_list)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)

        val checkBeer = findViewById<CheckBox>(R.id.CheckBoxBeer)
        val checkWine = findViewById<CheckBox>(R.id.CheckBoxWine)
        val checkCocktails = findViewById<CheckBox>(R.id.CheckBoxCocktails)
        val checkShot = findViewById<CheckBox>(R.id.CheckBoxShots)
        val layoutDrinksCheck = findViewById<LinearLayout>(R.id.linearLayoutDrinks)
        val labelDrinks = findViewById<TextView>(R.id.textViewDrinksLabel)

        getUsersList(playersListView, game.gameID)

        if (game.partyMode && auth.currentUser?.uid.toString() == game.hostID) {
            layoutDrinksCheck.visibility = View.VISIBLE
            labelDrinks.visibility = View.VISIBLE

            checkBeer.setOnClickListener {
                beerBool = checkBeer.isChecked
            }
            checkWine.setOnClickListener {
                wineBool = checkWine.isChecked
            }
            checkCocktails.setOnClickListener {
                cocktailBool = checkCocktails.isChecked
            }
            checkShot.setOnClickListener {
                shotBool = checkShot.isChecked
            }
        } else {
            layoutDrinksCheck.visibility = View.GONE
            labelDrinks.visibility = View.GONE
        }

        /**
         * If Host presses 'Start Game', Playrounds will be created. If the Game is in Party-Mode,
         * A checup runs wether a drink is choosen. If the host has choosen (a) drink(s) they will
         * be saved. Else a corresponding Feedback will be displayed.
         **/
        btnStartGame.setOnClickListener {
            if (game.partyMode && !beerBool && !wineBool && !cocktailBool && !shotBool) {
                Toast.makeText(this, "Please show us your drinks for the game", Toast.LENGTH_LONG)
                    .show()
            } else {
                if (game.partyMode) {
                    addDrinks()
                }
                awaitGamestart.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
                Sounds.playClickSound(this)
                savePlayrounds()
            }
        }

        //The Game will be deleted
        btnEndGame.setOnClickListener {
            Sounds.playClickSound(this)

            val callbackSuccess = object : Callback<Boolean> {
                override fun onTaskComplete(result: Boolean) {
                    Log.d("GAMEDELETE", "deleted? = $result")
                    val intent = Intent(this@WaitingActivity, LandingActivity::class.java)
                    startActivity(intent)
                }
            }
            deleteGame(game.gameID, callbackSuccess)
        }

        //Player will be removed from Game
        btnLeaveGame.setOnClickListener {
            removeUserFromGame()
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
        }

        //Player will be added to Game
        btnJoinGame.setOnClickListener {
            addUserToGame()
            btnLeaveGame.visibility = View.VISIBLE
            btnJoinGame.visibility = View.GONE
            getUsersList(playersListView, game.gameID)
            refreshLayout.isRefreshing = false
        }

        refreshLayout.setOnRefreshListener {
            Sounds.playRefreshSound(this)
            getUsersList(playersListView, game.gameID)
            refreshLayout.isRefreshing = false
        }

        btnInvitePlayers.setOnClickListener {
            Sounds.playClickSound(this)
            val callbackUser = object : Callback<UserQP> {
                override fun onTaskComplete(result: UserQP) {
                    val user = result
                    if (user.guest!!) {
                        val dialogFragment = InvitePlayer()
                        val ft = supportFragmentManager.beginTransaction()
                        val prev = supportFragmentManager.findFragmentByTag("invite")
                        if (prev != null) {
                            ft.remove(prev)
                        }
                        ft.addToBackStack(null)
                        dialogFragment.show(ft, "invite")
                    } else {
                        seeFriendsList()
                    }
                }
            }
            getUserWithID(callbackUser, auth.currentUser?.uid.toString())
        }
    }

    fun seeFriendsList() {
        val intent = Intent(this, InviteFriendsToGameActivity::class.java)
        intent.putExtra("gameID", game.gameID)
        startActivity(intent)
    }

    private fun addUserToGame() {
        val selectedItem = game
        selectedItem.users = selectedItem.users + auth.currentUser?.uid.toString()
        DBMethods.updateGameUsers(selectedItem)
    }

    /**Player will be removed from UI and from Game in Database**/
    private fun removeUserFromGame() {
        val selectedItem = game
        val filteredUsers =
            selectedItem.users.filterIndexed { _, s -> (s != auth.currentUser?.uid.toString()) }
        selectedItem.users = filteredUsers
        DBMethods.updateGameUsers(selectedItem)
    }

    /**According to Players'-State (Host(Minimum Number of Players is reached), Host(Minimum Number of Players is not reached), Player(joined), Player(not joined))**/
    fun setBtnVisibility(currentGame: Game, currentPlayerNumber: Int, playerNumber: Int) {
        val btnInvitePlayers = findViewById<Button>(R.id.invite_players_btn)
        val btnStartGame = findViewById<Button>(R.id.start_game_btn)
        val btnEndGame = findViewById<Button>(R.id.end_game)
        val btnLeaveGame = findViewById<Button>(R.id.leave_game)
        val btnJoinGame = findViewById<Button>(R.id.join_game_btn)

        when {
            currentGame.users[0] == auth.currentUser?.uid.toString() -> {
                btnStartGame.visibility = View.VISIBLE
                btnEndGame.visibility = View.VISIBLE
                btnLeaveGame.visibility = View.GONE
                btnJoinGame.visibility = View.GONE
                btnInvitePlayers.visibility = View.VISIBLE
            }
            currentGame.users.contains(auth.currentUser?.uid.toString()) -> {
                btnStartGame.visibility = View.GONE
                btnEndGame.visibility = View.GONE
                btnLeaveGame.visibility = View.VISIBLE
                btnJoinGame.visibility = View.GONE
                btnInvitePlayers.visibility = View.VISIBLE
            }
            else -> {
                btnStartGame.visibility = View.GONE
                btnEndGame.visibility = View.GONE
                btnLeaveGame.visibility = View.GONE
                btnJoinGame.visibility = View.VISIBLE
                btnInvitePlayers.visibility = View.GONE
            }
        }

        if (currentPlayerNumber == playerNumber) {
            btnStartGame.isClickable = true
            btnStartGame.setBackgroundResource(R.color.colorText)
            btnStartGame.setTextColor(Color.WHITE)
            btnInvitePlayers.visibility = View.GONE
        } else {
            btnStartGame.isClickable = false
            btnStartGame.setBackgroundResource(R.drawable.char_button_frame_disabled)
            btnStartGame.setTextColor(Color.BLACK)
        }
    }

    private fun getUsersList(
        playersListView: ListView,
        gameID: String
    ) {
        val playersNames = mutableListOf<UserQP>()
        var userIDList: MutableList<String>
        val callback = object : Callback<Game> {
            override fun onTaskComplete(result: Game) {
                game = result
                val playerNumber = game.playerNumber
                val currentPlayerNumber = game.users.size
                val players = game.users
                userIDList = players.toMutableList()
                userIDList.forEach {
                    val callbackUser = object : Callback<UserQP> {
                        override fun onTaskComplete(result: UserQP) {
                            val user = result
                            playersNames.add(user)
                            val adapter = PlayersListAdapter(
                                applicationContext,
                                R.layout.host_waiting_list_item,
                                playersNames
                            )
                            playersListView.adapter = adapter
                            setBtnVisibility(game, currentPlayerNumber, playerNumber)
                        }
                    }
                    getUserWithID(callbackUser, it)
                }
            }
        }
        getCurrentGame(callback, gameID)
    }

    /**
     * If The Host has Start the Game every Player will be allocated to corresponding View (Voter, Opponent)
     * **/
    private fun gotoGameLaunch() {
        awaitGamestart.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
        Sounds.playStartSound(this)
        val intent = Intent(this, GameLaunchingActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * If The Host has End the Game every Player will be send Back to landing-view
     * **/
    private fun gotoGameLanding() {
        awaitGamestart.remove() //IMPORTANT to remove the DB-Listener!!! Else it keeps on listening and run function if if-clause is correct.
        val intent = Intent(this, LandingActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * All Rounds will be created here before game actually starts.
     * At this point users-count is definitely sure and known.
     * By the knowledge of participants-count and rounds, every round can be created.
     * In a game round, each player competes once against each player in a duel to answer a question.
     * In addition, the rounds created are multiplied by the required number of rounds.
     * For example:
     * Players = 3
     * Rounds = 3
     * [Round1.1: competeters = user1 & user2, voters = user3;
     * Round1.2: competeters = user2 & user3; voters = user1;
     * Round1.3: competeters = user1 & user3; voters = user2]
     * --> This is one Round, so that it stays fair ;)
     * --> total rounds = 9
     * **/
    private fun getallRounds(): HashMap<String, Round> {
        var jump = 1
        var roundCount = 0
        val allRounds: HashMap<String, Round> = hashMapOf()

        var testroundcount = 0

            while (jump < game.users.size) {

                while (roundCount < game.users.size - jump) {

                    val voters = mutableListOf<String>()
                    for (user in game.users) {

                        if (game.users.indexOf(user) != roundCount && game.users.indexOf(user) != (roundCount + jump)) {
                            voters.add(user)
                        }
                    }
                    for (x in 0 until game.rounds) {
                        if (testroundcount < game.rounds*game.playerNumber){
                            val round = Round(
                                voters,
                                linkedMapOf(
                                    GameManager.opp0 to Opponent(game.users[roundCount]),
                                    GameManager.opp1 to Opponent(game.users[roundCount + jump])
                                ),
                                gameQuestions[testroundcount].question.toString()
                            )
                            val roundindex =
                                (testroundcount.rem(game.users.size)) * game.rounds + (testroundcount / game.users.size)
                            allRounds["round$roundindex"] = round
                            testroundcount += 1
                        }
                    }
                    roundCount += 1
                }
                roundCount = 0
                jump += 1
            }

        
        return allRounds

    }

    private fun addDrinks() {
        if (beerBool) {
            drinkChallenges.add("Have a sip of Beer")
            drinkChallenges.add("Have 3 sips of Beer")
            drinkChallenges.add("Ex your Beer and open a new one")
        }
        if (wineBool) {
            drinkChallenges.add("Have a sip of wine")
            drinkChallenges.add("Have 3 sips of wine")
            drinkChallenges.add("Ex your glas of wine and refill it")
        }
        if (cocktailBool) {
            drinkChallenges.add("Have a sip of your cocktail")
            drinkChallenges.add("Have 3 sips of your cocktail")
            drinkChallenges.add("Ex half of your glas and if it's empty refill it!")
            drinkChallenges.add("Let your cocktail be a bit stronger :)")
        }
        if (shotBool) {
            drinkChallenges.add("Have a Shot")
            drinkChallenges.add("Have 2 Shots")
            drinkChallenges.add("Have a shot of the ugliest Water you have")
            drinkChallenges.add("Choose a partner and have a shot with him together")
        }

        db.document(game.gameID)
            .update(DBMethods.drinksPath, drinkChallenges)
            .addOnSuccessListener {
                Sounds.playStartSound(this)
            }
            .addOnFailureListener { e -> Log.w("Error", "Error writing document", e) }
    }

    /**If all rounds are generated they will be saved into database. Afterwards the Listener
     * in this activity receives this update and sends every Player to the next view**/
    private fun savePlayrounds() {
        db.document(game.gameID)
            .update(DBMethods.playroundsPath, getallRounds())
            .addOnSuccessListener {
                Sounds.playStartSound(this)

                val intent = Intent(this, GameLaunchingActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e -> Log.w("Error", "Error writing document", e) }
    }

    private fun getQuestionsForGame(rounds: Int, player_count: Int, selected_category: String) {
        val countQuestions = rounds * player_count
        val callback = object : Callback<java.util.ArrayList<Question>> {
            override fun onTaskComplete(result: java.util.ArrayList<Question>) {
                var counter = 0
                while (counter < countQuestions+1) {
                    val position = (0 until result.size).random()
                    if (result[position].type.toString() == selected_category || selected_category == "All") {
                        selectedQuestions.add(result[position])
                        val currentQuestion = result[position]
                        result.remove(currentQuestion)
                        counter += 1
                    }
                }
                gameQuestions = selectedQuestions
            }
        }
        DBMethods.getQuestions(callback)
    }
}