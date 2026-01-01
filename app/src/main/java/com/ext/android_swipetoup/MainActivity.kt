package com.ext.android_swipetoup

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ext.android_swipe_to_up.SwipeToUpView

class MainActivity : AppCompatActivity() {

    private lateinit var swipeView: SwipeToUpView
    private lateinit var positionIndicator: TextView

    // ---------- Model ----------
    data class NewsItem(
        val title: String,
        val description: String,
        val category: String,
        val time: String,
        val source: String,
        val imageRes: Int,
        val backgroundColor: Int
    )

    // ---------- Sample Data ----------
    private val newsList = listOf(
        NewsItem(
            "Breaking: Major Tech Announcement",
            "Tech giants unveil revolutionary AI technology that could change the industry forever.",
            "TECHNOLOGY",
            "2 min ago",
            "TechNews",
            R.drawable.seen,
            0xFFE3F2FD.toInt()
        ),
        NewsItem(
            "Global Markets Rally",
            "Markets worldwide see strong gains amid positive economic signals.",
            "BUSINESS",
            "15 min ago",
            "FinanceDaily",
            R.drawable.seen,
            0xFFFFF3E0.toInt()
        ),
        NewsItem(
            "Sports: Championship Finals",
            "Two top teams battle in a thrilling championship final.",
            "SPORTS",
            "1 hour ago",
            "SportsCentral",
            R.drawable.seen,
            0xFFE8F5E9.toInt()
        ),
        NewsItem(
            "Climate Action Summit",
            "World leaders discuss urgent climate action strategies.",
            "WORLD",
            "2 hours ago",
            "GlobalNews",
            R.drawable.seen,
            0xFFFCE4EC.toInt()
        ),
        NewsItem(
            "Entertainment: Movie Premiere",
            "Blockbuster movie premieres to rave reviews.",
            "ENTERTAINMENT",
            "3 hours ago",
            "ShowBiz",
            R.drawable.seen,
            0xFFF3E5F5.toInt()
        )
    )

    // ---------- Lifecycle ----------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeView = findViewById(R.id.swipeView)
        positionIndicator = findViewById(R.id.positionIndicator)

        setupNewsItems()
        setupSwipeListener()
        updatePositionIndicator()
    }

    // ---------- Inflate Pages ----------
    private fun setupNewsItems() {
        newsList.forEach { news ->

            val view = LayoutInflater.from(this)
                .inflate(R.layout.item_news, swipeView, false)

            view.findViewById<TextView>(R.id.newsTitle).text = news.title
            view.findViewById<TextView>(R.id.newsDescription).text = news.description
            view.findViewById<TextView>(R.id.newsTime).text = news.time
            view.findViewById<TextView>(R.id.newsSource).text = news.source

            view.findViewById<ImageView>(R.id.newsImage).apply {
                setImageResource(news.imageRes)
            }

            view.setBackgroundColor(news.backgroundColor)
            swipeView.addView(view)
        }
    }

    // ---------- Swipe callbacks ----------
    private fun setupSwipeListener() {
        swipeView.setOnSwipeListener(object : SwipeToUpView.OnSwipeListener {

            override fun onSwipeUp(newPosition: Int) {
                updatePositionIndicator()
            }

            override fun onSwipeDown(newPosition: Int) {
                updatePositionIndicator()
            }
        })
    }

    // ---------- UI ----------
    private fun updatePositionIndicator() {
        val current = swipeView.getCurrentPosition() + 1
        val total = newsList.size
        positionIndicator.text = "$current / $total"
    }

    // ---------- Back behaviour ----------
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (swipeView.getCurrentPosition() > 0) {
            // simulate swipe down by user gesture expectation
            swipeView.dispatchTouchEvent(
                android.view.MotionEvent.obtain(
                    0L, 0L,
                    android.view.MotionEvent.ACTION_CANCEL,
                    0f, 0f, 0
                )
            )
        } else {
            super.onBackPressed()
        }
    }
}
