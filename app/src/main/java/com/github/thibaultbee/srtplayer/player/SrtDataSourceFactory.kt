package com.github.thibaultbee.srtplayer.player

import com.google.android.exoplayer2.upstream.DataSource

class SrtDataSourceFactory :
    DataSource.Factory {
    override fun createDataSource(): DataSource {
        return SrtDataSource()
    }
}