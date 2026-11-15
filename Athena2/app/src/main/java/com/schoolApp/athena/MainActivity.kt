package com.schoolApp.athena

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.schoolApp.athena.ui.theme.AthenaTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContent {
            AthenaTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    StateCheck(auth = auth)
                }
            }
        }
    }
}

@Composable
fun StateCheck(
    auth: FirebaseAuth
) {
    var message by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }

    if(showToast) {
        ShowToast(message = message)
        showToast = false
    }

    val db = Firebase.firestore

    var planimetria by remember { mutableStateOf(listOf<Planimetria>()) }

    db.collection("Planimetria")
        .orderBy("classe", Query.Direction.ASCENDING)
        .get()
        .addOnSuccessListener { documents ->
            planimetria = documents.map { document ->
                document.toObject(Planimetria::class.java)
            }
        }

    var userStatus by remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            userStatus = auth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }

    if(userStatus == null) {
        Access(
            auth = auth,
            db = db,
        ) {
            message = it
            showToast = true
        }
    } else {
        MainApp(
            auth = auth,
            db = db,
        ) {
            message = it
            showToast = true
        }
    }
}

@Composable
fun MainApp(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    showToast: (message: String) -> Unit
) {
    val uid = FirebaseAuth.getInstance().uid ?: ""

    var currentVersionServer by remember { mutableStateOf("") }
    var GPlink by remember { mutableStateOf("") }
    var instagram by remember { mutableStateOf("") }
    var instagramUrl by remember { mutableStateOf("") }

    // User Data
    var username by remember { mutableStateOf("" ) }
    var hisClass by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var privacyAccepted by remember { mutableStateOf(true) }

    // Calendar Data
    var orario by remember { mutableStateOf(mapOf<String, List<String>>()) }
    var overrides by remember { mutableStateOf(mapOf<String, String>()) }
    var totNotes by remember { mutableStateOf(hashMapOf<String, List<String>>())}
    val preferences = remember { mutableStateOf(hashMapOf<String, Long>())}

    // Assemblea Data
    var eventStatus by remember { mutableIntStateOf(0) }
    var userStatus by remember { mutableStateOf("")}

    // AppMaps Data
    val cartine = mapOf(
        "Villa" to R.drawable.villa,
        "Piano Terra" to R.drawable.piano_terra, // Replace with your actual drawable names
        "Primo Piano" to R.drawable.primo_piano,
        "Secondo Piano" to R.drawable.secondo_piano
    )
    var planimetria by remember { mutableStateOf(listOf<Planimetria>()) }

    // Marketplace data
    var offers by remember { mutableStateOf(listOf<MarketOffer>())}
    var offer by remember { mutableStateOf(MarketOffer())}

    // Promos Data
    var promozioni by remember { mutableStateOf(listOf<Promozione>()) }

    var goThrough by remember { mutableStateOf(false) }

    db.document("/AppInfo/Version").get()
        .addOnSuccessListener { doc ->
            currentVersionServer = doc.getString("currentVersionAndroid") ?: ""
            GPlink = doc.getString("GPlink") ?: ""
            instagram = doc.getString("instagram") ?: ""
            instagramUrl = doc.getString("instagramUrl") ?: ""
        }

    db.document("/Users/$uid").get()
        .addOnSuccessListener { document ->
            if (document != null) {

                username = document.getString("username") ?: ""
                hisClass = document.getString("hisClass") ?: ""
                email = document.getString("email") ?: ""
                school = document.getString("school") ?: ""
                privacyAccepted = document.getBoolean("privacyAccepted") ?: false

                val docIds = listOf(hisClass, "Overrides")
                val schoolId = db.collection("Scuole").document(school)

                schoolId.collection("Orario").whereIn(FieldPath.documentId(), docIds).get()
                    .addOnSuccessListener { documents ->
                        for (element in documents) {
                            when (element.id) {
                                hisClass -> orario = element.data as HashMap<String, List<String>>
                                "Overrides" -> overrides = element.data as HashMap<String, String>
                            }
                        }

                        val docIds2 = listOf("Notes", "Preferences")

                        db.collection("Users/$uid/SavedInfo").whereIn(FieldPath.documentId(), docIds2).get()
                            .addOnSuccessListener { documents2 ->
                                for(element in documents2) {
                                    when (element.id) {
                                        "Notes" -> totNotes = element.data as HashMap<String, List<String>>
                                        "Preferences" -> preferences.value = element.data as HashMap<String, Long>
                                    }
                                }
                                goThrough = true
                            }


                        schoolId.collection("Planimetria")
                            .orderBy("classe", Query.Direction.ASCENDING)
                            .get()
                            .addOnSuccessListener { docs ->
                                planimetria = docs.map { document ->
                                    document.toObject(Planimetria::class.java)
                                }
                            }

                        schoolId.collection("Assemblea").document("StatusControl").get()
                            .addOnSuccessListener { doc ->
                                eventStatus = (doc.getLong("eventStatus")?: 0).toInt()
                                userStatus = doc.getString(username) ?: ""
                            }
                        schoolId.collection("Promozioni")
                            .get()
                            .addOnSuccessListener { documents3 ->
                                promozioni = documents3.map { document ->
                                    document.toObject(Promozione::class.java)
                                }
                            }
                    }
            }
        }

    val navController = rememberNavController()

    if(goThrough) {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable(
                route = "home"
            ) {
                MainMenu(
                    instagram = instagram,
                    instagramUrl = instagramUrl,
                    GPlink = GPlink,
                    eventStatus = eventStatus,
                    privacyAccepted = privacyAccepted,
                    db = db,
                    uid = uid,
                    currentVersionServer = currentVersionServer,
                    navController = navController,
                ) {
                    privacyAccepted = true
                }
            }

            // Calendar subsection
            composable(
                route = "calendar",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                }
            ) {
                CalendarComp(
                    db = db,
                    orario = orario,
                    overrides = overrides,
                    totNotes = totNotes,
                    preferences = preferences,
                    navController = navController
                )
            }

            // Maps subSection
            composable(
                route = "appMaps",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                }
            ) {
                AppMaps(
                    planimetria = planimetria,
                    cartine = cartine,
                    navController = navController
                )
            }

            // Utilities subSection
            composable(
                route = "utilities",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                }
            ) {
                Utilities(
                    navController = navController
                )
            }

            // Events subSection
            composable(
                route = "assemblea",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                }
            ) {
                Assemblea(
                    username = username,
                    eventStatus = eventStatus,
                    userStatus = userStatus,
                    school = school,
                    navController = navController
                ) {
                    userStatus = it
                    val update = hashMapOf<String, Any>(username to it)
                    db.document("/Eventi/StatusControl").update(update)
                }
            }
            composable(
                route = "promos",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                }
            ) {
                Promos(
                    promozioni = promozioni,
                    navController = navController
                )
            }

            // Marketplace subsection
            navigation(
                startDestination = "marketplaceMain",
                route = "marketplace",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { -1000 },
                        animationSpec = tween(700)
                    ) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                }
            ) {
                composable("marketplaceMain") {
                    MarketplaceMain(
                        offers = offers,
                        username = username,
                        hisClass = hisClass,
                        uid = uid,
                        school = school,
                        db = db,
                        navController = navController,
                        liftOffers = { offers = it }
                    ) {
                        offer = it
                    }
                }

                composable("marketplacePersonal") {
                    MarketplacePersonal(
                        offers = offers,
                        uid = uid,
                        navController = navController
                    ) {
                        offer = it
                    }
                }

                composable("marketplaceProductPage") {
                    MarketplaceProductPage(
                        offer = offer,
                        school = school,
                        uid = uid,
                        db = db,
                        navController = navController
                    ) {
                        offers -= it
                    }
                }
            }

            // Infos Navigation
            navigation(
                startDestination = "infoSection",
                route = "info",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { -1000 },
                        animationSpec = tween(700)
                    ) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 },
                        animationSpec = tween(700)
                    ) + fadeOut()
                }
                ) {
                composable("infoSection") {
                    InfoSection(
                        navController = navController,
                        auth = auth
                    )
                }
                composable("accountSection") {
                    AccountSection(
                        username = username,
                        hisClass = hisClass,
                        email = email,
                        navController = navController
                    ) {
                        showToast(it)
                    }
                }
            }
        }
    }
}

@Composable
fun ShowToast(
    message: String
) {
    val context = LocalContext.current
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}