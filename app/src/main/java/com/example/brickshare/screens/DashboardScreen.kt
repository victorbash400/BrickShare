package com.example.brickshare.screens
import com.example.brickshare.ui.theme.HederaGreen
import com.example.brickshare.ui.theme.DeepNavy
import com.example.brickshare.ui.theme.BuildingBlocksWhite
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import com.example.brickshare.R
import com.example.brickshare.ui.theme.BrickShareFonts
import com.example.brickshare.viewmodel.UserViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.brickshare.ui.theme.BrickShareFonts.Halcyon

// Colors from your spec
val HederaGreen = Color(0xFF15C123)
val DeepNavy = Color(0xFF0A2540)
val BuildingBlocksWhite = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val userRole by userViewModel.userRole.collectAsState()

    // Material 3 theme with custom colors
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = HederaGreen,
            onPrimary = Color.White,
            background = BuildingBlocksWhite,
            onBackground = DeepNavy
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BuildingBlocksWhite
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (userRole == "investor") "Your Portfolio" else "Property Dashboard",
                        style = TextStyle(
                            fontFamily = Halcyon,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = DeepNavy
                        )
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = { /* Notifications */ },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HederaGreen.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.Rounded.Notifications,
                            contentDescription = "Notifications",
                            tint = HederaGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Summary Card
                if (userRole == "investor") {
                    PortfolioSummaryCard()
                } else {
                    PropertySummaryCard()
                }

                Spacer(Modifier.height(24.dp))

                // Featured Properties
                FeaturedPropertiesSection(navController)
            }
        }
    }
}

@Composable
fun PortfolioSummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = DeepNavy,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$124,500",
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = DeepNavy
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Total Portfolio Value",
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontSize = 16.sp,
                    color = DeepNavy.copy(alpha = 0.7f)
                )
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { /* Invest action */ },
                colors = ButtonDefaults.buttonColors(containerColor = HederaGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+ Invest", color = Color.White, fontFamily = Halcyon)
            }
        }
    }
}

@Composable
fun PropertySummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = DeepNavy,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$500,000",
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = DeepNavy
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Total Property Value",
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontSize = 16.sp,
                    color = DeepNavy.copy(alpha = 0.7f)
                )
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { /* Manage action */ },
                colors = ButtonDefaults.buttonColors(containerColor = HederaGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Manage", color = Color.White, fontFamily = Halcyon)
            }
        }
    }
}

@Composable
fun FeaturedPropertiesSection(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Featured Properties",
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = DeepNavy
                )
            )
            Text(
                text = "See All",
                style = TextStyle(
                    fontFamily = Halcyon,
                    fontSize = 16.sp,
                    color = HederaGreen
                ),
                modifier = Modifier.clickable { navController.navigate("browse") }
            )
        }

        Spacer(Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(listOf(
                Triple("Modern Loft", "$1,200/mo", R.drawable.placeholder_property),
                Triple("Beach Villa", "$2,100/mo", R.drawable.placeholder_property),
                Triple("City Condo", "$800/mo", R.drawable.placeholder_property)
            )) { (name, price, image) ->
                PropertyCard(name, price, image) {
                    navController.navigate("property_detail/0")
                }
            }
        }
    }
}

@Composable
fun PropertyCard(name: String, price: String, imageRes: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(
            containerColor = Color.White,
            contentColor = DeepNavy,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        )
    ) {
        Column {
            Image(
                painter = painterResource(imageRes),
                contentDescription = name,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = name,
                    style = TextStyle(
                        fontFamily = Halcyon,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DeepNavy
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = price,
                    style = TextStyle(
                        fontFamily = Halcyon,
                        fontSize = 14.sp,
                        color = HederaGreen
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreviewInvestor() {
    val viewModel = UserViewModel().apply { setUserRole("investor") }
    DashboardScreen(rememberNavController(), viewModel)
}

@Preview(showBackground = true)
@Composable
fun DashboardPreviewOwner() {
    val viewModel = UserViewModel().apply { setUserRole("owner") }
    DashboardScreen(rememberNavController(), viewModel)
}