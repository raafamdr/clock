package com.rafael.clock

import android.content.Intent
import android.os.Bundle
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

        binding.rightButton.setOnClickListener { handleRightButton() }
        binding.leftButton.setOnClickListener { handleLeftButton() }

        StopwatchService.state.observe(viewLifecycleOwner) {
            updateState(it)
        }

        StopwatchService.formattedTime.observe(viewLifecycleOwner) {
            binding.textTime.text = it
        }

        restoreButtons()
    }

    private fun updateState(state: States) {
        this.state = state
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

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), StopwatchService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun startTimer() {
        binding.leftButton.isEnabled = true
        updateButtons(R.string.lap, R.string.stop, R.color.md_theme_light_error)
        saveButtonsState(R.string.lap, R.string.stop, R.color.md_theme_light_error, 1)
        sendCommandToService(Constants.ACTION_START_SERVICE)
    }

    private fun stopTimer() {
        updateButtons(R.string.reset, R.string.resume, R.color.md_theme_light_primary)
        saveButtonsState(R.string.reset, R.string.resume, R.color.md_theme_light_primary, 1)
        sendCommandToService(Constants.ACTION_STOP_SERVICE)
    }

    private fun resumeTimer() {
        updateButtons(R.string.lap, R.string.stop, R.color.md_theme_light_error)
        saveButtonsState(R.string.lap, R.string.stop, R.color.md_theme_light_error, 1)
        sendCommandToService(Constants.ACTION_RESUME_SERVICE)
    }

    private fun resetTimer() {
        binding.leftButton.isEnabled = false
        binding.textTime.text = getString(R.string.initialTime)
        updateButtons(R.string.lap, R.string.start, R.color.md_theme_light_primary)
        saveButtonsState(R.string.lap, R.string.start, R.color.md_theme_light_primary, 0)
        sendCommandToService(Constants.ACTION_RESET_SERVICE)
    }

    private fun lap() {
    }

    private fun updateButtons(leftButtonText: Int, rightButtonText: Int, rightButtonColor: Int) {
        binding.leftButton.text = getString(leftButtonText)
        binding.rightButton.text = getString(rightButtonText)
        binding.rightButton.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                rightButtonColor
            )
        )
    }

    private fun saveButtonsState(
        leftButtonText: Int,
        rightButtonText: Int,
        rightButtonColor: Int,
        isLeftButtonEnabled: Int
    ) {
        StopwatchService.leftButtonTextResId = leftButtonText
        StopwatchService.rightButtonTextResId = rightButtonText
        StopwatchService.rightButtonColorResId = rightButtonColor
        StopwatchService.isLeftButtonEnabled = isLeftButtonEnabled
    }

    private fun restoreButtons() {
        if ((StopwatchService.leftButtonTextResId != 0) &&
            (StopwatchService.rightButtonTextResId != 0) &&
            (StopwatchService.rightButtonColorResId != 0) &&
            (StopwatchService.isLeftButtonEnabled != -1)
        ) {
            updateButtons(
                StopwatchService.leftButtonTextResId,
                StopwatchService.rightButtonTextResId,
                StopwatchService.rightButtonColorResId
            )

            binding.leftButton.isEnabled = StopwatchService.isLeftButtonEnabled == 1
        }
    }
}