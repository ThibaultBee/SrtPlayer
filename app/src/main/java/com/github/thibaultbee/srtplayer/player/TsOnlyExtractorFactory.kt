package com.github.thibaultbee.srtplayer.player

import com.google.android.exoplayer2.extractor.Extractor
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.extractor.ts.TsExtractor

class TsOnlyExtractorFactory: ExtractorsFactory {
    override fun createExtractors(): Array<Extractor> = arrayOf(
        TsExtractor())
}