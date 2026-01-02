# **SwipeToUpView Library**

---
SwipeToUpView is a custom Android ViewGroup that allows vertical swipe navigation between stacked pages with a smooth pop-up animation. Perfect for news feeds, cards, tutorials, or any stack-based UI.

---

## ✨ **Features**

- Vertical swipe up/down between pages.

- Smooth “popup” animation for pages.

- Dramatic scaling and alpha effect on the next page when swiping up.

- Fully customizable via XML or programmatically.

- Supports multiple pages stacked like cards. 



  ---

# **Preview**
---
<img src="https://github.com/S13reya/Android_SwipeToUp/blob/stages/app/src/main/assets/demovideo.gif" height="320"/>



## ⚡ **Installation**

**Step 1:** Add JitPack repository to your root build.gradle:

```gradle
maven { url = uri("https://jitpack.io") }
```

**Step 2:** Add the dependency in your app `build.gradle` (example if hosted on JitPack):  

```gradle
dependencies {
	        implementation 'com.github.Excelsior-Technologies-Community:ReadMoreTextView:1.0.0'

}
```
## ⚡ **attrs file**

```

<?xml version="1.0" encoding="utf-8"?>
<resources>

    <declare-styleable name="SwipeToUpView">

        <!-- Minimum swipe distance in pixels to trigger a fling -->
        <attr name="swipeThreshold" format="dimension" />

        <!-- Duration of the swipe animation in milliseconds -->
        <attr name="animationDuration" format="integer" />

        <!-- Enable or disable swipe up gesture -->
        <attr name="enableSwipeUp" format="boolean" />

        <!-- Enable or disable swipe down gesture -->
        <attr name="enableSwipeDown" format="boolean" />

        <!-- Sensitivity multiplier for swipe gestures -->
        <attr name="swipeSensitivity" format="float" />

        <!-- Enable over-scrolling at the first and last items -->
        <attr name="enableOverScroll" format="boolean" />

        <!-- Resistance factor for over-scroll -->
        <attr name="overScrollResistance" format="float" />

        <!-- Threshold percentage (0-1) of screen height to auto-complete swipe -->
        <attr name="autoSwipeThreshold" format="float" />

        <!-- Enable fade effect during swipe -->
        <attr name="enableFadeEffect" format="boolean" />

        <!-- Starting alpha value for current item -->
        <attr name="fadeStartAlpha" format="float" />

        <!-- Ending alpha value when swiping away -->
        <attr name="fadeEndAlpha" format="float" />


        <!-- 🔹 ADDED (MISSING – REQUIRED FOR STACK EFFECT) -->

        <!-- Vertical offset between stacked cards -->
        <attr name="stackOffset" format="dimension" />

        <!-- Minimum scale for stacked cards -->
        <attr name="minScale" format="float" />

        <!-- Minimum alpha for stacked cards -->
        <attr name="minAlpha" format="float" />

    </declare-styleable>

</resources>



```

## ⚡ **Usage**

1. Add in XML

```

<com.ext.android_swipe_to_up.SwipeToUpView
        android:id="@+id/swipeView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:swipeThreshold="150dp"
        app:animationDuration="900"
        app:enableSwipeUp="true"
        app:enableSwipeDown="true"
        app:swipeSensitivity="1.2"
        app:enableOverScroll="true"
        app:overScrollResistance="3.5"
        app:autoSwipeThreshold="0.35"
        app:enableFadeEffect="true"
        app:fadeStartAlpha="2.0"
        app:fadeEndAlpha="1.0"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

```

## **2. Setup in Activity**

## **1. Create a data model (optional)**
You can define a model for your pages like a news feed:
```
data class NewsItem(
    val title: String,
    val description: String,
    val category: String,
    val time: String,
    val source: String,
    val imageRes: Int,
    val backgroundColor: Int
)

```

## **2. Add pages programmatically**
```
val newsList = listOf(
    NewsItem("Breaking News", "Description here", "NEWS", "2 min ago", "Source", R.drawable.seen, 0xFFE3F2FD.toInt()),
    // Add more items
)

newsList.forEach { news ->
    val view = LayoutInflater.from(this).inflate(R.layout.item_news, swipeView, false)
    view.findViewById<TextView>(R.id.newsTitle).text = news.title
    view.findViewById<TextView>(R.id.newsDescription).text = news.description
    view.findViewById<TextView>(R.id.newsTime).text = news.time
    view.findViewById<TextView>(R.id.newsSource).text = news.source
    view.findViewById<ImageView>(R.id.newsImage).setImageResource(news.imageRes)
    view.setBackgroundColor(news.backgroundColor)
    swipeView.addView(view)
}


```
## **3. Handle swipe events**
```
swipeView.setOnSwipeListener(object : SwipeToUpView.OnSwipeListener {
    override fun onSwipeUp(newPosition: Int) {
        updatePositionIndicator()
    }

    override fun onSwipeDown(newPosition: Int) {
        updatePositionIndicator()
    }
})



```

## ** 4. Update a position indicator (optional)**
```
private fun updatePositionIndicator() {
    val current = swipeView.getCurrentPosition() + 1
    val total = swipeView.childCount
    positionIndicator.text = "$current / $total"
}



```




## **📄 License**

**MIT License**  
```
Copyright (c) 2025 Excelsior Technologies

Permission is hereby granted, free of charge, to any person obtaining a copy  
of this software and associated documentation files (the "Software"), to deal  
in the Software without restriction, including without limitation the rights  
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
copies of the Software, and to permit persons to whom the Software is  
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all  
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED **"AS IS"**, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```



  
