package com.slecornu.pipdemo

import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class VideoActivity(): AppCompatActivity() {
    var videoView: VideoView? = null
    var videoUrlList = listOf(VIDEO_URL1, VIDEO_URL2)
    var videoIndex = 0
    var currentVideoPath: Uri? = null
    var lastPosition = 0
    var mediaControls: MediaController? = null
    var pipStatus: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        onBackPressedDispatcher.addCallback(
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    enterPipMode()
                }
            }
        )
        
        videoView = findViewById<VideoView>(R.id.videoView)?.apply {
            if (mediaControls == null) {
                mediaControls = MediaController(this@VideoActivity).apply {
                    setAnchorView(this)
                }
            }

            setOnErrorListener { _, what, extra ->
                Log.d("CodeLab", "What $what extra $extra")
                true
            }

            setMediaController(mediaControls)
            setVideoURI(videoUrlList[videoIndex].toUri())

            requestFocus()
            start()
        }

        savedInstanceState?.let { restoreVideoState(it) }
    }

    override fun onPause() {
        super.onPause()
        if (!isInPictureInPictureMode) {
            lastPosition = videoView?.currentPosition ?: 0
            videoView?.pause()
        }
        else {
            Log.d("CodeLab", "Pip Mode onPause method")
        }
    }

    override fun onResume() {
        super.onResume()
        if (isInPictureInPictureMode) {
            Log.d("CodeLab", "Pip Mode onResume method")
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(VIDEO_PATH, currentVideoPath.toString() )
        outState.putInt(CURRENT_POSITION, lastPosition)
        outState.putBoolean(PIP_STATUS, isInPictureInPictureMode)
        videoView?.pause()
    }

    private fun restoreVideoState(savedInstanceState: Bundle){
        lastPosition = savedInstanceState.getInt(CURRENT_POSITION)
        currentVideoPath = savedInstanceState.getString(VIDEO_PATH)?.toUri()
        pipStatus = savedInstanceState.getBoolean(PIP_STATUS)

        videoView?.apply {
            setVideoURI(currentVideoPath)
            seekTo(lastPosition)
            requestFocus()
            start()
        }

        if (pipStatus) {
            enterPipMode()
        }
    }

    override fun onUserLeaveHint() {
        enterPipMode()
    }

    private fun enterPipMode(){
        videoView?.let {
            it.setMediaController(null)
            enterPictureInPictureMode(
                PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(it.width, it.height))
                    .build()
            )
        }
    }


    override fun onPictureInPictureModeChanged (isInPictureInPictureMode: Boolean, newconfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        val actionBar = supportActionBar
        if (isInPictureInPictureMode) {
            actionBar?.hide()
        } else {
            actionBar?.show()
            videoView!!.setMediaController(mediaControls);
        }
    }
    
    companion object {
        fun createIntent(context: Context) = Intent(context, VideoActivity::class.java)

        const val VIDEO_PATH = "videopath"
        const val PIP_STATUS = "pipstatus"
        const val CURRENT_POSITION = "Currentposition"
        const val VIDEO_URL1 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        const val VIDEO_URL2 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    }
}
