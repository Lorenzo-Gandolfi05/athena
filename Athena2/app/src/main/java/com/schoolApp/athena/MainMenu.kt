package com.schoolApp.athena

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MainMenu(
    instagram: String,
    instagramUrl: String,
    GPlink: String,
    eventStatus: Int,
    privacyAccepted: Boolean,
    db: FirebaseFirestore,
    uid: String,
    currentVersionServer: String,
    navController: NavHostController,
    acceptPrivacy: () -> Unit
) {
    val currentVersionLocal by remember { mutableStateOf("1.1.1") }
    var clickEnabled by remember { mutableStateOf(true) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = MaterialTheme.colorScheme.tertiary,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Menù Principale",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(onClick = { if(clickEnabled) {
                    clickEnabled = false
                    navController.navigate("info")
                } }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier
                .weight(1f)
        ) {
            item {
                AppIcon(
                    newScreen = "calendar",
                    icon = Icons.Default.CalendarToday,
                    description = "Move to Calendar",
                    bottomText = "Calendario",
                    clickEnabled = clickEnabled
                ) {
                    clickEnabled = false
                    navController.navigate(it)
                }
            }

            item {
                AppIcon(
                    newScreen = "appMaps",
                    icon = Icons.Default.Map,
                    description = "Move to Map",
                    bottomText = "Piantina",
                    clickEnabled = clickEnabled
                ) {
                    clickEnabled = false
                    navController.navigate(it)
                }
            }

            item {
                AppIcon(
                    newScreen = "promos",
                    icon = Icons.Default.Discount,
                    description = "discounts",
                    bottomText = "Promozioni",
                    clickEnabled = clickEnabled,
                ) {
                    clickEnabled = false
                    navController.navigate(it)
                }
            }

            item {
                AppIcon(
                    newScreen = "utilities",
                    icon = Icons.Default.Apps,
                    description = "Find target grades and other",
                    bottomText = "Strumenti",
                    clickEnabled = clickEnabled
                ) {
                    clickEnabled = false
                    navController.navigate(it)
                }
            }

            item {
                AppIcon(
                    newScreen = "marketplace",
                    icon = Icons.Default.Storefront,
                    description = "Move to Marketplace",
                    bottomText = "Marketplace",
                    clickEnabled = clickEnabled
                ) {
                    clickEnabled = false
                    navController.navigate(it)
                }
            }

            if(eventStatus >= 0) {
                item {
                    if (eventStatus != 0) {
                        AppIcon(
                            newScreen = "assemblea",
                            icon = Icons.Default.Flag,
                            description = "Move to Events",
                            bottomText = "Assemblea",
                            clickEnabled = clickEnabled
                        ) {
                            clickEnabled = false
                            navController.navigate(it)
                        }
                    } else {
                        AppIcon(
                            newScreen = "",
                            icon = Icons.Default.QuestionMark,
                            description = "A whole bunch of nothing",
                            bottomText = "In arrivo",
                            clickEnabled = clickEnabled
                        ) { }
                    }
                }
            }
        }
        /*
        Row(
            modifier = Modifier
                .padding(bottom = 36.dp)
        ) {
            val uriHandler = LocalUriHandler.current
            Text(
                text = "Seguici su instagram a ",
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                )
            Text(
                text = instagram,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable { uriHandler.openUri(instagramUrl) }
            )
        }
         */
    }
    if(currentVersionLocal != currentVersionServer && currentVersionServer != "" && currentVersionServer != "1.1.0") {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.6f))
                .fillMaxSize()
                .clickable {}
        ) {
            Surface(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(384.dp, 256.dp)
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
                    Text(
                        text = "Nuovo Aggiornamento",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                    )
                    Text(
                        text = "E' disponibile un nuovo aggiornamento di Athena.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    val uriHandler = LocalUriHandler.current
                    Text(
                        text = "Scaricalo ora su Google Play",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .clickable { uriHandler.openUri(GPlink) }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
    if(!privacyAccepted) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {  }
                .padding(12.dp)
                //.wrapContentSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(12.dp)
                    .padding(top = 12.dp)
                    .align(alignment = Alignment.Center)
            ) {
                Text(
                    text = "Aggiornamento privacy policy",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "Abbiamo aggiornato la normativa della privacy per accomodare l'utilizzo del marketplace" +
                            " da parte di voi utenti.",
                    color = Color.White,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )
                val uriHandler = LocalUriHandler.current
                Text(
                    text = "Testo integrale",
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable {
                            uriHandler
                                .openUri("https://lorenzo-gandolfi-dev.github.io/Athena-privacy-policy/")
                        }
                )
                Button(onClick = {
                    db.collection("Users").document(uid)
                        .update("privacyAccepted", true)
                        .addOnSuccessListener {
                            acceptPrivacy()
                        }
                },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Accetto"
                    )
                }
            }
        }
    }
}



@Composable
fun AppIcon(
    newScreen: String,
    icon: ImageVector,
    description: String,
    bottomText: String,
    clickEnabled: Boolean,
    moveTo: (screen: String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.tertiary,
        shadowElevation = 16.dp,
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .size(98.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                if (clickEnabled) {
                    moveTo(newScreen)
                }
            }

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = Color.Black,
                modifier = Modifier
                    .size(48.dp)

            )
            Text(
                text = bottomText,
                fontSize = 16.sp
            )
        }
    }
}