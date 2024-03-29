package io.github.thibaultbee.srtplayer

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import io.github.thibaultbee.srtplayer.player.SrtDataSourceFactory
import io.github.thibaultbee.srtplayer.player.TsOnlyExtractorFactory

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
        /**
         * URL format: srt://host:port?streamid=streamid&latency=latency
         *
         * Only the following parameters are extracted from the URL query:
         * - streamid`
         * - `passphrase`: setting a passphrase with `setCustomCacheKey` method overrides this parameter
         * - `latency`
         */
        val url = PreferenceManager.getDefaultSharedPreferences(getApplication())
            .getString(
                (getApplication() as Context).getString(R.string.srt_endpoint_key),
                (getApplication() as Context).getString(R.string.srt_endpoint_default)
            )
        val passphrase = PreferenceManager.getDefaultSharedPreferences(getApplication())
            .getString(
                (getApplication() as Context).getString(R.string.srt_passphrase_key),
                (getApplication() as Context).getString(R.string.srt_passphrase_default)
            )

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