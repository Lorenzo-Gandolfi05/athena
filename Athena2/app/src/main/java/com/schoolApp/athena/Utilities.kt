package com.schoolApp.athena

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SubdirectoryArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun Utilities(
    navController: NavHostController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = MaterialTheme.colorScheme.tertiary,
            shadowElevation = 16.dp,
            modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Main Menu Icon",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { navController.popBackStack() },
                        tint = Color.White
                    )
                    Text(
                        text = "Strumenti",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        var averageAppended by remember { mutableStateOf("") }
        var gradesAmountAppended by remember { mutableStateOf("") }
        var current by remember { mutableStateOf("") }

        var targetFocus by remember { mutableStateOf(false) }
        var target by remember { mutableStateOf("") }

        var showRes by remember { mutableStateOf(true) }

        var oneGrade by remember { mutableFloatStateOf(0f) }
        var twoGrades by remember { mutableStateOf(listOf<Float>()) }
        var threeGrades by remember { mutableStateOf(listOf<Float>()) }

        Box(
            modifier = Modifier
                .padding(horizontal = 40.dp, vertical = 8.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp)
                )
                .clip(RoundedCornerShape(32.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Text(
                    text = "Target media",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                )
                Divider(thickness = 2.dp)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(top = 4.dp)
                ) {
                    Text(text = "Media corrente: $averageAppended")
                    Text(text = "Numero voti: $gradesAmountAppended")

                    Text(
                        text = current,
                        fontSize = 24.sp
                    )
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    item { CalcButton(name = "1") {
                        if(targetFocus) {
                            target = if(target.isEmpty()) "1" else "${target[0]}.1"
                        } else {
                            current = if(current.isEmpty()) "1" else "${current[0]}.1"
                        }
                    } }
                    item { CalcButton(name = "2") {
                        if(targetFocus) {
                            target = if(target.isEmpty()) "2" else "${target[0]}.2"
                        } else {
                            current = if(current.isEmpty()) "2" else "${current[0]}.2"
                        }
                    } }
                    item { CalcButton(name = "3") {
                        if(targetFocus) {
                            target = if(target.isEmpty()) "3" else "${target[0]}.3"
                        } else {
                            current = if(current.isEmpty()) "3" else "${current[0]}.3"
                        }
                    } }
                    item {
                        CalcButton(textOverIcon = false) {
                            if(current.isNotEmpty()) {
                                if(averageAppended.isEmpty()) {
                                    averageAppended = if ("," !in current) {
                                        current
                                    } else {
                                        (current[0].digitToInt().toFloat() +
                                                (current[2].digitToInt().toFloat() / 10)).toString()
                                    }
                                } else if (gradesAmountAppended.isEmpty()) {
                                    gradesAmountAppended = "${current[0]}"
                                    targetFocus = true
                                } else {
                                    target = if ("," !in current) {
                                        current
                                    } else {
                                        (current[0].digitToInt().toFloat() +
                                                (current[2].digitToInt().toFloat() / 10)).toString()
                                    }
                                }
                                current = ""
                            }
                        }
                    }
                    item { CalcButton(name = "4") {
                        if(targetFocus) {
                            target = if(target.isEmpty()) "4" else "${target[0]}.4"
                        } else {
                            current = if(current.isEmpty()) "4" else "${current[0]}.4"
                        }
                    } }
                    item { CalcButton(name = "5") {
                        if(targetFocus) {
                            target = if(target.isEmpty()) "5" else "${target[0]}.5"
                        } else {
                            current = if(current.isEmpty()) "5" else "${current[0]}.5"
                        }
                    } }
                    item { CalcButton(name = "6") {
                        if(targetFocus) {
                            target = if(target.isEmpty()) "6" else "${target[0]}.6"
                        } else {
                            current = if(current.isEmpty()) "6" else "${current[0]}.6"
                        }                    } }
                    item { CalcButton(textOverIcon = false, icon = Icons.Filled.Delete) {
                        if(targetFocus) {
                            if (target.isNotEmpty()) {
                                target = ""
                            }
                        } else if (current.isNotEmpty()) {
                            current = ""
                        } else if (gradesAmountAppended.isNotEmpty()) {
                            gradesAmountAppended = ""
                        } else if (averageAppended.isNotEmpty()) {
                            averageAppended = ""
                        }
                    } }
                    item { CalcButton(name = "7") {
                        if(targetFocus) {
                            target = if(target.isEmpty()) "7" else "${target[0]}.7"
                        } else {
                            current = if(current.isEmpty()) "7" else "${current[0]}.7"
                        }
                    } }
                    item { CalcButton(name = "8") {
                        if(targetFocus) {
                            target = if(target.isEmpty()) "8" else "${target[0]}.8"
                        } else {
                            current = if(current.isEmpty()) "8" else "${current[0]}.8"
                        }
                    } }
                    item { CalcButton(name = "9") {
                        if(targetFocus) {
                            target = if(target.isEmpty()) "9" else "${target[0]}.9"
                        } else {
                            current = if(current.isEmpty()) "9" else "${current[0]}.9"
                        }
                    } }
                    item { CalcButton(name = "tg", focus = targetFocus) {
                        targetFocus = !targetFocus
                        showRes = false
                    } }
                    item { Spacer(modifier = Modifier.weight(1f)) }
                    item { CalcButton(name = "0") {
                        if(targetFocus) {
                            if(target.isEmpty() || target == "1") target += 0
                        } else {
                            if(current.isEmpty() || current == "1") current += 0
                        }
                    } }
                    item { Spacer(modifier = Modifier.weight(1f)) }
                    item { CalcButton(name = "=") {
                        if(target.isEmpty()) {
                            target = "0.0"
                        }
                        val gAv = if(averageAppended.isNotEmpty()) averageAppended.toFloat() else 0f
                        val gAA = if(gradesAmountAppended.isNotEmpty()) gradesAmountAppended.toFloat() else 0f
                        val tg = target.toFloat()
                        oneGrade = (tg * (gAA + 1)) - (gAA * gAv)
                        val twoGT = (oneGrade + target.toFloat()) / 2
                        val threeGT = (oneGrade + 2 * target.toFloat()) / 3

                        if(oneGrade - oneGrade.toInt() > 0f) {
                            oneGrade = if(oneGrade - oneGrade.toInt() > 0.5f) {
                                oneGrade.toInt() + 1f
                            } else {
                                oneGrade.toInt() + 0.5f
                            }
                        }

                        val twoI = twoGT.toInt() + 0f
                        val twoFract = twoGT - twoGT.toInt()
                        twoGrades = if(twoFract > 0f) {
                            if(twoFract > 0.25f) {
                                if(twoFract > 0.5f) {
                                    if(twoFract > 0.75f) {
                                        listOf(twoI + 1f, twoI + 1f)
                                    } else listOf(twoI + 1f, twoI + 0.5f)
                                } else listOf(twoI + 0.5f, twoI + 0.5f)
                            } else listOf(twoI + 0.5f, twoI)
                        } else listOf(twoI, twoI)

                        val threeI = threeGT.toInt() + 0f
                        val threeFract = threeGT - threeI
                        threeGrades = if(threeFract > 0f) {
                            if(threeFract > 0.16f) {
                                if(threeFract > 0.33f) {
                                    if(threeFract > 0.5f) {
                                        if(threeFract > 0.66f) {
                                            if(threeFract > 0.83f) {
                                                listOf(threeI + 1f, threeI + 1f, threeI + 1f)
                                            } else listOf(threeI + 1f, threeI + 1f, threeI + 0.5f)
                                        } else listOf(threeI + 1f, threeI + 0.5f, threeI + 0.5f)
                                    } else listOf(threeI + 0.5f, threeI + 0.5f, threeI + 0.5f)
                                } else listOf(threeI + 0.5f, threeI + 0.5f, threeI)
                            } else listOf(threeI + 0.5f, threeI, threeI)
                        } else listOf(threeI, threeI, threeI)

                        targetFocus = true
                        showRes = true
                    } }
                }
            }
        }
        if(targetFocus) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .clip(RoundedCornerShape(32.dp))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()

                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Text(
                        text = "target: $target",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                    )
                    if(showRes) {
                        Text(
                            text = if(oneGrade in 0f..10f) "Un voto: $oneGrade" else "Un voto: impossibile"
                        )
                        Text(
                            text = if(twoGrades.sum() in 0f..20f)
                                "Due voti: ${twoGrades.joinToString(separator = ", ")}"
                            else "Due voti: impossibile"
                        )
                        Text(
                            text = if(threeGrades.sum() in 0f..30f)
                                "Tre voti: ${threeGrades.joinToString(separator = ", ")}"
                            else "Tre voti: impossibile",
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalcButton(
    name: String = "",
    focus: Boolean = false,
    textOverIcon: Boolean = true,
    icon: ImageVector = Icons.Filled.SubdirectoryArrowLeft,
    action: () -> Unit
) {
    Surface(
        color = if(!focus) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(24.dp)
            )
            .clip(CircleShape)
            .size(width = 48.dp, height = 48.dp)
            .clickable { action() }

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if(textOverIcon) {
                Text(
                    text = name,
                    color = if (!focus) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (!focus) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background,
                )
            }
        }
    }
}