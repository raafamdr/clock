package com.rafael.clock

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rafael.clock.databinding.FragmentStopwatchBinding
import com.rafael.clock.Constants.States

class StopwatchFragment : Fragment() {
    private var _binding: FragmentStopwatchBinding? = null
    private val binding get() = _binding!!
    private var state = States.INIT

    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStopwatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler = Handler(Looper.getMainLooper())
        binding.rightButton.setOnClickListener { handleRightButton() }
        binding.leftButton.setOnClickListener { handleLeftButton() }
    }

    private fun handleRightButton() {
        when (state) {
            States.INIT -> {
                startTimer()
            }

            States.STARTED -> {
                stopTimer()
            }

            States.STOPPED -> {
                resumeTimer()
            }
        }
    }

    private fun handleLeftButton() {
        if (state == States.STARTED) {
            lap()
        } else if (state == States.STOPPED) {
            resetTimer()
        }
    }

    private fun startTimer() {
        binding.leftButton.isEnabled = true
        handleButtons(
            R.string.lap,
            R.string.stop,
            R.color.md_theme_light_error,
            States.STARTED
        )
        runTimer()
    }

    private fun stopTimer() {
        handleButtons(
            R.string.reset,
            R.string.resume,
            R.color.md_theme_light_primary,
            States.STOPPED
        )
        handler.removeCallbacks(runnable)
    }

    private fun resumeTimer() {
        handleButtons(
            R.string.lap,
            R.string.stop,
            R.color.md_theme_light_error,
            States.STARTED
        )
        runTimer()
    }

    private fun resetTimer() {
        binding.leftButton.isEnabled = false
        handleButtons(R.string.lap, R.string.start, R.color.md_theme_light_primary, States.INIT)

        handler.removeCallbacks(runnable)
        binding.textTime.text = getString(R.string.initialTime)
        elapsedTime = 0
    }

    private fun lap() {
    }

    private fun handleButtons(
        leftButtonText: Int,
        rightButtonText: Int,
        rightButtonColor: Int,
        status: States
    ) {
        binding.leftButton.text = getString(leftButtonText)
        binding.rightButton.text = getString(rightButtonText)
        binding.rightButton.setBackgroundColor(
            ContextCompat.getColor(requireContext(), rightButtonColor)
        )
        state = status
    }

    private fun runTimer() {
        startTime = System.currentTimeMillis() - elapsedTime
        runnable = object : Runnable {
            override fun run() {
                updateTimer()
                handler.postDelayed(this, 10) // Update every 10 milliseconds
            }
        }
        handler.post(runnable)
    }

    private fun updateTimer() {
        elapsedTime = System.currentTimeMillis() - startTime

        val hours = (elapsedTime / (1000 * 60 * 60)).toInt()
        val minutes = ((elapsedTime / (1000 * 60)) % 60).toInt()
        val seconds = ((elapsedTime / 1000) % 60).toInt()
        val milliseconds = (elapsedTime % 1000) / 10

        val formattedTime = when {
            hours > 0 -> String.format("%02d:%02d.%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d.%02d", minutes, seconds, milliseconds)
        }

        binding.textTime.text = formattedTime
    }
}