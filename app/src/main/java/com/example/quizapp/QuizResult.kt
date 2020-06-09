package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ncorti.slidetoact.SlideToActView
import kotlinx.android.synthetic.main.activity_quiz_result.*

class QuizResult : AppCompatActivity() {
    private lateinit var slide: SlideToActView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)


        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val mUserName: String? = intent.getStringExtra(Constants.userName)
        val mTotalQuestion: Int = intent.getIntExtra(Constants.totalQuestion, 0)
        val mWrongAnswer: Int = intent.getIntExtra(Constants.WrongAnswer, 0)
        val mCorrectAnswer: Int = mTotalQuestion - mWrongAnswer
        val mTotalTime: Long = intent.getLongExtra(Constants.TotalTime, 0)
        user_name.text = mUserName
        user_score.text = "Your score is $mCorrectAnswer out of $mTotalQuestion"
        time_taken.text = "Total time taken : $mTotalTime seconds"

        fun startActivity() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        slide_finish.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                startActivity()

            }
        }

    }


}