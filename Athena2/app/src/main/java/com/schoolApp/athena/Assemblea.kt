package com.schoolApp.athena

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

@Composable
fun Assemblea(
    username: String,
    eventStatus: Int,
    userStatus: String,
    school: String,
    navController: NavHostController,
    changeUserStatus: (newStatus: String) -> Unit,
) {
    val db = Firebase.firestore
    var addScreen by remember { mutableStateOf(false) }
    var information by remember { mutableStateOf(false) }

    var suggestions by remember { mutableStateOf(listOf<GruppoAssemblea>()) }

    LaunchedEffect(Unit) {

        db.collection("Scuole").document(school).collection("/Eventi/Assemblea/Suggestions")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    suggestions = snapshots.documents.mapNotNull { document ->
                        document.toObject(GruppoAssemblea::class.java)
                    }
                }
            }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = MaterialTheme.colorScheme.tertiary,
            shadowElevation = 16.dp,
            modifier = Modifier
                .padding(bottom = 4.dp)
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
                    text = "Assemblea",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Event p2 information",
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { information = true }
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Box(
            contentAlignment = Alignment.Center
        ) {
            Divider(
                thickness = 4.dp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(vertical = 14.dp)
            )
            Row {
                Spacer(modifier = Modifier.weight(1f))

                StateBall(eventStatus, 1)

                Spacer(modifier = Modifier.weight(1f))

                StateBall(eventStatus, 2)

                Spacer(modifier = Modifier.weight(1f))

                StateBall(
                    if(userStatus == "") eventStatus else 2,
                    3
                )

                Spacer(modifier = Modifier.weight(1f))

                StateBall(
                    if(userStatus != "" && eventStatus == 3) 4 else eventStatus,
                    4
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        when(eventStatus) {
            1 -> GroupPhase1(
                userStatus = userStatus,
                suggestions = suggestions,
                addGroup = { addScreen = true },
                raiseFun = { changeUserStatus("") },
            )
            2 -> GroupPhase2(
                username = username,
                userStatus = userStatus,
                suggestions = suggestions,
                raiseFun = { changeUserStatus("") },
                changeUserStatus = { changeUserStatus(it) }
            )
            3 -> GroupPhase3(
                username = username,
                userStatus = userStatus,
                suggestions = suggestions,
                changeUserStatus = { changeUserStatus(it) }
            )
        }
    }
    if(addScreen) {
        var guideLines by remember { mutableStateOf(true) }

        Box(contentAlignment = Alignment.Center) {
            Surface(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 32.dp,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                if(guideLines) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Linee Guida",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                        )
                        Text(
                            text = stringResource(R.string.groups_guidelines),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        )
                        Button(onClick = { guideLines = false }) {
                            Text("Ho capito")
                        }
                    }
                } else {
                    var groupName by remember { mutableStateOf("") }
                    var groupDesc by remember { mutableStateOf("") }
                    var raiseErr1 by remember { mutableStateOf(false) }
                    var raiseErr2 by remember { mutableStateOf(false) }
                    var noDup by remember { mutableStateOf(false)}

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Crea un gruppo",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                        )
                        Insertable(
                            info = groupName,
                            message = "Nome del gruppo",
                            textChange = { if(it.length <= 50) groupName = it }
                        )

                        if(raiseErr1) ErrorMess(text = "Dai un nome al tuo gruppo!")
                        if(raiseErr2) ErrorMess(text = "Il simbolo '£' non è valido")

                        Insertable(
                            info = groupDesc,
                            message = "Descrizione",
                            textChange = { if(it.length <= 100) groupDesc = it },
                        )

                        if (noDup) ErrorMess(text = "Questo gruppo esiste già!")

                        Row {
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = { addScreen = false },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text("Annulla")
                            }
                            Button(
                                onClick = {
                                    raiseErr1 = false
                                    raiseErr2 = false
                                    noDup = false
                                    if(groupName.isNotBlank()) {
                                        if("£" !in groupName) {
                                            db.runTransaction { transaction ->
                                                val docSnapshot = transaction
                                                    .get(db.document("/Eventi/Assemblea/Suggestions/$groupName-0"))
                                                if (!docSnapshot.exists()) {
                                                    val update = GruppoAssemblea(
                                                        titolo = groupName,
                                                        descrizione = groupDesc,
                                                        relatori = listOf(username),
                                                        timestamp = System.currentTimeMillis()
                                                    )
                                                    db.document("/Eventi/Assemblea/Suggestions/$groupName-0")
                                                        .set(update)

                                                    changeUserStatus("$groupName-0£££relatore")

                                                    addScreen = false
                                                } else noDup = true
                                            }
                                        } else raiseErr2 = true
                                    } else raiseErr1 = true
                                },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text("Crea gruppo")
                            }
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
    if(information) {
        var dial by remember { mutableIntStateOf(0) }
        val stringResId = when (dial) {
            0 -> R.string.event_s0
            1 -> R.string.event_s1
            2 -> R.string.event_s2
            3 -> R.string.event_s3
            4 -> R.string.event_s4
            else -> R.string.event_s0
        }
        val titleId = when (dial) {
            0 -> "La nuova assemblea"
            1 -> "1. Proposta gruppi:"
            2 -> "2. Iscrizione:"
            3 -> "3. Ultime iscrizioni:"
            4 -> "4. Il tuo gruppo:"
            else -> ""
        }
        Box(contentAlignment = Alignment.Center) {
            Surface(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(384.dp, 320.dp)
                    .padding(horizontal = 12.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(16.dp),
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .padding(start = 16.dp, end = 24.dp)
                ) {
                    Row {
                        Text(
                            text = titleId,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = CircleShape
                                )
                                .clickable { information = false }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clip(RoundedCornerShape(14.dp))
                            ) {
                                Text(
                                    text = "x",
                                    color = MaterialTheme.colorScheme.background,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(stringResId),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(end = 28.dp)
                        ) {
                            if(dial != 0) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                                    contentDescription = "Go back",
                                    modifier = Modifier
                                        .clickable { dial-- }
                                        .size(36.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                                    contentDescription = "Go back",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier
                                        .size(36.dp)
                                )
                            }
                        }

                        Text("${dial+1}/5")

                        Box(
                            modifier = Modifier
                                .padding(start = 28.dp)
                        ) {
                            if(dial != 4) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                                    contentDescription = "Go back",
                                    modifier = Modifier
                                        .clickable { dial++ }
                                        .size(36.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                                    contentDescription = "Go back",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier
                                        .size(36.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupPhase1(
    userStatus: String,
    suggestions: List<GruppoAssemblea>,
    addGroup: () -> Unit,
    raiseFun: () -> Unit,
) {
    val db = Firebase.firestore

    Text(
        text = "Proposta Gruppi",
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        ),
        modifier = Modifier
            .padding(vertical = 12.dp)
    )

    if(userStatus.isBlank()) {
        Button(
            onClick = { addGroup() },
            modifier = Modifier
                .padding(bottom = 8.dp)
        ) {
            Text("Crea un gruppo")
        }
    } else {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 16.dp,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Relatore del gruppo:\n${userStatus.substringBefore("£££")}",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f)
                )

                Button(
                    onClick = {
                        db.document("/Eventi/Assemblea/Suggestions/${userStatus
                            .substringBefore("£££")}").delete()
                        raiseFun()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove your application",
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }
        }
    }

    LazyColumn {
        items(items = suggestions) {suggestion ->
            var expanded by rememberSaveable { mutableStateOf(false) }

            Surface(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(32.dp),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(32.dp)
                    )
            ) {
                Row(modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 18.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    Column(modifier = Modifier
                        .weight(1f)
                    ) {
                        Text(
                            text = suggestion.titolo,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                        )
                        Text(
                            text = suggestion.relatori[0],
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontStyle = FontStyle.Italic,
                                fontSize = 20.sp
                            )
                        )

                        if(expanded) {
                            Text(
                                text = suggestion.descrizione,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                            )
                        }
                    }
                    Icon(
                        imageVector = if(expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "expand or reduce size",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { expanded = !expanded }
                    )
                }
            }
        }
    }
}

@Composable
fun GroupPhase2(
    username: String,
    userStatus: String,
    suggestions: List<GruppoAssemblea>,
    raiseFun: () -> Unit,
    changeUserStatus: (newStatus: String) -> Unit
) {
    val db = Firebase.firestore

    Text(
        text = "Iscrizione",
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        ),
        modifier = Modifier
            .padding(vertical = 12.dp)
    )

    if(userStatus.isNotBlank()) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 16.dp,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if(userStatus.substringAfter("£££") == "iscritto") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Sei iscritto al gruppo:\n${userStatus.substringBefore("£££")}",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .weight(1f)
                    )

                    Button(
                        onClick = {
                            db.document("/Eventi/Assemblea/Suggestions/${userStatus
                                .substringBefore("£££")}")
                                .update("iscritti", FieldValue.arrayRemove(username))
                            raiseFun()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove your application",
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = "Sei relatore del gruppo:\n${userStatus.substringBefore("£££")}",
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
    LazyColumn {
        items(items = suggestions) {suggestion ->
            var expanded by rememberSaveable { mutableStateOf(false) }

            Surface(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(32.dp),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(32.dp)
                    )
            ) {
                Row(modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 18.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    Column(modifier = Modifier
                        .weight(1f)
                    ) {
                        Text(
                            text = suggestion.titolo + "-${suggestion.iterazione}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                        )
                        Text(
                            text = suggestion.relatori[0],
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontStyle = FontStyle.Italic,
                                fontSize = 20.sp
                            )
                        )

                        if(expanded) {
                            Text(
                                text = suggestion.descrizione,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                            )

                            Text(
                                text = "Relatori:",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier
                                    .padding(top = 8.dp)
                            )

                            Text(suggestion.relatori.joinToString(separator = ", "))

                            Text(
                                text = "Iscritti:",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier
                                    .padding(top = 8.dp)
                            )

                            Text(suggestion.iscritti.joinToString(separator = ", "))


                            if(userStatus.isBlank() && suggestion.aperto) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                ) {
                                    Spacer(modifier = Modifier.weight(1f))

                                    if(suggestion.relatori.size < 2) {
                                        Button(
                                            onClick = {
                                                val update = mapOf("relatori" to (suggestion.relatori + username))
                                                db.document("/Eventi/Assemblea/Suggestions/" +
                                                        "${suggestion.titolo}-${suggestion.iterazione}")
                                                    .update(update)
                                                changeUserStatus("${suggestion.titolo}-${suggestion.iterazione}£££relatore")
                                            }) {
                                            Text("Diventa relatore")
                                        }
                                    } else if(suggestion.iscritti.size == 25) {
                                        Button(onClick = {
                                            db.document("Eventi/Assemblea/Suggestions/${suggestion.titolo}-${suggestion.iterazione}")
                                                .update("aperto", false)

                                            val update = GruppoAssemblea(
                                                titolo = suggestion.titolo,
                                                descrizione = suggestion.descrizione,
                                                relatori = listOf(username),
                                                timestamp = System.currentTimeMillis(),
                                                iterazione = suggestion.iterazione + 1
                                            )
                                            db.document("/Eventi/Assemblea/Suggestions/" +
                                                    "${update.titolo}-${update.iterazione}")
                                                .set(update)

                                            changeUserStatus(
                                                "${update.titolo}-${update.iterazione}£££relatore"
                                            )
                                        }) {
                                            Text("Crea nuovo gruppo")
                                        }
                                    } else {
                                        Button(onClick = {
                                            val update = mapOf("iscritti" to (suggestion.iscritti + username))
                                            db.document("/Eventi/Assemblea/Suggestions/${suggestion.titolo}-${suggestion.iterazione}")
                                                .update(update)
                                            changeUserStatus("${suggestion.titolo}-${suggestion.iterazione}£££iscritto")
                                        }) {
                                            Text("Iscriviti")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Icon(
                        imageVector = if(expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "expand or reduce size",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { expanded = !expanded }
                    )
                }
            }
        }
    }
}

@Composable
fun GroupPhase3(
    username: String,
    userStatus: String,
    suggestions: List<GruppoAssemblea>,
    changeUserStatus: (newStatus: String) -> Unit
) {
    val db = Firebase.firestore

    if(userStatus.isNotBlank()) {
        GroupPhase4(
            userStatus = userStatus,
            suggestions = suggestions
        )
    } else {
        Text(
            text = "Ultime iscrizioni",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            ),
            modifier = Modifier
                .padding(vertical = 12.dp)
        )

        LazyColumn {
            items(items = suggestions) {suggestion ->
                var expanded by rememberSaveable { mutableStateOf(false) }

                Surface(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(32.dp),
                    shadowElevation = 16.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(32.dp)
                        )
                ) {
                    Row(modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 18.dp)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    ) {
                        Column(modifier = Modifier
                            .weight(1f)
                        ) {
                            Text(
                                text = suggestion.titolo + "-${suggestion.iterazione}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                            )
                            Text(
                                text = suggestion.relatori[0],
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 20.sp
                                )
                            )

                            if(expanded) {
                                Text(
                                    text = suggestion.descrizione,
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                )

                                Text(
                                    text = "Relatori:",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                )

                                Text(suggestion.relatori.joinToString(separator = ", "))

                                Text(
                                    text = "Iscritti:",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                )

                                Text(suggestion.iscritti.joinToString(separator = ", "))

                                Text(
                                    text = "Classe:",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                )

                                Text(suggestion.classe)


                                if(suggestion.iscritti.size < suggestion.limiteMassimo) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                    ) {
                                        Spacer(modifier = Modifier.weight(1f))

                                        Button(onClick = {
                                            val update = mapOf("iscritti" to (suggestion.iscritti + username))
                                            db.document("/Eventi/Assemblea/Suggestions/${suggestion.titolo}-${suggestion.iterazione}")
                                                .update(update)
                                            changeUserStatus("${suggestion.titolo}-${suggestion.iterazione}£££iscritto")
                                        }) {
                                            Text("Iscriviti")
                                        }
                                    }
                                }
                            }
                        }
                        Icon(
                            imageVector = if(expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "expand or reduce size",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { expanded = !expanded }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupPhase4(
    userStatus: String,
    suggestions: List<GruppoAssemblea>,
) {
    val yourGroup = suggestions.find { it.titolo == userStatus.substringBefore("-") } !!
    Text(
        text = "La tua iscrizione",
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        ),
        modifier = Modifier
            .padding(vertical = 12.dp)
    )

    Surface(
        color = MaterialTheme.colorScheme.tertiary,
        shape = RoundedCornerShape(32.dp),
        shadowElevation = 16.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(32.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            Text(
                text = yourGroup.titolo + "-${yourGroup.iterazione}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
            )
            Text(
                text = "Relatori:",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
            )

            Text(yourGroup.relatori.joinToString(separator = ", "))

            Text(
                text = "Iscritti:",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
            )

            Text(yourGroup.iscritti.joinToString(separator = ", "))

            Text(
                text = "Classe:",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
            )

            Text(yourGroup.classe)

            Text(
                text = "Descrizione:",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
            )

            Text(yourGroup.descrizione)
        }
    }
}

@Composable
fun StateBall(
    eventStatus: Int,
    target: Int
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 16.dp,
    ) {
        Icon(
            imageVector = Icons.Default.Circle,
            contentDescription = "eventStatus",
            tint = if (eventStatus == target)
                MaterialTheme.colorScheme.tertiary else
                MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(28.dp)
        )
    }
}