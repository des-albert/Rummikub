package org.db.rummikub

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.db.rummikub.ui.theme.RummikubTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RummikubTheme {
                Scaffold(
                    topBar = {
                        TopBar()
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ScoreSummary(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    style = MaterialTheme.typography.headlineMedium,
                    text = "RummiKub"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.secondary
        )
    )
}

@Composable
fun ScoreSummary(
    modifier: Modifier = Modifier,
    playerViewModel: MainViewModel = viewModel()
) {
    var scoreVisible by remember { mutableStateOf(false) }
    var lossLeft by remember { mutableStateOf("") }
    var lossRight by remember { mutableStateOf("") }
    var placeLeft by remember { mutableStateOf("") }
    var placeRight by remember { mutableStateOf("") }
    var winner by remember { mutableIntStateOf(0) }
    val players = playerViewModel.players
    val prevScore = playerViewModel.previousResult
    val timeLeft by playerViewModel.timeLeft.collectAsState()
    val context = LocalContext.current
    var isRunning by remember { mutableStateOf(false) }
    var duration by remember { mutableIntStateOf(1) }

    val scores = playerViewModel.history

    val imageMap = mapOf(
        "DB" to R.drawable.db,
        "Bo" to R.drawable.bo,
        "Steve" to R.drawable.steve
    )

    LaunchedEffect(Unit) {
        playerViewModel.loadHistory()
    }

    Surface(
        color = MaterialTheme.colorScheme.primary,

        ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            )
            {
                for ((index, score) in players.withIndex()) {
                    Column(
                        modifier = modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val resourceId = imageMap[score.name]
                        if (resourceId != null) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            {
                                Image(
                                    painter = painterResource(id = resourceId),
                                    contentDescription = null,
                                    modifier = Modifier.size(72.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        ElevatedButton(
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer, // Background color
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            onClick = {
                                scoreVisible = true
                                winner = index
                                when (index) {
                                    0 -> {
                                        placeLeft = players[1].name
                                        placeRight = players[2].name

                                    }

                                    1 -> {
                                        placeLeft = players[0].name
                                        placeRight = players[2].name

                                    }

                                    2 -> {
                                        placeLeft = players[0].name
                                        placeRight = players[1].name

                                    }
                                }
                            }
                        ) {
                            Text(
                                text = score.name,
                                fontSize = 24.sp,
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = score.total.toString(),
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = score.wins.toString(),
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = scores[index].toString(),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.errorContainer
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
            if (!scoreVisible) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ElevatedButton(
                        colors = ButtonDefaults.elevatedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.inversePrimary
                        ),
                        onClick = {
                            if (isRunning) {
                                playerViewModel.stopTimer()
                            } else {
                                playerViewModel.startTimer(60, context)
                            }
                            isRunning = !isRunning
                            duration = 60
                        })
                    {
                        Text(
                            text = if (isRunning && duration == 60) "Cancel" else "1:00",
                            fontSize = 18.sp,
                        )
                    }
                    ElevatedButton(
                        colors = ButtonDefaults.elevatedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.inversePrimary
                        ),
                        onClick = {
                            if (isRunning) {
                                playerViewModel.stopTimer()
                            } else {
                                playerViewModel.startTimer(120, context)
                            }
                            isRunning = !isRunning
                            duration = 120
                        })
                    {
                        Text(
                            text = if (isRunning && duration == 120) "Cancel" else "2:00",
                            fontSize = 18.sp,
                        )
                    }
                    ElevatedButton(
                        colors = ButtonDefaults.elevatedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.inversePrimary
                        ),
                        onClick = {
                            if (isRunning) {
                                playerViewModel.stopTimer()
                            } else {
                                playerViewModel.startTimer(180, context)
                            }
                            isRunning = !isRunning
                            duration = 180
                        })
                    {
                        Text(
                            text = if (isRunning && duration == 180) "Cancel" else "3:00",
                            fontSize = 18.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${playerViewModel.formatTime(timeLeft, Locale.getDefault())} ",
                        fontSize = 48.sp,
                        color = MaterialTheme.colorScheme.errorContainer
                    )

                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ElevatedButton(
                        onClick = {
                            when (prevScore.winner) {

                                0 -> {
                                    prevScore.numberGames -= 1
                                    prevScore.winner = 3
                                    players[0] = players[0].copy(
                                        total = players[0].total - prevScore.leftScore - prevScore.rightScore,
                                        wins = players[0].wins - 1
                                    )
                                    players[1] = players[1].copy(
                                        total = players[1].total + prevScore.leftScore
                                    )
                                    players[2] = players[2].copy(
                                        total = players[2].total + prevScore.rightScore
                                    )
                                }

                                1 -> {
                                    prevScore.numberGames -= 1
                                    prevScore.winner = 3
                                    players[1] = players[1].copy(
                                        total = players[1].total - prevScore.leftScore - prevScore.rightScore,
                                        wins = players[1].wins - 1
                                    )
                                    players[0] = players[0].copy(
                                        total = players[0].total + prevScore.leftScore
                                    )
                                    players[2] = players[2].copy(
                                        total = players[2].total + prevScore.rightScore
                                    )
                                }

                                2 -> {
                                    prevScore.numberGames -= 1
                                    prevScore.winner = 3
                                    players[2] = players[2].copy(
                                        total = players[2].total - prevScore.leftScore - prevScore.rightScore,
                                        wins = players[2].wins - 1
                                    )
                                    players[0] = players[0].copy(
                                        total = players[0].total + prevScore.leftScore
                                    )
                                    players[1] = players[1].copy(
                                        total = players[1].total + prevScore.rightScore
                                    )
                                }

                                3 -> {

                                }
                            }
                        }
                    ) {
                        Text(
                            text = "Undo",
                            fontSize = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(25.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ElevatedButton(
                        colors = ButtonDefaults.elevatedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.inversePrimary
                        ),
                        onClick = {
                            val newScores =
                                listOf(players[0].wins, players[1].wins, players[2].wins)
                            playerViewModel.saveHistory(newScores)
                        }
                    )
                    {
                        Text(
                            text = "Save Scores",
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(25.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ElevatedButton(
                        colors = ButtonDefaults.elevatedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.inversePrimary
                        ),
                        onClick = {
                            val newScores =
                                listOf(0,0,0)
                            playerViewModel.saveHistory(newScores)
                        }
                    )
                    {
                        Text(
                            text = "Clear History",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (scoreVisible) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedTextField(
                            value = lossLeft,
                            onValueChange = {
                                lossLeft = it
                            },
                            placeholder = {
                                Text(
                                    text = placeLeft
                                )
                            },
                            singleLine = true,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.background,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                focusedTextColor = MaterialTheme.colorScheme.error
                            ),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedTextField(
                            value = lossRight,
                            onValueChange = {
                                lossRight = it
                            },
                            placeholder = {
                                Text(
                                    text = placeRight
                                )
                            },
                            singleLine = true,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.background,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                focusedTextColor = MaterialTheme.colorScheme.error
                            ),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ElevatedButton(
                        onClick = {
                            scoreVisible = false

                            when (winner) {
                                0 -> {
                                    players[0] = players[0].copy(
                                        total = players[0].total + lossLeft.toInt() + lossRight.toInt(),
                                        wins = players[0].wins + 1
                                    )
                                    players[1] = players[1].copy(
                                        total = players[1].total - lossLeft.toInt()
                                    )
                                    players[2] = players[2].copy(
                                        total = players[2].total - lossRight.toInt()
                                    )
                                }

                                1 -> {
                                    players[1] = players[1].copy(
                                        total = players[1].total + lossLeft.toInt() + lossRight.toInt(),
                                        wins = players[1].wins + 1
                                    )
                                    players[0] = players[0].copy(
                                        total = players[0].total - lossLeft.toInt()
                                    )
                                    players[2] = players[2].copy(
                                        total = players[2].total - lossRight.toInt()
                                    )
                                }

                                2 -> {
                                    players[2] = players[2].copy(
                                        total = players[2].total + lossLeft.toInt() + lossRight.toInt(),
                                        wins = players[2].wins + 1
                                    )
                                    players[0] = players[0].copy(
                                        total = players[0].total - lossLeft.toInt()
                                    )
                                    players[1] = players[1].copy(
                                        total = players[1].total - lossRight.toInt()
                                    )
                                }
                            }

                            prevScore.winner = winner
                            prevScore.leftScore = lossLeft.toInt()
                            prevScore.rightScore = lossRight.toInt()
                            prevScore.numberGames += 1

                            lossLeft = ""
                            lossRight = ""
                        }

                    ) {
                        Text(
                            text = "Save",
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun RummikubPreview() {
    RummikubTheme {
        Scaffold(
            topBar = {
                TopBar()
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            ScoreSummary(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}