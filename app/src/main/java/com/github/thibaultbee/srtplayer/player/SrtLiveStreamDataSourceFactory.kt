package com.github.thibaultbee.srtplayer.player

import com.google.android.exoplayer2.upstream.DataSource
import java.net.InetSocketAddress

class SrtLiveStreamDataSourceFactory(
    private val inetSocketAddress: InetSocketAddress,
    private val passPhrase: String? = null
) :
    DataSource.Factory {
    override fun createDataSource(): DataSource {
        return SrtLiveStreamDataSource(inetSocketAddress, passPhrase)
    }
}