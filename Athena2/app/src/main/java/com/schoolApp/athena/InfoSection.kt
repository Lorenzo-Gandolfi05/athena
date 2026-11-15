package com.schoolApp.athena

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun InfoSection(
    navController: NavHostController,
    auth: FirebaseAuth
) {
    var openPrivacy by remember { mutableStateOf(false) }
    var ensureQuit by remember { mutableStateOf(false) }
    var thanks by remember { mutableStateOf(false) }

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
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Settings Icon",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Text(
                        text = "Informazioni",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            item {
                var clickEnabled by remember { mutableStateOf(true) }
                ContainerBox(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    minModifier = Modifier
                        .clickable {
                            if(clickEnabled) {
                                clickEnabled = false
                                navController.navigate("accountSection")
                            }
                        }
                ) {
                    Text(
                        text = "Il tuo Account",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    )
                }
            }
            item {
                ContainerBox(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    minModifier = Modifier
                        .clickable {
                            openPrivacy = true
                        }
                ) {
                    Text(
                        text = "Informativa della privacy",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    )
                }
            }
            item {
                ContainerBox(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    minModifier = Modifier
                        .clickable {
                            thanks = true
                        }
                ) {
                    Text(
                        text = "Ringraziamenti",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    )
                }
            }
            item {
                ContainerBox(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    minModifier = Modifier
                        .clickable {
                            ensureQuit = true
                        }
                ) {
                    Text(
                        text = "Logout",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    )
                }
            }
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
                    }
                    ) {
                        Text(text = "Ho letto")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    if(thanks) {
        val people = listOf(
            "Luca Albiero", "Diego Placidi", "Filippo Bruni", "Gaia Tonolini", "Simone del Fiore",
            "Devinderpreet Kaur", "Haobo Hu", "Luca Marchini", "Elettra Fierro", "Francesco Ronca", "Sofia Fetz",
            "Solorzano Martinez Gerard Antoine", "Gioele Corcelli", "Samuele Ronca", "Chiara Campaniello",
            "Carlotta Guerra", "Lorenzo Fondrieschi Bagatta", "Dana Hassane", "Giacomo Perfetti", "Giordano Fioletti",
            "Valeria Vannazzi", "Lorenzo Baldassare", "Elisa Torosani", "Mirko Maragno", "Nikita Osokin", "Wenxin Xu"
        )
        Box(
            contentAlignment = Alignment.Center
        ) {
            ContainerBox(
                minModifier = Modifier
                    .width(312.dp)
                    .height(256.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "I nostri beta testers",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                            .padding(top = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .shadow(
                                elevation = 16.dp,
                                shape = CircleShape
                            )
                            .clickable { thanks = false }
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

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                    items(items = people) { name ->
                        Text(
                            text = name,
                            color = Color.White,
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }

    if(ensureQuit) {
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
                        text = "Vuoi effettuare il logout da Athena?",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                    )
                    Row {
                        Button(
                            onClick = { ensureQuit = false },
                            elevation = ButtonDefaults.elevatedButtonElevation(16.dp),
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                        ) {
                            Text("Annulla")
                        }

                        Button(
                            onClick = {
                                auth.signOut()
                            },
                            elevation = ButtonDefaults.elevatedButtonElevation(16.dp),
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                        ) {
                            Text("Conferma")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContainerBox(
    modifier: Modifier = Modifier,
    minModifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiary,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Box(
        modifier = modifier
            .shadow(elevation = 16.dp)
    ) {
        Column(
            modifier = minModifier
                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .background(color)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            content()
        }
    }
}