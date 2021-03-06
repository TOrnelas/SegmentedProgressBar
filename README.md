# SegmentedProgressBar

An Instagram-like stories segmented progress bar

[![](https://jitpack.io/v/TOrnelas/SegmentedProgressBar.svg)](https://jitpack.io/#TOrnelas/SegmentedProgressBar)

![screen-20210306-002812_2 (3)](https://user-images.githubusercontent.com/17816841/110188846-0a784800-7e15-11eb-85c2-897e572173c2.gif)


## Setup

Add Jitpack repository to your project level `build.gradle`

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Add SegmentedProgressBar to your app's `build.gradle` dependencies

```
dependencies {
    implementation 'com.github.TOrnelas:SegmentedProgressBar:0.0.1'
}
```

## Usage

### Add it to your layout xml file

```
<pt.tornelas.segmentedprogressbar.SegmentedProgressBar
        android:id="@+id/spb"
        android:layout_width="match_parent"
        android:layout_height="5dp"
        android:layout_margin="8dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:segmentBackgroundColor="@color/colorAccent"
        app:segmentSelectedBackgroundColor="@color/colorPrimary"
        app:segmentStrokeColor="@android:color/black"
        app:segmentSelectedStrokeColor="@android:color/black"
        app:segmentStrokeWidth="1dp"
        app:segmentCornerRadius="2dp"
        app:segmentMargins="@dimen/default_segment_margin"
        app:totalSegments="10"
        app:timePerSegment="2000"/>
```

Alternatively, you can set all properties in your Fragment/Activity:

```
val spb = findViewById<SegmentedProgressBar>(R.id.spb)
spb.segmentCount = 10
...
```

### Initialize it and start it

```
val spb = findViewById<SegmentedProgressBar>(R.id.spb)
spb.start()
```

## Available methods

```
// Starts/resumes progress animation
spb.start()

// Pauses progress animation
spb.pause()

// Restarts progress animation
spb.reset()

// Completes animation of current segment, starts animating following segment
spb.next()

// Resets animation of current segment, starts animating previous segment
spb.previous()

// Resets animation of current segment
spb.restartSegment()

// Skips X segments (-X option available)
spb.skip(X)

// Restarts animation for segment at position X
spb.setPosition(X)
```

## Set a ViewPager

You can sync a ViewPager with a SegmentedProgressBar by doing

```
spb.viewPager = viewPager
```

This will
- Update the SegmentedProgressBar segments automatically when a swipe action is done on a ViewPager
- Pause/Resume progress animation when user touches down/up on the view pager (Like you do to pause an Instagram story)

## Set a Listener

```
spb.listener = object : SegmentedProgressBarListener {
    override fun onPage(oldPageIndex: Int, newPageIndex: Int) {
        // New page started animating
    }

    override fun onFinished() {
        // All segments animated and finished animation
    }
}
```

