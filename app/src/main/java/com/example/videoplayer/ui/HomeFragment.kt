package com.example.videoplayer.ui

import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.example.videoplayer.R
import com.example.videoplayer.databinding.FragmentHomeBinding
import com.example.videoplayer.extensions.currentSeconds
import com.example.videoplayer.extensions.seconds
import com.example.videoplayer.utils.BaseFragment

@RequiresApi(Build.VERSION_CODES.R)
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), SeekBar.OnSeekBarChangeListener, SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    private val mediaPlayer = MediaPlayer()
    private lateinit var runnable: Runnable
    private var handler = Handler(Looper.getMainLooper())

    private var seekBarProgress :Int? = null

    private val viewModel: VideoViewModel by viewModels()

    companion object {
        const val SECOND = 1000
    }

    override fun startCreating(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {
        init()

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("message", "landscape")
            changeVideoScreenSize()


            seekBarProgress = savedInstanceState?.getInt("SEEKBAR_PROGRESS")

            Log.d("seekbar222222", seekBarProgress.toString())
        }
    }

    private fun init(){
        mediaPlayer.setOnPreparedListener(this)
        binding.videoView.holder.addCallback(this)
        binding.seekBar.setOnSeekBarChangeListener(this)
        binding.playButton.isEnabled = false

        binding.playButton.setOnClickListener {

            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.playButton.setImageResource(android.R.drawable.ic_media_play)
            } else {
                mediaPlayer.start()
                binding.playButton.setImageResource(android.R.drawable.ic_media_pause)
            }
        }
    }

    private fun changeVideoScreenSize(){
        val metrics = DisplayMetrics()
        context?.display?.getRealMetrics(metrics)
        val layoutParams: ViewGroup.LayoutParams = binding.videoView.layoutParams
        layoutParams.width = metrics.widthPixels
        layoutParams.height = 650
        binding.videoView.layoutParams = layoutParams

        binding.playButton.visibility = View.GONE
    }

    private fun timeInString(seconds: Int): String {
        return String.format(
            "%02d:%02d",
            (seconds / 3600 * 60 + ((seconds % 3600) / 60)),
            (seconds % 60)
        )
    }

    private fun initializeSeekBar(progress: Int) {
        with(binding){
            seekBar.max = mediaPlayer.seconds()
            textProgress.text = progress.toString()
            textTotalTime.text = timeInString(mediaPlayer.seconds())
            progressBar.visibility = View.GONE
            playButton.isEnabled = true
        }
    }

    private fun updateSeekBar() {
        runnable = Runnable {
            binding.textProgress.text = timeInString(mediaPlayer.currentSeconds())
            binding.seekBar.progress = mediaPlayer.currentSeconds()
            handler.postDelayed(runnable, SECOND.toLong())

            // set seekBar progress
            seekBarProgress = binding.seekBar.progress
            Log.d("media2222", "$seekBarProgress")
        }
        handler.postDelayed(runnable, SECOND.toLong())
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser){
            mediaPlayer.seekTo(progress * SECOND)
        }

        Log.d("media", "$progress")
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mediaPlayer.apply {
            setDataSource(requireContext(),
                Uri.parse("android.resource://${activity?.packageName}/raw/test_video"))
            setDisplay(holder)
            prepareAsync()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    override fun onPrepared(mp: MediaPlayer?) {
        binding.progressBar.visibility = View.GONE
        mediaPlayer.start()
        binding.playButton.setImageResource(android.R.drawable.ic_media_pause)
        if(seekBarProgress==null){
            initializeSeekBar(R.string.default_value)
        }else{
            initializeSeekBar(R.string.default_value+seekBarProgress!!)
        }

        updateSeekBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        mediaPlayer.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        seekBarProgress?.let { outState.putInt("SEEKBAR_PROGRESS", it) }
    }
}