# SrtPlayer

An Android SRT test player powered by [ExoPlayer](https://github.com/google/ExoPlayer). Most of this
code comes from [YoussefHenna](https://github.com/YoussefHenna) reply in
the [SRT support ExoPlayer issue](https://github.com/google/ExoPlayer/issues/8647).

It demonstrates how to use [srtdroid](https://github.com/ThibaultBee/srtdroid) and read data from a
remote SRT device.

It is only a demo project, it is not recommend to use it in production.

## Test

In order to test SrtPlayer, I use 2 tools:

* `FFmpeg`
* `srt-live-transmit`

On my computer with IP `ip.of.my.computer`, run:

```bash
srt-live-transmit srt://:1234 srt://:9998
```

On another terminal, run:

```bash
ffmpeg -f lavfi -re -i smptebars=duration=300:size=1280x720:rate=30 -f lavfi -re -i sine=frequency=1000:duration=60:sample_rate=44100 -pix_fmt yuv420p -c:v libx264 -b:v 1000k -g 30 -keyint_min 40 -profile:v baseline -preset veryfast -f mpegts "srt://127.0.0.1:1234?pkt_size=1316"
```

Then, on the Android application, set the `SRT endpoint` to:

```bash
srt://ip.of.my.computer:9998?streamid=mystream
```

Only caller mode in Live transmission is supported.

See [SrtDataSource](https://github.com/ThibaultBee/SrtPlayer/blob/39f282973476f6589013092a2b6710e4a7192194/app/src/main/java/io/github/thibaultbee/srtplayer/player/SrtDataSource.kt#L28)

Press `Update`.

## Licence

    Copyright 2021 Thibault B.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.