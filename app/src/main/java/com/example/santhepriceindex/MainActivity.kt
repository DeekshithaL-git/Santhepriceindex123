package com.example.santhepriceindex

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.google.firebase.database.*

data class Product(
    val id: String = "",
    val product: String = "",
    val price: String = "",
    val rating: String = ""
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme(

                colorScheme = darkColorScheme(
                    primary = Color(0xFF90CAF9),
                    secondary = Color(0xFF64B5F6)
                )
            ) {

                Surface {

                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {

        composable("dashboard") {
            DashboardScreen(navController)
        }

        composable("add") {
            AddProductScreen(navController)
        }

        composable("view") {
            ViewProductsScreen()
        }

        composable("admin") {
            AdminScreen()
        }
    }
}

@Composable
fun DashboardScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Sante Price Index",
            fontSize = 30.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(30.dp))

        DashboardButton("➕ Add Product") {
            navController.navigate("add")
        }

        DashboardButton("📋 View Products") {
            navController.navigate("view")
        }

        DashboardButton("👑 Admin Panel") {
            navController.navigate("admin")
        }
    }
}

@Composable
fun DashboardButton(
    title: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable {
                onClick()
            },

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {

        Text(
            text = title,
            fontSize = 22.sp,
            color = Color.White,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
fun AddProductScreen(
    navController: NavHostController
) {

    var product by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }

    val database = FirebaseDatabase
        .getInstance(
            "https://sante-price-index-84ddf-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )
        .reference

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Add Product",
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = product,
            onValueChange = {
                product = it
            },
            label = {
                Text("Product")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = price,
            onValueChange = {
                price = it
            },
            label = {
                Text("Price")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = rating,
            onValueChange = {
                rating = it
            },
            label = {
                Text("Rating")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val id =
                    database.child("prices")
                        .push()
                        .key ?: ""

                val item = Product(
                    id,
                    product,
                    price,
                    rating
                )

                database.child("prices")
                    .child(id)
                    .setValue(item)

                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Save Product")
        }
    }
}

@Composable
fun ViewProductsScreen() {

    val productList = remember {
        mutableStateListOf<Product>()
    }

    val context = LocalContext.current

    val database = FirebaseDatabase
        .getInstance(
            "https://sante-price-index-84ddf-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )
        .reference

    LaunchedEffect(Unit) {

        database.child("prices")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    productList.clear()

                    for (data in snapshot.children) {

                        val item =
                            data.getValue(Product::class.java)

                        if (item != null) {
                            productList.add(item)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "All Products",
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {

            items(productList) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = item.product,
                            fontSize = 22.sp,
                            color = Color.White
                        )

                        Text(
                            text = "₹ ${item.price}",
                            fontSize = 18.sp,
                            color = Color.White
                        )

                        Text(
                            text = "⭐ ${item.rating}",
                            fontSize = 18.sp,
                            color = Color.Yellow
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Button(
                            onClick = {

                                val phoneNumber =
                                    "919876543210"

                                val message =
                                    "Hello, I am interested in ${item.product}"

                                val intent = Intent(
                                    Intent.ACTION_VIEW
                                )

                                intent.data = Uri.parse(
                                    "https://wa.me/$phoneNumber?text=$message"
                                )

                                context.startActivity(intent)
                            }
                        ) {

                            Text("💬 Contact Seller")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminScreen() {

    val productList = remember {
        mutableStateListOf<Product>()
    }

    val database = FirebaseDatabase
        .getInstance(
            "https://sante-price-index-84ddf-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )
        .reference

    LaunchedEffect(Unit) {

        database.child("prices")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    productList.clear()

                    for (data in snapshot.children) {

                        val item =
                            data.getValue(Product::class.java)

                        if (item != null) {
                            productList.add(item)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "👑 Admin Panel",
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {

            items(productList) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = item.product,
                            fontSize = 22.sp,
                            color = Color.White
                        )

                        Text(
                            text = "₹ ${item.price}",
                            color = Color.White
                        )

                        Text(
                            text = "⭐ ${item.rating}",
                            color = Color.Yellow
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Button(
                            onClick = {

                                database.child("prices")
                                    .child(item.id)
                                    .removeValue()
                            }
                        ) {

                            Text("🗑 Delete Product")
                        }
                    }
                }
            }
        }
    }
}