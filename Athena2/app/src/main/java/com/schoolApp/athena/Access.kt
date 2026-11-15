package com.schoolApp.athena

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

@Composable
fun Access(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    showToast: (message: String) -> Unit
) {
    var logScreen by remember { mutableStateOf(true) }
    var openPrivacy by remember { mutableStateOf(false) }
    var privacyRead by remember { mutableStateOf(false) }
    var pickedSchool by remember { mutableStateOf("") }

    var mailFormat by remember { mutableStateOf(mapOf<String, String>())}

    db.collection("Scuole").document("ElencoScuole")
        .get()
        .addOnSuccessListener { doc->
            mailFormat = doc.get("mailFormat") as Map<String, String>
        }

    val imeHandler by keyboardAsState()
    val imeDp = with(LocalDensity.current) { (imeHandler[1] / 4).toDp() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset(0.dp, -imeDp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 32.dp,
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                if (logScreen) {
                    Login(
                        auth = auth,
                    ) { showToast(it) }
                } else if(pickedSchool == "") {
                    SchoolPicker(
                        mailFormat = mailFormat
                    ) {
                        pickedSchool = it
                    }
                } else {
                    Register(
                        auth = auth,
                        db = db,
                        pickedSchool = pickedSchool,
                        mailFormat = mailFormat,
                        privacyRead = privacyRead,
                        showToast = { showToast(it) }
                    ) {
                        openPrivacy = true
                    }
                }
            }
            Button(
                onClick = {
                    pickedSchool = ""
                    logScreen = !logScreen
                    privacyRead = false
                          },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Text(text = if (logScreen) "Non ho un account" else "Ho già un account")
            }
        }
        if(openPrivacy) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(color = MaterialTheme.colorScheme.tertiary)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                item {
                    val text1 = R.string.terms_of_service_1
                    Text(
                        text = stringResource(text1),
                        color = Color.White
                    )
                }
                item {
                    val text2 = R.string.terms_of_service_2
                    val uriHandler = LocalUriHandler.current
                    Text(
                        text = stringResource(text2),
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable {
                                uriHandler
                                    .openUri("https://policies.google.com/privacy?hl=it")
                            }
                    )
                }
                item {
                    val text3 = R.string.terms_of_service_3
                    val uriHandler = LocalUriHandler.current
                    Text(
                        text = stringResource(text3),
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable {
                                uriHandler
                                    .openUri("https://firebase.google.com/support/privacy?hl=it#firebase_support_for_gdpr_and_ccpa")
                            }
                    )
                }
                item {
                    val text4 = R.string.terms_of_service_4
                    Text(
                        text = stringResource(text4),
                        color = Color.White,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )
                }
                item {
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = {
                            openPrivacy = false
                            privacyRead = true
                        }
                        ) {
                            Text(text = "Ho letto")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun Login(
    auth: FirebaseAuth,
    showToast: (message: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorE by remember { mutableStateOf(false) }
    var errorP by remember { mutableStateOf(false) }
    var errorW by remember { mutableStateOf(false) }

    Column {
        Text(text = "Login",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(horizontal = 20.dp)
        )

        Insertable(email, "Email", textChange = { email = it }, modifier = Modifier.fillMaxWidth())

        if(errorE) { ErrorMess(text = "Inserire la mail") }

        Pwordbox(password = password, message = "Password", textChange = { password = it })

        if(errorP) { ErrorMess(text = "La password deve contenere almeno 8 caratteri") }

        if(errorW) { ErrorMess("Qualcosa è andato storto") }

        ElevatedButton(
            onClick = {
                errorE = false
                errorP = false
                errorW = false

                if ( email == "" ) {
                    errorE = true
                } else if ( password.length < 8 ) {
                    errorP = true
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d(TAG, "signInWithEmail:success")
                            } else {
                                errorW = true
                            }
                        }
                }
            },
            elevation = ButtonDefaults.elevatedButtonElevation(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .padding(top = 10.dp, bottom = 20.dp)
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Accedi")
        }
    }
}

@Composable
fun SchoolPicker(
    mailFormat: Map<String, String>,
    picked: (choice: String) -> Unit,
) {
    val schoolsList = mailFormat.keys.toList()
    var chosenSchool by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .height(324.dp)
    ) {
        Text(text = "Crea Account",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(horizontal = 20.dp)
        )

        Insertable(
            info = chosenSchool,
            message = "Scegli la tua scuola",
            textChange = { chosenSchool = it },
            modifier = Modifier
                .fillMaxWidth()
        )

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 64.dp)
        ) {
            items(items = filterString(schoolsList, chosenSchool)) { school->
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 16.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { picked(school) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = school,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            modifier = Modifier
                                .align(alignment = Alignment.Center)
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Register(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    pickedSchool: String,
    mailFormat: Map<String, String>,
    privacyRead: Boolean,
    showToast: (message: String) -> Unit,
    openPrivacy: () -> Unit
) {
    val schoolFormat = mailFormat[pickedSchool] ?: ""
    var email by remember { mutableStateOf("") }
    var hisClass by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorRead by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(false) }

    var errorE by remember { mutableStateOf(false) }
    var errorC by remember { mutableStateOf(false) }
    var errorP by remember { mutableStateOf(false) }
    var errorW by remember { mutableStateOf(false) }

    var classScreen by remember { mutableStateOf(false) }

    var planimetria by remember { mutableStateOf(listOf<Planimetria>()) }

    db.collection("Scuole").document(pickedSchool).collection("Planimetria")
        .orderBy("classe", Query.Direction.ASCENDING)
        .get()
        .addOnSuccessListener { documents ->
            planimetria = documents.map { document ->
                document.toObject(Planimetria::class.java)
            }
        }

    Column {
        Text(
            text = "Crea Account",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(horizontal = 20.dp)
        )

        TextField(
            value = email,
            onValueChange = {
                if("a$it".last() == '@') email += schoolFormat else email = it
                            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            label = { Text(
                text = "cognome.nome$schoolFormat",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Italic
            ) },
            trailingIcon = {
                if (hisClass.isBlank()) {
                    Icon(
                        imageVector = Icons.Default.Room,
                        contentDescription = "Scegli una classe",
                        tint = Color.Black,
                        modifier = Modifier
                            .clickable { classScreen = !classScreen }
                            .padding(end = 20.dp)
                            .size(32.dp)
                    )
                } else {
                    Text(
                        text = hisClass,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clickable { classScreen = !classScreen }
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp)
                .padding(horizontal = 20.dp)
                .height(56.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(28.dp)
                )
        )

        if (errorE) {
            ErrorMess(text = "Format email errato: cognome.nome$schoolFormat")
        }

        if (errorC) {
            ErrorMess(text = "Scegliere una classe")
        }

        Pwordbox(password = password, message = "Password", textChange = { password = it })

        if (errorP) {
            ErrorMess(text = "La password deve contenere almeno 8 caratteri")
        }

        if (errorW) {
            ErrorMess(text = "Qualcosa è andato storto")
        }

        Row {
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    if(privacyRead) {
                        checked = it
                    } else {
                        errorRead = true
                    }
                }
            )

            Text(
                text = "Creando un profilo, accetti l'informativa della privacy",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable { openPrivacy() }
            )
        }

        if(errorRead && !privacyRead) {
            ErrorMess(text = "prima leggi e poi accetta l'informativa!")
        }

        var clickEnabled by remember { mutableStateOf(true) }
        ElevatedButton(
            onClick = {
                if(clickEnabled) {
                    errorE = false
                    errorC = false
                    errorP = false
                    errorW = false

                    if (schoolFormat !in email || "." !in email.substringBefore("@")) {
                        errorE = true
                    } else if (password.length < 8) {
                        errorP = true
                    } else if (hisClass.isBlank()) {
                        errorC = true
                    } else if (!checked) {
                        errorRead = true
                    } else {
                        clickEnabled = false
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { creation ->
                                if (creation.isSuccessful) {
                                    val uid = FirebaseAuth.getInstance().uid ?: ""
                                    val fullName = email.substringBefore("@")
                                    val name = fullName.substringAfter(".")
                                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                    val surname = fullName.substringBefore(".")
                                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                    val username = "$name $surname"
                                    val info = hashMapOf(
                                        "username" to username,
                                        "email" to email,
                                        "hisClass" to hisClass,
                                        "school" to pickedSchool,
                                        "privacyAccepted" to true
                                    )
                                    db.collection("/Users")
                                        .document(uid)
                                        .set(info)
                                    db.document("/Users/$uid")
                                    showToast("Account creato con successo!")
                                } else {
                                    errorW = true
                                    clickEnabled = true
                                }
                            }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(16.dp),
            modifier = Modifier
                .padding(top = 10.dp, bottom = 20.dp)
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Crea Account")
        }
    }

    if (classScreen) {
        var erMess by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .size(256.dp, 384.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var searchClass by remember { mutableStateOf(hisClass) }

                    TextField(
                        value = searchClass,
                        onValueChange = { searchClass = it },
                        singleLine = true,
                        shape = RoundedCornerShape(28.dp),
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
                            .padding(bottom = 4.dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(28.dp)
                            )
                            .width(168.dp)
                            .height(56.dp)
                    )
                    if (erMess) {
                        Text(
                            text = "Classe non valida!",
                            color = Color.Red,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                        )
                    }
                    LazyColumn {
                        items(items = filter(planimetria, searchClass)) { classe ->
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shadowElevation = 16.dp,
                                shape = RoundedCornerShape(15.dp),
                                modifier = Modifier
                                    .clickable { searchClass = classe.classe }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = classe.classe,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Divider(
                        thickness = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Button(onClick = {
                        if (searchClass in planimetria.map { it.classe }) {
                            hisClass = searchClass
                            classScreen = false
                        } else {
                            erMess = true
                        }
                    }) {
                        Text(
                            text = "Conferma"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Insertable(
    info: String,
    message: String,
    textChange: (newText: String) -> Unit,
    modifier: Modifier = Modifier,
    doSingleLine: Boolean = true
) {
    TextField(
        value = info,
        onValueChange = { textChange(it) },
        singleLine = doSingleLine,
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        label = { Text(
            text = message,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Italic
        ) },
        modifier = modifier
            .padding(top = 10.dp, bottom = 10.dp)
            .padding(horizontal = 20.dp)
            .width(288.dp)
            .height(56.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(28.dp)
            )
    )
}

@Composable
fun Pwordbox(
    password: String,
    message: String,
    textChange: (newText: String) -> Unit
) {
    var hidepas by remember { mutableStateOf(true) }
    val eyeIcon: ImageVector = if (hidepas) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = password,
            onValueChange = { textChange(it) },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            label = {
                Text(
                    text = message,
                    fontWeight = FontWeight.Normal,
                    fontStyle = FontStyle.Italic
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = eyeIcon,
                    contentDescription = if (hidepas) "Nascondi password" else "Mostra password",
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .size(32.dp)
                        .clickable { hidepas = !hidepas }
                )
            },
            visualTransformation = if (hidepas) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .width(288.dp)
                .height(56.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(28.dp)
                )
        )



    }
}

@Composable
fun ErrorMess(
    text: String
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.errorContainer,
        fontSize = 14.sp,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .padding(horizontal = 20.dp)
    )
}

fun filter(
    parsingList: List<Planimetria>,
    keyWord: String,
    doClass: Boolean = true
): List<Planimetria> {
    val dumList = mutableListOf<Planimetria>()
    if(doClass) {
        for(element in parsingList) {
            if(keyWord in element.classe) {
                dumList.add(element)
            }
        }
    } else {
        for(element in parsingList) {
            if(keyWord in element.stanza.toString()) {
                dumList.add(element)
            }
        }
        val finalList = dumList.sortedBy {it.stanza}
        return finalList
    }
    return dumList
}

fun filterString(
    parsingList: List<String>,
    keyword: String
): List<String> {
    val dumList = mutableListOf<String>()
    for (element in parsingList) {
        if (keyword in element || keyword.isEmpty()) {
            dumList.add(element)
        }
    }
    return dumList
}