package com.github.thibaultbee.srtplayer

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import com.github.thibaultbee.srtplayer.player.SrtDataSourceFactory
import com.github.thibaultbee.srtplayer.player.TsOnlyExtractorFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    var _player: Player? = null
    val player: Player
        get() {
            _player?.let {
                releasePlayer(it)
            }
            _player = buildPlayer()
            return _player!!
        }

    private fun releasePlayer(player: Player) {
        player.stop()
        player.release()
    }

    private fun buildPlayer(): Player {
        val url = PreferenceManager.getDefaultSharedPreferences(getApplication())
            .getString((getApplication() as Context).getString(R.string.srt_endpoint_key), null)
        val passphrase = PreferenceManager.getDefaultSharedPreferences(getApplication())
            .getString((getApplication() as Context).getString(R.string.srt_passphrase_key), null)

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


        val player = ExoPlayer.Builder(getApplication())
            .build()
        player.setMediaSource(source)

        player.prepare()
        player.play()
        player.playWhenReady = true
        return player
    }
}