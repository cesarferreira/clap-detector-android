package com.cesarferreira.clapdetector.library

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.onsets.OnsetHandler
import be.tarsos.dsp.onsets.PercussionOnsetDetector

class ClapDetector {

    var claps = 0
    private lateinit var dispatcher: AudioDispatcher

    fun detectClapAnd(tapThreshold: Int = 2, action: () -> (Unit)) {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0)
        val threshold = 12.0
        val sensitivity = 50.0
        val mPercussionDetector = PercussionOnsetDetector(
            22050f, 1024,
            OnsetHandler { time, salience ->

                claps++

                println("Time: $time Claps:$claps")

                if (claps == tapThreshold) {
                    resetClaps()
                    action()
                }

            }, sensitivity, threshold
        )
        dispatcher.addAudioProcessor(mPercussionDetector)
        Thread(dispatcher, "Audio Dispatcher").start()
    }

    fun cancel() {
        dispatcher.stop()
    }

    private fun resetClaps() {
        claps = 0
    }
}