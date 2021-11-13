package com.github.thibaultbee.srtplayer.player

import android.net.Uri
import android.util.Log
import com.github.thibaultbee.srtdroid.enums.SockOpt
import com.github.thibaultbee.srtdroid.enums.Transtype
import com.google.android.exoplayer2.upstream.BaseDataSource
import java.io.IOException
import com.github.thibaultbee.srtdroid.models.Socket
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DataSpec
import java.net.InetSocketAddress
import java.util.*

class SrtLiveStreamDataSource(
    private val inetSocketAddress: InetSocketAddress,
    private val passPhrase: String?
) :
    BaseDataSource(/*isNetwork*/true) {

    companion object {
        const val PAYLOAD_SIZE = 1316
    }

    private var socket: Socket? = null
    private val byteQueue: Queue<Byte> = LinkedList()

    override fun open(dataSpec: DataSpec): Long {
        socket = Socket().apply {
            setSockFlag(SockOpt.TRANSTYPE, Transtype.LIVE)
            setSockFlag(SockOpt.PAYLOADSIZE, PAYLOAD_SIZE)
            passPhrase?.let { setSockFlag(SockOpt.PASSPHRASE, it) }
            Log.i("SrtLiveStreamDataSource", "Connecting to $inetSocketAddress")
            connect(inetSocketAddress)
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
        var bytesReceived = 0
        if (socket != null) {
            val received = socket!!.recv(PAYLOAD_SIZE)
            for (byte in received.second /*received byte array*/) {
                byteQueue.offer(byte)
            }
            repeat(length) { index ->
                val byte = byteQueue.poll()
                if (byte != null) {
                    buffer[index + offset] = byte
                    bytesReceived++
                }
            }
            return bytesReceived
        }
        throw IOException("Couldn't read bytes at offset: $offset")
    }

    override fun getUri(): Uri {
        return Uri.parse("srt://$inetSocketAddress")
    }

    override fun close() {
        socket?.close()
        socket = null
    }
}
