package com.example.brickshare.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.brickshare.ui.theme.BrickShareFonts
import com.example.brickshare.ui.theme.HederaGreen
import com.example.brickshare.ui.theme.DeepNavy
import com.example.brickshare.ui.theme.BuildingBlocksWhite
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

data class PropertyInvestor(
    val name: String,
    val shares: Int
)

data class Property(
    val id: String,
    val name: String,
    val address: String,
    val value: Int,
    val investors: List<PropertyInvestor>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePropertyScreen(navController: NavController, propertyId: String) {
    val property = Property(
        id = propertyId,
        name = "Property A",
        address = "123 Main St",
        value = 50000,
        investors = listOf(
            PropertyInvestor("John Doe", 5),
            PropertyInvestor("Jane Smith", 3)
        )
    )

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
            containerColor = BuildingBlocksWhite,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Manage Property",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("dashboard") }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = DeepNavy
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BuildingBlocksWhite
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                // Property Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardColors(
                        containerColor = Color.White,
                        contentColor = DeepNavy,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = property.name,
                            style = TextStyle(
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = property.address,
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 16.sp,
                            color = DeepNavy.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "$${property.value}",
                            style = TextStyle(
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = HederaGreen
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = 1f,
                            modifier = Modifier.fillMaxWidth(),
                            color = HederaGreen,
                            trackColor = DeepNavy.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Fully Funded",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 14.sp,
                            color = HederaGreen,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Investors Section
                Text(
                    text = "Investors",
                    fontFamily = BrickShareFonts.Halcyon,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepNavy,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Investors List Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardColors(
                        containerColor = Color.White,
                        contentColor = DeepNavy,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        items(property.investors) { investor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = investor.name,
                                    fontFamily = BrickShareFonts.Halcyon,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepNavy
                                )

                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp)),
                                    color = HederaGreen.copy(alpha = 0.2f),
                                    onClick = { /* Details view */ }
                                ) {
                                    Text(
                                        text = "${investor.shares} shares",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontFamily = BrickShareFonts.Halcyon,
                                        fontSize = 14.sp,
                                        color = HederaGreen
                                    )
                                }
                            }
                            if (investor != property.investors.last()) {
                                Divider(color = DeepNavy.copy(alpha = 0.2f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Distribution Action
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardColors(
                        containerColor = Color.White,
                        contentColor = DeepNavy,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ready to distribute profits?",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You have $500 available to distribute to investors",
                            fontFamily = BrickShareFonts.Halcyon,
                            fontSize = 14.sp,
                            color = DeepNavy.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { /* Handle distribution */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HederaGreen
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Distribute $500",
                                fontFamily = BrickShareFonts.Halcyon,
                                fontSize = 16.sp,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManagePropertyScreen() {
    ManagePropertyScreen(
        navController = rememberNavController(),
        propertyId = "1"
    )
}