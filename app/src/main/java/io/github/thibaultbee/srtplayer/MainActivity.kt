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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import io.github.thibaultbee.srtplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    /**
     * URL format: srt://host:port?streamid=streamid&latency=latency
     */
    private val url: String?
        get() = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(
                getString(R.string.srt_endpoint_key),
                getString(R.string.srt_endpoint_default)
            )

    private val passphrase: String?
        get() = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(
                getString(R.string.srt_passphrase_key),
                getString(R.string.srt_passphrase_default)
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        binding.playerView.player = viewModel.player
        binding.updateButton.setOnClickListener {
            viewModel.setMediaItem(url!!, passphrase!!)
        }
    }
}