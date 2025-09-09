package org.db.rummikub

import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class Score(
  var name: String,
  var total: Int,
  var wins: Int,
)

class MainViewModel : ViewModel() {
  val players: SnapshotStateList<Score> = mutableStateListOf(
    Score("DB", 0, 0),
    Score("Sherry", 0, 0),
    Score("Bo", 0, 0),
    Score("Steve", 0, 0)
  )

  private val _timeLeft = MutableStateFlow(0L)
  val timeLeft: StateFlow<Long> get() = _timeLeft.asStateFlow()

  private var mediaPlayer: MediaPlayer? = null
  private var timer: CountDownTimer? = null

  fun startTimer(duration: Long, context: Context) {
    timer?.cancel()
    timer = object : CountDownTimer(duration * 1000, 1000) {
      override fun onTick(millisUntilFinished: Long) {
        _timeLeft.value = millisUntilFinished / 1000
      }

      override fun onFinish() {
        _timeLeft.value = 0
        playSound(context)
      }
    }.start()
  }

  private fun playSound(context: Context) {
    mediaPlayer = MediaPlayer.create(context, R.raw.beep)
    mediaPlayer?.start()
  }

  override fun onCleared() {
    super.onCleared()
    timer?.cancel()
    mediaPlayer?.release()
  }

  fun stopTimer() {
    timer?.cancel()
    _timeLeft.value = 0
  }

  fun formatTime(seconds: Long, locale: Locale): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format( locale, "%02d:%02d", minutes, remainingSeconds)
  }


}