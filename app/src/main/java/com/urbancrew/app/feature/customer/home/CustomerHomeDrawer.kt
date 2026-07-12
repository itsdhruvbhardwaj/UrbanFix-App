package com.urbancrew.app.feature.customer.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.urbancrew.app.R

@Composable
fun CustomerHomeDrawer(
    onCloseClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onItemClick: (String) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        modifier = Modifier.width(320.dp).fillMaxHeight(),
        windowInsets = WindowInsets(0)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Modern Header with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 40.dp)
            ) {
                // Close button
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding()
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                Column(modifier = Modifier.statusBarsPadding()) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp).fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Dhruv Bhardwaj",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "dhruv@urbancrew.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Menu Items
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                DrawerMenuItem(stringResource(R.string.drawer_profile), Icons.Default.Person) { onItemClick("Profile") }
                DrawerMenuItem(stringResource(R.string.drawer_settings), Icons.Default.Settings) { onItemClick("Settings") }
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                DrawerMenuItem(stringResource(R.string.drawer_contact_us), Icons.AutoMirrored.Filled.ContactSupport) { onItemClick("Contact") }
                DrawerMenuItem(stringResource(R.string.drawer_raise_query), Icons.AutoMirrored.Filled.HelpOutline) { onItemClick("Query") }
                DrawerMenuItem(stringResource(R.string.drawer_language), Icons.Default.Language) { onItemClick("Language") }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                DrawerMenuItem(
                    label = stringResource(R.string.drawer_become_worker),
                    icon = Icons.Default.WorkOutline,
                    iconColor = Color(0xFFF97316) // Orange
                ) { onItemClick("Worker") }
            }

            // Logout at bottom
            Box(modifier = Modifier.padding(16.dp)) {
                DrawerMenuItem(
                    label = stringResource(R.string.drawer_logout),
                    icon = Icons.AutoMirrored.Filled.Logout,
                    iconColor = MaterialTheme.colorScheme.error,
                    onClick = onLogoutClick
                )
            }
        }
    }
}

@Composable
internal fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.Medium) },
        icon = { Icon(icon, contentDescription = null, tint = iconColor) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(vertical = 2.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(12.dp)
    )
}
