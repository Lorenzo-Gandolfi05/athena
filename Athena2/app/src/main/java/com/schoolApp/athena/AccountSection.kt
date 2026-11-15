package com.schoolApp.athena

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

@Composable
fun AccountSection(
    username: String,
    hisClass: String,
    email: String,
    navController: NavHostController,
    doToast: (message: String) -> Unit
) {
    var accountDeletion by remember { mutableStateOf(false) }
    val db = Firebase.firestore
    val uid = FirebaseAuth.getInstance().uid ?: ""

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .shadow(elevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
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
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Settings Icon",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Text(
                        text = "Il tuo Account",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Surface(
            color = MaterialTheme.colorScheme.tertiary,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 4.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column {
                DoubleText(text1 = "Nome Utente: ", text2 = username, extraPadStart = true)

                DoubleText(text1 = "Classe: ", text2 = hisClass)

                DoubleText(text1 = "Email: ", text2 = email, extraPadEnd = true)
            }
        }

        ContainerBox(
            modifier = Modifier
                .clickable {
                    accountDeletion = true
                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Elimina il tuo Account",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            )
        }
    }
    if(accountDeletion) {
        var accountMail by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Box(contentAlignment = Alignment.Center) {
            Surface(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Elimina il tuo account",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 4.dp)
                    )
                    Insertable(
                        info = accountMail,
                        message = "Inserisci la mail",
                        textChange = { accountMail = it }
                    )
                    Insertable(
                        info = password,
                        message = "Inserisci la password",
                        textChange = { password = it }
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                val account = FirebaseAuth.getInstance().currentUser
                                if(accountMail.isNotBlank() && password.isNotBlank()) {
                                    account?.let {
                                        val credentials = EmailAuthProvider.getCredential(accountMail, password)
                                        account.reauthenticate(credentials).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                db.collection("/Users/$uid/SavedInfo").get()
                                                    .addOnSuccessListener { documents ->
                                                        for (document in documents) {
                                                            document.reference.delete()
                                                        }
                                                    }
                                                db.document("/Users/$uid").delete()

                                                db.document("/Eventi/StatusControl")
                                                    .update(username, FieldValue.delete())

                                                account.delete().addOnCompleteListener { deleteTask ->
                                                    if (deleteTask.isSuccessful) {
                                                        doToast("Account eliminato con successo")
                                                    } else {
                                                        doToast("Eliminazione account fallita")
                                                    }
                                                }
                                            } else {
                                                doToast("Credenziali errate")
                                            }
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        ) {
                            Text("Elimina account")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Box {
                    Row(modifier = Modifier.padding(top = 12.dp, end = 12.dp)) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "cancel",
                            modifier = Modifier
                                .clickable { accountDeletion = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoubleText(
    text1: String,
    text2: String,
    extraPadStart: Boolean = false,
    extraPadEnd: Boolean = false
) {
    Row {
        Text(
            text = text1,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = 16.dp)
                .padding(top = if (extraPadStart) 12.dp else 4.dp, bottom = if (extraPadEnd) 12.dp else 4.dp)
        )

        Text(
            text = text2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(end = 16.dp)
                .padding(top = if (extraPadStart) 12.dp else 4.dp, bottom = if (extraPadEnd) 12.dp else 4.dp)
        )
    }
}