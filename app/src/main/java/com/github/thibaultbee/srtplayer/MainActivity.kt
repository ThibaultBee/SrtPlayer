package com.github.thibaultbee.srtplayer

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.thibaultbee.srtplayer.databinding.ActivityMainBinding
import com.github.thibaultbee.srtplayer.player.SrtLiveStreamDataSourceFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import java.net.InetSocketAddress

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val url = "192.168.1.27"
        val port = 9998

        val source = ProgressiveMediaSource.Factory(
            SrtLiveStreamDataSourceFactory(
                InetSocketAddress(url, port)
            ),
        ).createMediaSource(MediaItem.fromUri(Uri.EMPTY))


        val player = ExoPlayer.Builder(this)
            .build()
        player.setMediaSource(source)
        binding.playerView.player = player

        player.prepare()
        player.play()
        player.playWhenReady = true
    }
}