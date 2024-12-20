/*
 * Copyright (C) 2021 Thibault B.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.thibaultbee.srtplayer

import android.app.Application
import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import io.github.thibaultbee.srtplayer.player.SrtDataSourceFactory
import io.github.thibaultbee.srtplayer.player.TsOnlyExtractorFactory

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var _player: Player? = null
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

    @OptIn(UnstableApi::class)
    private fun buildPlayer(): Player {
        /**
         * URL format: srt://host:port?streamid=streamid&latency=latency
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