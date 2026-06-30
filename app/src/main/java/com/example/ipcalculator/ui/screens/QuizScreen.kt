package com.example.ipcalculator.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ipcalculator.IPCalculator
import com.example.ipcalculator.ui.components.GlowingCard
import com.example.ipcalculator.ui.components.SectionHeader

@Composable
fun QuizScreen() {
    var score by rememberSaveable { mutableStateOf(0) }
    var streak by rememberSaveable { mutableStateOf(0) }
    var bestStreak by rememberSaveable { mutableStateOf(0) }
    var totalAnswered by rememberSaveable { mutableStateOf(0) }
    
    // We recreate the question state using a trigger to recreate
    var questionTrigger by rememberSaveable { mutableStateOf(0) }
    val currentQuestion = remember(questionTrigger) { IPCalculator.generateQuizQuestion() }
    
    var selectedAnswer by rememberSaveable(questionTrigger) { mutableStateOf<String?>(null) }
    var isAnswerCorrect by rememberSaveable(questionTrigger) { mutableStateOf(false) }
    var answered by rememberSaveable(questionTrigger) { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scoreboard Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Subnetting Quiz", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Test CCNA/Network+ skills", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Score", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$score/$totalAnswered", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    // Streak badge
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
                        Text(
                            text = "$streak",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }

        // Question Card
        GlowingCard {
            SectionHeader(title = "Question")
            
            Text(
                text = currentQuestion.prompt,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Multiple choice buttons
            currentQuestion.options.forEach { option ->
                val isSelected = selectedAnswer == option
                val isThisCorrect = option == currentQuestion.correctAnswer
                
                val containerColor = when {
                    answered && isThisCorrect -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    answered && isSelected && !isThisCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                    isSelected -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surface
                }
                
                val borderColor = when {
                    answered && isThisCorrect -> MaterialTheme.colorScheme.primary
                    answered && isSelected && !isThisCorrect -> MaterialTheme.colorScheme.error
                    isSelected -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                }

                val contentColor = when {
                    answered && isThisCorrect -> MaterialTheme.colorScheme.primary
                    answered && isSelected && !isThisCorrect -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }

                OutlinedButton(
                    onClick = {
                        if (!answered) {
                            selectedAnswer = option
                            answered = true
                            isAnswerCorrect = option == currentQuestion.correctAnswer
                            totalAnswered++
                            if (isAnswerCorrect) {
                                score++
                                streak++
                                if (streak > bestStreak) bestStreak = streak
                            } else {
                                streak = 0
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = containerColor, contentColor = contentColor),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        // Result & Explanation Card
        AnimatedVisibility(
            visible = answered,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAnswerCorrect) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    } else {
                        MaterialTheme.colorScheme.error.copy(alpha = 0.08f)
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    if (isAnswerCorrect) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isAnswerCorrect) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isAnswerCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (isAnswerCorrect) "Correct Answer!" else "Incorrect Answer!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAnswerCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                    
                    // Detailed explanation text
                    val explanation = getQuizExplanation(currentQuestion)
                    Text(
                        text = explanation,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = { questionTrigger++ },
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Next Question")
                    }
                }
            }
        }
    }
}

// Generates educational explanations dynamically
fun getQuizExplanation(question: IPCalculator.QuizQuestion): String {
    val result = IPCalculator.calculateIPv4(question.ip, question.prefix)!!
    return when (question.questionType) {
        IPCalculator.QuestionType.NETWORK_ADDRESS -> {
            "To find the Network Address of ${question.ip}/${question.prefix}, we perform a logical AND operation between the IP and the Subnet Mask (${result.subnetMask}). " +
                    "This clears out the host bits, resulting in ${result.networkAddress}."
        }
        IPCalculator.QuestionType.BROADCAST_ADDRESS -> {
            "To find the Broadcast Address of ${question.ip}/${question.prefix}, we perform a logical OR between the IP and the Wildcard Mask (${result.wildcardMask}). " +
                    "This sets all host bits to 1, resulting in ${result.broadcastAddress}."
        }
        IPCalculator.QuestionType.FIRST_HOST -> {
            "The First Usable Host is always the Network Address + 1. " +
                    "Since the Network Address is ${result.networkAddress}, adding 1 gives ${result.usableRangeStart}."
        }
        IPCalculator.QuestionType.LAST_HOST -> {
            "The Last Usable Host is always the Broadcast Address - 1. " +
                    "Since the Broadcast Address is ${result.broadcastAddress}, subtracting 1 gives ${result.usableRangeEnd}."
        }
        IPCalculator.QuestionType.USABLE_HOSTS -> {
            "The total number of hosts is 2^(32 - ${question.prefix}) = ${result.totalHosts}. " +
                    "Subtracting 2 addresses (one for the Network address and one for the Broadcast address) gives ${result.usableHosts} usable hosts."
        }
        IPCalculator.QuestionType.WILDCARD_MASK -> {
            "The Wildcard Mask is the bitwise inverse of the Subnet Mask (${result.subnetMask}). " +
                    "Subtracting each octet of the Subnet Mask from 255 gives ${result.wildcardMask}."
        }
    }
}
