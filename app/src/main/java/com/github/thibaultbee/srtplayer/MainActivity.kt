package com.github.thibaultbee.srtplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.github.thibaultbee.srtplayer.databinding.ActivityMainBinding
import com.github.thibaultbee.srtplayer.player.SrtDataSourceFactory
import com.github.thibaultbee.srtplayer.player.TsOnlyExtractorFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        val url = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.srt_endpoint_key), null)
        val passphrase = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.srt_passphrase_key), null)

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            /**
             * From SRT socket option: "The password must be minimum 10 and maximum
             * 79 characters long."
             */
            .setCustomCacheKey(passphrase)
            .build()

        /**
         *  Force to extract MPEG-TS
         */
        val source =
            ProgressiveMediaSource.Factory(SrtDataSourceFactory(), TsOnlyExtractorFactory())
                .createMediaSource(mediaItem)


        val player = ExoPlayer.Builder(this)
            .build()
        player.setMediaSource(source)
        binding.playerView.player = player

        player.prepare()
        player.play()
        player.playWhenReady = true
    }
}