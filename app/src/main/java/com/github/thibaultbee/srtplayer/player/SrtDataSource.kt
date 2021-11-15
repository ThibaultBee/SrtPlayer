package com.github.thibaultbee.srtplayer.player

import android.net.Uri
import android.util.Log
import com.github.thibaultbee.srtdroid.enums.SockOpt
import com.github.thibaultbee.srtdroid.enums.Transtype
import com.github.thibaultbee.srtdroid.models.Socket
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.BaseDataSource
import com.google.android.exoplayer2.upstream.DataSpec
import java.io.IOException
import java.io.InputStream
import java.net.InetSocketAddress
import java.util.*

class SrtDataSource :
    BaseDataSource(/*isNetwork*/true) {

    companion object {
        const val PAYLOAD_SIZE = 1316
    }

    private var socket: Socket? = null
    private var inputStream: InputStream? = null

    override fun open(dataSpec: DataSpec): Long {
        socket = Socket().apply {
            setSockFlag(SockOpt.TRANSTYPE, Transtype.LIVE)
            setSockFlag(SockOpt.PAYLOADSIZE, PAYLOAD_SIZE)
            dataSpec.key?.let { setSockFlag(SockOpt.PASSPHRASE, it) }

            Log.i("SrtDataSource", "Connecting to ${dataSpec.uri.host}:${dataSpec.uri.port}.")
            dataSpec.uri.host?.let { connect(it, dataSpec.uri.port) }
                ?: throw IOException("Host is not valid")
            inputStream = getInputStream()
        }
        return C.LENGTH_UNSET.toLong()
    }


    /**
     * Receives from SRT socket and feeds into a queue. Depending on the length requested
     * from exoplayer, that amount of bytes is polled from queue and onto the buffer with the given offset.
     *
     * You cannot directly receive at the given length from the socket, because SRT uses a
     * predetermined payload size that cannot be dynamic
     */
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) {
            return 0
        }

        inputStream?.let {
            return it.read(buffer, offset, length)
        }
        throw IOException("Couldn't read bytes at offset: $offset")
    }

    override fun getUri(): Uri {
        return Uri.parse("srt://")
    }

    override fun close() {
        inputStream?.close()
        inputStream = null
        socket?.close()
        socket = null
    }
}
