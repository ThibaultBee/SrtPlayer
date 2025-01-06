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
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import io.github.thibaultbee.srtplayer.player.SrtDataSourceFactory
import io.github.thibaultbee.srtplayer.player.TsOnlyExtractorFactory

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val player = ExoPlayer.Builder(getApplication()).build()

    @OptIn(UnstableApi::class)
    fun setMediaItem(url: String, passphrase: String) {
        player.setMediaSource(createMediaSource(url, passphrase))
        player.prepare()
        player.playWhenReady = true
    }

    @OptIn(UnstableApi::class)
    private fun createMediaItem(url: String, passphrase: String): MediaItem {
        return MediaItem.Builder()
            .setUri(url)
            /**
             * From SRT socket option: "The password must be minimum 10 and maximum
             * 79 characters long."
             */
            .setCustomCacheKey(passphrase)
            .build()
    }

    @OptIn(UnstableApi::class)
    private fun createMediaSource(mediaItem: MediaItem): MediaSource {
        return ProgressiveMediaSource.Factory(SrtDataSourceFactory(), TsOnlyExtractorFactory())
            .createMediaSource(mediaItem)
    }

    private fun createMediaSource(url: String, passphrase: String): MediaSource {
        return createMediaSource(createMediaItem(url, passphrase))
    }

    override fun onCleared() {
        player.stop()
        player.release()
        super.onCleared()
    }
}