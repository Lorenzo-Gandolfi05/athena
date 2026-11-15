package com.schoolApp.athena

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Composable
fun MarketplaceMain(
    offers: List<MarketOffer>,
    username: String,
    hisClass: String,
    uid: String,
    school: String,
    db: FirebaseFirestore,
    navController: NavHostController,
    liftOffers: (newOffers: List<MarketOffer>) -> Unit,
    liftOffer: (newOffer: MarketOffer) -> Unit

) {
    var noOffer by rememberSaveable { mutableStateOf(true) }

    db.collection("Scuole").document(school).collection("Marketplace").get()
        .addOnSuccessListener { docs->
            val newOffers = docs.documents.mapNotNull { document ->
                document.toObject(MarketOffer::class.java)
            }
            liftOffers(newOffers)
        }

    Box {
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
            var offerFilter by remember { mutableStateOf("") }
            Insertable(
                info = offerFilter,
                message = "Cerca un'offerta",
                textChange = { offerFilter = it },
                modifier = Modifier
                    .padding(top = 8.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f)
            ) {
                items(items = filterOffer(offers, offerFilter)) { offer ->
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
                                if (noOffer) {
                                    liftOffer(offer)
                                    navController.navigate("marketplaceProductPage")
                                }
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

        Box(
            modifier = Modifier
                .animateContentSize()
                .padding(12.dp)
                .wrapContentSize()
                .align(alignment = Alignment.BottomStart)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Add an offer",
                tint = Color.White,
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate("marketplacePersonal")
                    }
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(12.dp)
                    .size(36.dp)
            )
        }

        Box(
            modifier = Modifier
                .animateContentSize()
                .padding(12.dp)
                .wrapContentSize()
                .align(alignment = if (noOffer) Alignment.BottomEnd else Alignment.Center)
        ) {
            if (noOffer) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add an offer",
                    tint = Color.White,
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable { noOffer = false }
                        .background(MaterialTheme.colorScheme.tertiary)
                        .padding(12.dp)
                        .size(36.dp)
                )
            } else {
                val context = LocalContext.current
                var loading by rememberSaveable { mutableStateOf(false) }

                var item by remember { mutableStateOf("") }
                var price by remember { mutableStateOf("") }
                var contact by remember { mutableStateOf("") }
                var used by remember { mutableStateOf(true) }
                var url by remember { mutableStateOf("") }
                val id = UUID.randomUUID().toString()

                var urlUpload by remember { mutableStateOf("") }

                var erItem by remember { mutableStateOf(false) }
                var erPrice by remember { mutableStateOf(false) }
                var erContact by remember { mutableStateOf(false) }

                val imagePickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri?->
                    uri?.let {
                        url = uri.toString()
                    }
                }

                Column(
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .padding(horizontal = 12.dp)
                        .padding(top = 20.dp, bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Crea un'offerta",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close dial",
                                tint = Color.White,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { noOffer = true }
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(4.dp)
                            )
                        }
                    }
                    Insertable(
                        info = item,
                        message = "Oggetto",
                        textChange = { item = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if(erItem) {
                        ErrorMess(text = "Dai un titolo all'offerta!")
                    }

                    val pricePattern = Regex("^\\d{0,7}(,\\d{0,2})?\$")
                    TextField(
                        value = price,
                        onValueChange = { input->
                            if(pricePattern.matches(input)) {
                                price = input
                            }
                                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(28.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        label = { Text(
                            text = "Prezzo",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Italic
                        ) },
                        modifier = Modifier
                            .fillMaxWidth()
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

                    if(erPrice) {
                        ErrorMess(text = "Inserisci il prezzo!")
                    }

                    val instaPattern = Regex("^[a-zA-Z0-9._]{1,30}\$")
                    Insertable(
                        info = contact,
                        message = "Instagram",
                        textChange = { input->
                            if(instaPattern.matches(input)) {
                                contact = input
                            }
                                     },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if(erContact) {
                        ErrorMess(text = "Dai un instagram per contattarti!")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = "Usato:",
                            color = Color.White
                        )
                        Checkbox(
                            checked = used,
                            onCheckedChange = {
                                used = it
                            }
                        )
                    }
                    if(url.isEmpty()) {
                        Image(
                            painter = painterResource(id = R.drawable.placeholder_image),
                            contentDescription = "Placeholder image",
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .aspectRatio(3f / 2f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { imagePickerLauncher.launch("image/*") }
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(url.toUri()),
                            contentDescription = "Loaded image",
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .aspectRatio(3f / 2f)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                    if(loading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .align(alignment = Alignment.CenterHorizontally)
                        )
                    } else {
                        Button(
                            onClick = {
                                erItem = false
                                erPrice = false
                                erContact = false
                                if (item.isEmpty()) {
                                    erItem = true
                                } else if (price.isEmpty()) {
                                    erPrice = true
                                } else if (contact.isEmpty()) {
                                    erContact = false
                                } else {
                                    loading = true
                                    if (url.isEmpty()) {
                                        val offer = MarketOffer(
                                            seller = "$username, $hisClass",
                                            item = item,
                                            imageUri = "",
                                            price = price,
                                            contact = contact,
                                            used = used,
                                            uid = uid,
                                            id = id
                                        )
                                        liftOffers(offers + offer)
                                        db.collection("Scuole")
                                            .document(school)
                                            .collection("Marketplace")
                                            .document(id)
                                            .set(offer)
                                        noOffer = true
                                    } else {
                                        val storageRef = FirebaseStorage.getInstance().reference
                                            .child("$school/Marketplace/$id")

                                        val inputStream = context.contentResolver.openInputStream(url.toUri())
                                        val bitmap = BitmapFactory.decodeStream(inputStream)
                                        val compressedFile = File
                                            .createTempFile("tempImage", ".jpg", context.cacheDir)
                                        FileOutputStream(compressedFile).use { out ->
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out)
                                        }

                                        storageRef.putFile(Uri.fromFile(compressedFile))
                                            .addOnSuccessListener {
                                                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                                    urlUpload = downloadUrl.toString()
                                                    val offer = MarketOffer(
                                                        seller = "$username, $hisClass",
                                                        item = item,
                                                        imageUri = urlUpload,
                                                        price = price,
                                                        contact = contact,
                                                        used = used,
                                                        uid = uid,
                                                        id = id
                                                    )
                                                    liftOffers(offers + offer)
                                                    db.collection("Scuole")
                                                        .document(school)
                                                        .collection("Marketplace")
                                                        .document(id)
                                                        .set(offer)
                                                    noOffer = true
                                                }
                                            }
                                            .addOnFailureListener {
                                                loading = false
                                            }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 12.dp)
                        ) {
                            Text(text = "Carica")
                        }
                    }
                }
            }
        }
    }
}

fun filterOffer(
    parsingList: List<MarketOffer>,
    keyword: String
): List<MarketOffer> {
    val dumList = mutableListOf<MarketOffer>()
    for (element in parsingList) {
        if (keyword in element.item || keyword.isEmpty()) {
            dumList.add(element)
        }
    }
    return dumList
}