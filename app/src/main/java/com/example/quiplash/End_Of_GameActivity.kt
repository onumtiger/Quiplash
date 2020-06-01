import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.example.quiplash.R

class End_Of_GameActivity : AppCompatActivity() {

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_of_game)

        val btnBack = findViewById<AppCompatImageButton>(R.id.scoreboard_go_back_arrow)

        btnBack.setOnClickListener() {
            super.onBackPressed();
        }

        val scoreboardList = findViewById<ListView>(R.id.players_list)
        val scoreboardArray = arrayOfNulls<String>(5)

        for (i in 0 until scoreboardArray.size) {
            scoreboardArray[i] = "Player $i"
        }


        val adapter = ArrayAdapter<String>(
            this, R.layout.scoreboard_list_item,
            R.id.active_player, scoreboardArray
        )

        scoreboardList.adapter = adapter
    }
}