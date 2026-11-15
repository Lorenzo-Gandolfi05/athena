package com.schoolApp.athena

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun MarketplaceProductPage(
    offer: MarketOffer,
    school: String,
    uid: String,
    db: FirebaseFirestore,
    navController: NavHostController,
    liftOffers: (newOffers: MarketOffer) -> Unit
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
                        text = "Marketplace",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Text(
            text = offer.item,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp, bottom = 16.dp)
        )
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
        Text(
            text = offer.seller,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 4.dp)
        )
        Text(
            text = "€${offer.price}; ${if(offer.used) "Usato" else "Nuovo"}",
            fontSize = 18.sp,
            modifier = Modifier
                .padding(top = 4.dp)
        )

        Box(
            modifier = Modifier
                .padding(12.dp)
        ) {
            val uriHandler = LocalUriHandler.current
            if (offer.uid == uid) {
                Text(
                    text = "Elimina l'offerta",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .clickable {
                            if(offer.imageUri.isNotEmpty()) {
                                val storageRef = FirebaseStorage.getInstance().reference
                                    .child(school)
                                    .child("Marketplace")
                                    .child(offer.id)
                                storageRef.delete()
                                    .addOnSuccessListener {
                                        db.collection("Scuole")
                                            .document(school)
                                            .collection("Marketplace")
                                            .document(offer.id)
                                            .delete()
                                            .addOnSuccessListener {
                                                liftOffers(offer)
                                                navController.popBackStack()
                                            }
                                    }
                            } else {
                                db.collection("Scuole")
                                    .document(school)
                                    .collection("Marketplace")
                                    .document(offer.id)
                                    .delete()
                                    .addOnSuccessListener {
                                        liftOffers(offer)
                                        navController.popBackStack()
                                    }
                            }
                        }
                        .padding(12.dp)
                )
            } else {
                Text(
                    text = "Contatta il venditore",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            navController.navigate("marketplacePersonal")
                        }
                        .background(MaterialTheme.colorScheme.tertiary)
                        .clickable {
                            uriHandler.openUri("https://www.instagram.com/${offer.contact}/")
                        }
                        .padding(12.dp)
                )
            }
        }
    }
}