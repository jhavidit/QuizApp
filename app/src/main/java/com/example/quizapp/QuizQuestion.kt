package com.example.quizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_quiz_question.*


class QuizQuestion : AppCompatActivity(), View.OnClickListener {

    private var wrongAnswer: Int = 0
    private var mCurrentPosition: Int = 1
    private var mQuestionsList: ArrayList<Questions>? = null
    private var mSelectedOptionPosition: Int = 0
    private var mUserName: String? = null
    private var mNextQuestion: Int = 0
    private var flag: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private var timeDuration: Long = 10000
    private var correctSound: MediaPlayer? = null
    private var incorrectSound: MediaPlayer? = null
    private var clockTicking: MediaPlayer? = null
    private var totalTimeTaken: Long = 0
    private var timeTaken: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)
        timer.text = "${(timeDuration / 1000).toString()}"
        correctSound = MediaPlayer.create(this, R.raw.correct_answer)
        incorrectSound = MediaPlayer.create(this, R.raw.wrong_sound)
        clockTicking = MediaPlayer.create(this, R.raw.clock_tick)
        mQuestionsList = Constants.getQuestions()

        setQuestion()

        option_one.setOnClickListener(this)
        option_two.setOnClickListener(this)
        option_three.setOnClickListener(this)
        option_four.setOnClickListener(this)
        btn_submit.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.option_one -> {
                if (!flag)
                    selectedOptionView(option_one, 1)
                else
                    Toast.makeText(this, "Answer already submitted", Toast.LENGTH_SHORT).show()
            }

            R.id.option_two -> {
                if (!flag)
                    selectedOptionView(option_two, 2)
                else
                    Toast.makeText(this, "Answer already submitted", Toast.LENGTH_SHORT).show()

            }

            R.id.option_three -> {
                if (!flag)
                    selectedOptionView(option_three, 3)
                else
                    Toast.makeText(this, "Answer already submitted", Toast.LENGTH_SHORT).show()

            }

            R.id.option_four -> {
                if (!flag)
                    selectedOptionView(option_four, 4)
                else
                    Toast.makeText(this, "Answer already submitted", Toast.LENGTH_SHORT).show()

            }
            R.id.btn_submit -> {
                if (mSelectedOptionPosition != 0) {
                    countDownTimer!!.cancel()
                    clockTicking!!.pause()
                }
                if (mSelectedOptionPosition == 0)
                    Toast.makeText(this, "Select Option First", Toast.LENGTH_SHORT).show()
                else {

                    val question: Questions? = mQuestionsList?.get(mCurrentPosition - 1)

                    if (question!!.correctAnswer != mSelectedOptionPosition)
                        incorrectSound!!.start()
                    else
                        correctSound!!.start()

                    if (question!!.correctAnswer != mSelectedOptionPosition) {
                        wrongAnswer++
                        answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                    }
                    answerView(question!!.correctAnswer, R.drawable.correct_option_border_bg)
                    mNextQuestion = 1
                    flag = true
                    btn_submit.visibility = View.GONE

                    timeTaken = timer.text.toString().toLong()
                    timeTaken = (timeDuration / 1000) - timeTaken
                    totalTimeTaken += timeTaken
                    nextQuestion(mCurrentPosition)

                    mSelectedOptionPosition = 0
                }
            }

        }
    }

    private fun nextQuestion(currentPosition: Int) {
        val question: Questions? = mQuestionsList?.get(mCurrentPosition - 1)
        if (mCurrentPosition < mQuestionsList!!.size) {
            mCurrentPosition++
            Handler().postDelayed({
                setQuestion()
            }, 1500)

        } else {
            mUserName = intent.getStringExtra(Constants.userName)

            val intent = Intent(this, QuizResult::class.java)
            intent.putExtra(Constants.userName, mUserName)
            intent.putExtra(Constants.WrongAnswer, wrongAnswer)
            intent.putExtra(Constants.totalQuestion, mQuestionsList!!.size)
            intent.putExtra(Constants.TotalTime, totalTimeTaken)
            startActivity(intent)
            finish()

        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeDuration, 1000) {
            override fun onFinish() {
                clockTicking!!.pause()
                Toast.makeText(this@QuizQuestion, "Times Up", Toast.LENGTH_LONG).show()

                incorrectSound!!.start()
                val question: Questions? = mQuestionsList?.get(mCurrentPosition - 1)
                answerView(question!!.correctAnswer, R.drawable.correct_option_border_bg)
                mNextQuestion = 1
                flag = true
                wrongAnswer++
                totalTimeTaken += timeDuration / 1000
                nextQuestion(mCurrentPosition)

                /*  if (mCurrentPosition == mQuestionsList!!.size)
                      btn_submit.text = "FINISH"
                  else
                      btn_submit.text = "GO TO NEXT QUESTION"*/

                mSelectedOptionPosition = 0

            }

            override fun onTick(millisUntilFinished: Long) {
                timer.text = (millisUntilFinished / 1000).toString()

            }
        }.start()

    }

    private fun answerView(correctOption: Int, drawableView: Int) {
        when (correctOption) {
            1 -> {
                option_one.background = ContextCompat.getDrawable(this, drawableView)
            }
            2 -> {
                option_two.background = ContextCompat.getDrawable(this, drawableView)
            }
            3 -> {
                option_three.background = ContextCompat.getDrawable(this, drawableView)
            }
            4 -> {
                option_four.background = ContextCompat.getDrawable(this, drawableView)
            }
        }
    }


    private fun setQuestion() {
        btn_submit.visibility = View.VISIBLE
        clockTicking!!.start()
        val question = mQuestionsList!![mCurrentPosition - 1]
        flag = false
        defaultOptionsView()

        progressBar.progress = mCurrentPosition
        tv_progress.text = "$mCurrentPosition" + "/" + progressBar.max

        tv_question.text = question.question
        ques_image.setImageResource(question.image)
        option_one.text = question.optionOne
        option_two.text = question.optionTwo
        option_three.text = question.optionThree
        option_four.text = question.optionFour
        startTimer()
    }


    private fun defaultOptionsView() {

        btn_submit.text = "SUBMIT"

        val options = ArrayList<TextView>()
        options.add(0, option_one)
        options.add(1, option_two)
        options.add(2, option_three)
        options.add(3, option_four)

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this@QuizQuestion,
                R.drawable.default_option_border_bg
            )
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {

        defaultOptionsView()

        mSelectedOptionPosition = selectedOptionNum

        tv.setTextColor(
            Color.parseColor("#363A43")
        )
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(
            this@QuizQuestion,
            R.drawable.selected_option_border_bg
        )
    }

}