package com.schoolApp.athena

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun MarketplacePersonal(
    offers: List<MarketOffer>,
    uid: String,
    navController: NavHostController,
    liftOffer: (newOffer: MarketOffer) -> Unit
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Settings Icon",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Text(
                        text = "Le Tue Offerte",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f)
        ) {
            items(items = myOffers(offers, uid)) { offer ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            liftOffer(offer)
                            navController.navigate("marketplaceProductPage")
                        }
                ) {
                    Column {
                        if (offer.imageUri.isEmpty()) {
                            Image(
                                painter = painterResource(id = R.drawable.placeholder_image),
                                contentDescription = "Placeholder image",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .aspectRatio(3f / 2f)
                            )
                        } else {
                            AsyncImage(
                                model = offer.imageUri,
                                contentDescription = "offer image",
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .aspectRatio(3f / 2f)
                            )
                        }
                        Divider(
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.tertiary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = offer.item,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                            )
                            Text(
                                text = "Prezzo: €${offer.price}",
                                color = Color.White,
                                modifier = Modifier
                                    .padding(top = 4.dp, bottom = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun myOffers(
    parsingList: List<MarketOffer>,
    keyword: String
): List<MarketOffer> {
    val dumList = mutableListOf<MarketOffer>()
    for (element in parsingList) {
        if (keyword in element.uid) {
            dumList.add(element)
        }
    }
    return dumList
}