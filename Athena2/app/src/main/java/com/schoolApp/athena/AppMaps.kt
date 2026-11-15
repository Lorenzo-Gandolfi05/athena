package com.schoolApp.athena

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlin.math.sign

@Composable
fun AppMaps(
    planimetria: List<Planimetria>,
    cartine: Map<String, Int>,
    navController: NavHostController
) {
    var currentMap by remember { mutableStateOf(
        cartine[cartine.keys.toList()[0]]
    )}

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
                        text = "Piantina",
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

        val imeHandler by keyboardAsState()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var imageSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
            var scale by remember { mutableFloatStateOf(2f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .animateContentSize()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
                    .clipToBounds()
                    .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
                    .border(
                        width = 2.dp,
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
                    )
                    .height(if (imeHandler[0] == 1) 200.dp else 300.dp)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(1f, 5f)
                            scale = newScale
                            offset = Offset(
                                cap(offset.x + pan.x, imageSize.width * (scale - 1) / 2),
                                cap(offset.y + pan.y, imageSize.height * (scale - 1) / 2)
                            )
                        }
                    }
                    .graphicsLayer(
                        scaleX = maxOf(1f, scale),
                        scaleY = maxOf(1f, scale),
                        translationX = offset.x,
                        translationY = offset.y
                    )
            ) {
                AnimatedContent(targetState = currentMap, label = "Animated Picture") { targetPicture->
                    AsyncImage(
                        model = targetPicture,
                        contentDescription = "School map",
                        modifier = Modifier
                            .animateContentSize()
                            .onGloballyPositioned { coordinates ->
                                imageSize = coordinates.size.toSize()
                            }
                    )
                }
            }

            val floors = cartine.keys.toList()
            val maps = cartine.values.toList()

            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp))
                        .border(
                            width = 2.dp,
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
                        )
                        .background(MaterialTheme.colorScheme.tertiary)
                        .padding(vertical = 6.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    for (floor in floors.indices) {
                        SwapButton(
                            mapName = processFloors(floors[floor]),
                            newMap = maps[floor],
                            swapMap = {
                                currentMap = it
                                offset = Offset.Zero
                                scale = 2f
                            }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            var doClass by remember { mutableStateOf(true) }

                var searchClass by remember { mutableStateOf("") }

                Row(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 4.dp)
                ) {
                    TextField(
                        value = searchClass,
                        onValueChange = { searchClass = it },
                        singleLine = true,
                        shape = RoundedCornerShape(32.dp, 0.dp, 0.dp, 32.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        label = {
                            Text(
                                text = "Cerca una classe",
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Italic
                            )
                        },
                        modifier = Modifier
                            .width(212.dp)
                            .height(56.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(32.dp, 0.dp, 0.dp, 32.dp)
                            )
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(0.dp, 32.dp, 32.dp, 0.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .width(72.dp)
                            .height(56.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(0.dp, 32.dp, 32.dp, 0.dp)
                            )
                            .clickable { doClass = !doClass }

                    ) {
                        Text(
                            text = if(doClass) "Classe" else "Aula",
                            color = Color.White,
                            modifier = Modifier
                                .padding(end = 4.dp)
                            )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                ) {
                    items(items = filter(planimetria, searchClass, doClass)) { classe ->
                        Surface(
                            color = MaterialTheme.colorScheme.tertiary,
                            shadowElevation = 16.dp,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = if(doClass) classe.classe + ":  " else classe.stanza.toString() + ": ",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                )
                                Text(
                                    text = if(doClass) "${classe.piano}, aula ${classe.stanza}" else
                                        "${classe.piano}, ${classe.classe}",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 16.sp
                                    ),)
                            }
                        }
                    }
                }

        }
    }
}

@Composable
fun SwapButton(
    mapName: String,
    newMap: Int,
    swapMap: (nMap: Int) -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 16.dp,
                shape = CircleShape
            )
            .clickable { swapMap(newMap) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.background)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Text(
                text = mapName,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
            )
        }
    }
}

fun cap(
    inputValue: Float,
    maximum : Float
): Float {
    val sign = inputValue.sign.toInt()
    return if(inputValue*sign >= maximum) {
        maximum*sign
    }
    else {
        inputValue
    }
}

fun processFloors(
    input: String
): String {
    return if(input == "Villa") {
        "V"
    } else if (input == "Primo Piano") {
        "1"
    } else if (input == "Piano Terra") {
        "0"
    } else {
        "2"
    }
}