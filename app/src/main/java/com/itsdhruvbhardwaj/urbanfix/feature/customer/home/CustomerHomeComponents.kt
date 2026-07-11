package com.itsdhruvbhardwaj.urbanfix.feature.customer.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsdhruvbhardwaj.urbanfix.R
import com.itsdhruvbhardwaj.urbanfix.ui.theme.Dimens

data class CategoryData(val name: String, val icon: ImageVector)
data class StepData(val icon: ImageVector, val label: String)

@Composable
fun CustomerTopBar(onMenuClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth().statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
            
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("Urban")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFFF97316))) {
                        append("Fix")
                    }
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                BadgedBox(badge = { Badge { Text("2") } }) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                }
                Spacer(modifier = Modifier.width(Dimens.SpacingMedium))
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}

@Composable
fun CustomerBottomNavigation() {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text(stringResource(R.string.nav_home)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.AutoMirrored.Filled.EventNote, null) },
            label = { Text(stringResource(R.string.nav_bookings)) }
        )
        NavigationBarItem(selected = false, onClick = {}, icon = { Spacer(Modifier.size(24.dp)) }, label = { Text("") }, enabled = false)
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.AutoMirrored.Filled.Chat, null) },
            label = { Text(stringResource(R.string.nav_chats)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Outlined.Person, null) },
            label = { Text(stringResource(R.string.nav_profile)) }
        )
    }
}

@Composable
fun GreetingSection(name: String) {
    Column(modifier = Modifier.padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall)) {
        Text(
            text = stringResource(R.string.home_greeting_morning, name),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.home_hero_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 32.sp
        )
    }
}

@Composable
fun LocationSection(location: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(Dimens.PaddingMedium),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.PaddingMedium, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = location, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
        }
    }
}

@Composable
fun SearchSection() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.PaddingMedium),
        placeholder = { Text(stringResource(R.string.home_search_placeholder), fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(Dimens.CornerRadiusHuge),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        singleLine = true
    )
}

@Composable
fun PopularServicesSection() {
    val categories = listOf(
        CategoryData(stringResource(R.string.cat_plumber), Icons.Default.WaterDrop),
        CategoryData(stringResource(R.string.cat_electrician), Icons.Default.ElectricBolt),
        CategoryData(stringResource(R.string.cat_carpenter), Icons.Default.Handyman),
        CategoryData(stringResource(R.string.cat_ac_repair), Icons.Default.AcUnit),
        CategoryData(stringResource(R.string.cat_washing_machine), Icons.Default.LocalLaundryService),
        CategoryData(stringResource(R.string.cat_ro_service), Icons.Default.Waves),
        CategoryData(stringResource(R.string.cat_painter), Icons.Default.FormatPaint),
        CategoryData(stringResource(R.string.cat_more), Icons.Default.GridView)
    )

    Column(modifier = Modifier.padding(top = Dimens.PaddingLarge)) {
        SectionHeader(stringResource(R.string.home_section_popular), stringResource(R.string.home_view_all))
        Column(modifier = Modifier.padding(horizontal = Dimens.PaddingSmall)) {
            categories.chunked(4).forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    rowItems.forEach { item -> CategoryIcon(item) }
                }
            }
        }
    }
}

@Composable
fun CategoryIcon(item: CategoryData) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp).width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(item.icon, contentDescription = item.name, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = item.name, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun VerifiedBanner() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(Dimens.PaddingMedium),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
    ) {
        Row(modifier = Modifier.padding(Dimens.PaddingLarge), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = "✓ " + stringResource(R.string.home_banner_verified),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.home_banner_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = stringResource(R.string.home_banner_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(Dimens.CornerRadiusMedium)) {
                    Text(stringResource(R.string.home_banner_button), fontSize = 12.sp)
                }
            }
            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        }
    }
}

@Composable
fun HowItWorksSection() {
    val steps = listOf(
        StepData(Icons.Default.TouchApp, stringResource(R.string.step_choose)),
        StepData(Icons.Default.EditNote, stringResource(R.string.step_describe)),
        StepData(Icons.Default.People, stringResource(R.string.step_match)),
        StepData(Icons.Default.CheckCircle, stringResource(R.string.step_relax))
    )

    Column(modifier = Modifier.padding(top = Dimens.PaddingSmall)) {
        SectionHeader(stringResource(R.string.home_section_how_it_works), stringResource(R.string.home_see_all))
        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.PaddingMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge)
        ) {
            items(steps) { step ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
                    Surface(modifier = Modifier.size(50.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
                        Box(contentAlignment = Alignment.Center) { Icon(step.icon, null, tint = MaterialTheme.colorScheme.primary) }
                    }
                    Text(step.label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp), lineHeight = 12.sp)
                }
            }
        }
    }
}

@Composable
fun TopRatedSection() {
    val names = remember { listOf("Ravi Kumar", "Amit Verma", "Sandeep Y.", "Imran Khan") }
    Column(modifier = Modifier.padding(top = Dimens.PaddingLarge)) {
        SectionHeader(stringResource(R.string.home_section_top_rated), stringResource(R.string.home_view_all))
        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.PaddingMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
        ) {
            items(4) { index -> TopRatedCard(name = names[index], rating = "4.8") }
        }
    }
}

@Composable
fun TopRatedCard(name: String, rating: String) {
    Card(modifier = Modifier.width(110.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
            Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
                Icon(Icons.Default.AccountCircle, null, modifier = Modifier.fillMaxSize(), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFFB400), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(rating, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = action, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
    }
}
