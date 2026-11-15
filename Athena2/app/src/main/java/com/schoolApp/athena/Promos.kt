package com.schoolApp.athena

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun Promos(
    promozioni: List<Promozione>,
    navController: NavHostController
) {
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
                        contentDescription = "Main Menu Icon",
                        tint = Color.White,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable { navController.popBackStack() }
                    )
                    Text(
                        text = "Promozioni",
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
            items(items = promozioni) { deal ->
                val uriHandler = LocalUriHandler.current

                Surface(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp)
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
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(
                                    text = deal.businessName,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                )
                                Text(
                                    text = deal.addressText,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontStyle = FontStyle.Italic,
                                        textDecoration = TextDecoration.Underline,
                                        fontSize = 14.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .clickable {
                                            uriHandler
                                                .openUri(deal.addressUrl)
                                        }
                                )
                                Text(
                                    text = deal.promotion,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .width(64.dp)
                                    .height(64.dp)
                            ) {
                                AsyncImage(
                                    model = deal.photoUrl,
                                    contentDescription = "business logo",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(32.dp))
                                )
                            }
                        }
                        if(deal.menuButtons.isNotEmpty()) {
                            Row {
                                Spacer(modifier = Modifier.weight(1f))
                                for(button in deal.menuButtons.keys) {
                                    Button(
                                        onClick = { uriHandler.openUri(deal.menuButtons[button] ?: "") },
                                        elevation = ButtonDefaults.elevatedButtonElevation(16.dp),
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp)
                                        ) {
                                        Text(text = button)
                                    }
                                }
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}