package com.schoolApp.athena

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@Composable
fun CalendarComp(
    db: FirebaseFirestore,
    orario: Map<String, List<String>>,
    overrides: Map<String, String>,
    totNotes: HashMap<String, List<String>>,
    preferences: MutableState<java.util.HashMap<String, Long>>,
    navController: NavHostController
) {
    val uid = FirebaseAuth.getInstance().uid ?: ""

    var mainCal by remember { mutableStateOf(true)}
    val litBoxes by remember { mutableStateOf(generateValues(totNotes)) }

    var dumday by remember { mutableIntStateOf(
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    ) }
    var dummonth by remember { mutableIntStateOf(
        Calendar.getInstance().get(Calendar.MONTH)
    ) }
    var dumweekday by remember { mutableIntStateOf(
        (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5 ) % 7
    ) }
    var dumyear by remember { mutableIntStateOf(
        Calendar.getInstance().get(Calendar.YEAR)
    ) }

    val referenceYear by remember { mutableIntStateOf(if(dummonth < 7) dumyear else dumyear + 1) }

    // Daily subject information
    var subjects by remember { mutableStateOf(orario["$dumweekday"] ?: listOf()) }

    // Daily notes information
    var dayNotes by remember { mutableStateOf(totNotes["$dummonth-$dumday"] ?: listOf()) }
    var newNote by remember { mutableStateOf("") }

    val monthDis = hashMapOf(
        0 to "Gennaio",
        1 to "Febbraio",
        2 to "Marzo",
        3 to "Aprile",
        4 to "Maggio",
        5 to "Giugno",
        6 to "Luglio",
        7 to "Agosto",
        8 to "Settembre",
        9 to "Ottobre",
        10 to "Novembre",
        11 to "Dicembre"
    )

    val weekDisShort = hashMapOf(
        0 to "Lun",
        1 to "Mar",
        2 to "Mer",
        3 to "Gio",
        4 to "Ven",
        5 to "Sab",
        6 to "Dom"
    )

    val monthCap = hashMapOf(
        0 to 31,
        1 to if(referenceYear%4 == 0) 29 else 28,
        2 to 31,
        3 to 30,
        4 to 31,
        5 to 30,
        6 to 31,
        7 to 31,
        8 to 30,
        9 to 31,
        10 to 30,
        11 to 31
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .shadow(elevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiary)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
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
                        text = "Calendario",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = monthDis[dummonth].toString(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Today,
                        contentDescription = "Today",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(horizontal = 8.dp)
                            .weight(1f)
                            .clickable {
                                dumweekday = (Calendar
                                    .getInstance()
                                    .get(Calendar.DAY_OF_WEEK) + 5) % 7
                                dumday = Calendar
                                    .getInstance()
                                    .get(Calendar.DAY_OF_MONTH)
                                dummonth = Calendar
                                    .getInstance()
                                    .get(Calendar.MONTH)
                                dumyear = Calendar
                                    .getInstance()
                                    .get(Calendar.YEAR)

                                subjects = orario["$dumweekday"] ?: listOf()
                                dayNotes = totNotes["$dummonth-$dumday"] ?: listOf()
                            },
                        tint = Color.White
                    )

                    for(change in -2..2) {
                        DayBut(
                            change = change,
                            dumday = dumday,
                            dummonth = dummonth,
                            relWeek = (dumweekday + change + 7) % 7,
                            monthCap = monthCap,
                            changeData = { newDay, newWeek, mInc ->
                                dumday = newDay
                                dumweekday = newWeek
                                dummonth = (dummonth + mInc + 12) % 12

                                dayNotes = totNotes["$dummonth-$dumday"] ?: listOf()
                                subjects = orario["$dumweekday"] ?: listOf()

                                newNote = ""
                            }
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.CalendarMonth
                        ,
                        contentDescription = "Calendar Icon",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(horizontal = 8.dp)
                            .weight(1f)
                            .clickable {
                                mainCal = !mainCal
                            },
                        tint = Color.White
                    )
                }
            }
        }

        if(mainCal) {
            val minDistance = LocalConfiguration.current.screenWidthDp.dp / 4
            var dragAmount by remember { mutableFloatStateOf(0f) }
            var actionTriggered by remember { mutableStateOf(false) }

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                dragAmount = 0f
                                actionTriggered = false
                            },
                            onHorizontalDrag = { _, dragAmountChange ->
                                dragAmount += dragAmountChange

                                if (!actionTriggered) {
                                    when {
                                        dragAmount > minDistance.toPx() -> {
                                            actionTriggered = true
                                            val relDay = dayCheck(dumday - 1, dummonth, monthCap)
                                            if (relDay > dumday) {
                                                dummonth = (dummonth + 11) % 12
                                            }
                                            dumday = relDay
                                            dayNotes = totNotes["$dummonth-$dumday"] ?: listOf()
                                            dumweekday = (dumweekday + 6) % 7
                                            subjects = orario["$dumweekday"] ?: listOf()
                                            dragAmount = 0f

                                            newNote = ""
                                        }

                                        dragAmount < -minDistance.toPx() -> {
                                            actionTriggered = true
                                            val relDay = dayCheck(dumday + 1, dummonth, monthCap)
                                            if (relDay < dumday) {
                                                dummonth = (dummonth + 1) % 12
                                            }
                                            dumday = relDay
                                            dayNotes = totNotes["$dummonth-$dumday"] ?: listOf()
                                            dumweekday = (dumweekday + 1) % 7
                                            subjects = orario["$dumweekday"] ?: listOf()

                                            newNote = ""
                                        }
                                    }
                                }
                            },
                            onDragEnd = {
                                dragAmount = 0f
                                actionTriggered = false
                            },
                            onDragCancel = {
                                dragAmount = 0f
                                actionTriggered = false
                            }
                        )
                    }
            ) {
                itemsIndexed(
                    items = if (dumweekday != 6) {
                        subjects.subList(0, (overrides["$dummonth-$dumday"] ?: "${subjects.size}").toInt())
                    } else subjects
                ) { hour, subject ->
                    var forceRecompose by remember { mutableLongStateOf(0L) }
                    val preference by remember {
                        derivedStateOf {
                            (preferences.value["$dumweekday-$hour"] ?: 0L) to forceRecompose
                        }
                    }
                    val override by remember { derivedStateOf { (overrides["$dummonth-$dumday-$hour"] ?: "") } }

                    Surface(
                        color = MaterialTheme.colorScheme.tertiary,
                        shadowElevation = 16.dp,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        if (override.isNotBlank()) {
                            Text(
                                text = override,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        } else if ("/" !in subject) {
                            Text(
                                text = override.ifBlank { subject },
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        } else {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = subject.substringBefore(" / "),
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        textDecoration = if (preference.first == 1L) TextDecoration.Underline else TextDecoration.None
                                    ),
                                    modifier = Modifier
                                        .clickable {
                                            if (preferences.value["$dumweekday-$hour"] == 1L) {
                                                preferences.value["$dumweekday-$hour"] = 0L
                                                forceRecompose++
                                                val updates = hashMapOf<String, Any>("$dumweekday-$hour" to 0L)
                                                db.document("/Users/$uid/SavedInfo/Preferences")
                                                    .set(updates, SetOptions.merge())
                                            } else {
                                                preferences.value["$dumweekday-$hour"] = 1L
                                                forceRecompose++
                                                val updates = hashMapOf<String, Any>("$dumweekday-$hour" to 1L)
                                                db.document("/Users/$uid/SavedInfo/Preferences")
                                                    .set(updates, SetOptions.merge())
                                            }
                                        }
                                )
                                Text(
                                    text = " / ",
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                )
                                Text(
                                    text = subject.substringAfter(" / "),
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        textDecoration = if (preference.first == 2L) TextDecoration.Underline else TextDecoration.None
                                    ),
                                    modifier = Modifier
                                        .clickable {
                                            if (preferences.value["$dumweekday-$hour"] == 2L) {
                                                preferences.value["$dumweekday-$hour"] = 0L
                                                forceRecompose++
                                                val updates = hashMapOf<String, Any>("$dumweekday-$hour" to 0L)
                                                db.document("/Users/$uid/SavedInfo/Preferences")
                                                    .set(updates, SetOptions.merge())
                                            } else {
                                                preferences.value["$dumweekday-$hour"] = 2L
                                                forceRecompose++
                                                val updates = hashMapOf<String, Any>("$dumweekday-$hour" to 2L)
                                                db.document("/Users/$uid/SavedInfo/Preferences")
                                                    .set(updates, SetOptions.merge())
                                            }
                                        }
                                )
                            }
                        }
                    }
                }

                // Separatore orario / note
                item { NoteDivider() }

                // Aggiungi una nota
                item {
                    TextField(
                        value = newNote,
                        onValueChange = { newNote = it },
                        singleLine = true,
                        shape = RoundedCornerShape(28.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTrailingIconColor = MaterialTheme.colorScheme.primary
                        ),
                        label = {
                            Text(
                                text = "Nuova nota",
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Italic
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Aggiungi",
                                modifier = Modifier
                                    .clickable {
                                        if (newNote.isNotBlank()) {
                                            totNotes["$dummonth-$dumday"] = dayNotes + newNote
                                            dayNotes = dayNotes + newNote
                                            val updates = hashMapOf<String, Any>("$dummonth-$dumday" to dayNotes)
                                            db.document("/Users/$uid/SavedInfo/Notes").set(updates, SetOptions.merge())
                                            newNote = ""
                                        }
                                    }
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .width(288.dp)
                            .height(56.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(28.dp)
                            )
                    )
                }

                // Elenco note
                items(items = dayNotes) { note ->
                    Surface(
                        color = MaterialTheme.colorScheme.tertiary,
                        shadowElevation = 16.dp,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                        ) {
                            Text(
                                text = note,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .weight(1f)
                            )
                            Button(
                                onClick = {
                                    totNotes["$dummonth-$dumday"] = dayNotes - note
                                    dayNotes = dayNotes - note
                                    val updates = hashMapOf<String, Any>("$dummonth-$dumday" to dayNotes)
                                    db.document("/Users/$uid/SavedInfo/Notes").set(updates, SetOptions.merge())
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Rimuovi"
                                )
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = !mainCal,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 500)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 500)
            )
        ) {
            val monthSequence = listOf(8, 9, 10, 11, 0, 1, 2, 3, 4, 5)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7)
            ) {
                for (num in monthSequence) {
                    val topPadding = if(num != 8) 84.dp else  12.dp
                    item(span = { GridItemSpan(7) }) {
                        Box {
                            Text(
                                text = monthDis[num]!!,
                                style = TextStyle.Default.copy(
                                    fontSize = 24.sp,
                                    textDecoration = TextDecoration.Underline,
                                ),
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .padding(top = topPadding, bottom = 6.dp)
                                    .align(alignment = Alignment.Center)
                            )
                        }
                    }
                    items(count = 7) { day ->
                        Box {
                            Text(
                                text = weekDisShort[day] ?: "",
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .align(alignment = Alignment.Center)
                            )
                        }
                    }
                    items(count = getMonthStart(num+1)) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    items(count = monthCap[num]!!) {
                        val notesPresent = litBoxes["$num-${it + 1}"] ?: false
                        DayBox(it + 1, notesPresent) {
                            dumday = it + 1
                            dumweekday = (getMonthStart(num + 1) + it) % 7
                            dummonth = num
                            dayNotes = totNotes["$dummonth-$dumday"] ?: listOf()
                            subjects = orario["$dumweekday"] ?: listOf()
                            mainCal = true
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DayBut(
    change: Int,
    dumday: Int,
    dummonth: Int,
    relWeek: Int,
    monthCap: HashMap<Int, Int>,
    changeData: (newDay: Int, newWeek: Int, mInc: Int) -> Unit,
) {
    val weekDisShort = hashMapOf(
        0 to "Lun",
        1 to "Mar",
        2 to "Mer",
        3 to "Gio",
        4 to "Ven",
        5 to "Sab",
        6 to "Dom"
    )
    val relDay = dayCheck(dumday + change, dummonth, monthCap)
    Button(
        onClick = {
            if(relDay - dumday > 3) {
                changeData(relDay, relWeek, -1)
            } else if(relDay - dumday < -3) {
                changeData(relDay, relWeek, +1)
            } else {
                changeData(relDay, relWeek, 0)
            }
        },
        contentPadding = PaddingValues(0.dp),
        colors = if(change == 0) ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ) else ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        ),
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${weekDisShort[relWeek]}",
                fontSize = 14.sp
            )
            Text(
                text = "$relDay",
                fontSize = 14.sp
            )
        }
    }
}

fun dayCheck(
    mday: Int,
    dummonth: Int,
    monthCap: HashMap<Int, Int>
): Int {
    val monthcap = monthCap[dummonth]!!
    val dummy = if(mday > monthcap) {
        mday - monthcap
    } else if(mday < 1) {
        monthCap[(dummonth + 11) % 12]!! + mday
    }
    else {
        mday
    }
    return dummy
}

@Composable
fun NoteDivider() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Divider(
            thickness = 4.dp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(vertical = 14.dp)
        )
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 16.dp,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Note",
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun DayBox(
    dayOfMonth: Int,
    notesPresent: Boolean,
    changeDay: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .clip(CircleShape)
            .clickable { changeDay() }
            .background(if(notesPresent) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.background)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    ) {
        Text(
            text = dayOfMonth.toString(),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .padding(12.dp)
                .align(alignment = Alignment.Center)
        )
    }
}

fun generateValues(totNotes: HashMap<String, List<String>>): Map<String, Boolean> {
    val parsedMap = hashMapOf<String, Boolean>()
    for((key, value) in totNotes) {
        if(value != listOf<String>()) {
            parsedMap[key] = true
        }
    }
    return parsedMap.toMap()
}

fun getMonthStart(month: Int): Int {
    val year = Calendar.getInstance().get(Calendar.YEAR)
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
    val format = SimpleDateFormat("yyyy-MM-dd")

    val parsedDate: Date = if(currentMonth < 7 && month > 6) {
        format.parse("${year-1}-${lengthCheck(month)}-01")!!
    } else if(currentMonth > 6 && month < 7){
        format.parse("${year+1}-${lengthCheck(month)}-01")
    } else format.parse("$year-${lengthCheck(month)}-01")!!

    val calendar = Calendar.getInstance()
    calendar.time = parsedDate
    val dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
    return dayOfWeek
}

fun lengthCheck(value: Int): String {
    val dummy = "0$value"
    return dummy.substring(dummy.length-2)
}