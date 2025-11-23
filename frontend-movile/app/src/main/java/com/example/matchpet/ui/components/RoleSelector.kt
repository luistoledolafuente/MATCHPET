package com.example.matchpet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchpet.data.model.UserRole
import com.example.matchpet.ui.theme.BorderGray
import com.example.matchpet.ui.theme.PrimaryTeal
import com.example.matchpet.ui.theme.TextSecondary


@Composable
fun RoleSelector(
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = BorderGray,
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White)
    ) {
        // Tab Adoptante
        RoleTab(
            text = "Adoptante",
            isSelected = selectedRole == UserRole.ADOPTER,
            onClick = { onRoleSelected(UserRole.ADOPTER) },
            modifier = Modifier.weight(1f)
        )

        // Tab Refugio
        RoleTab(
            text = "Refugio",
            isSelected = selectedRole == UserRole.SHELTER,
            onClick = { onRoleSelected(UserRole.SHELTER) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RoleTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) PrimaryTeal else Color.Transparent
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

// Alternativa: Versión con borde inferior (como en tu diseño)
@Composable
fun RoleSelectorUnderline(
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Tab Adoptante
            RoleTabUnderline(
                text = "Adoptante",
                isSelected = selectedRole == UserRole.ADOPTER,
                onClick = { onRoleSelected(UserRole.ADOPTER) },
                modifier = Modifier.weight(1f)
            )

            // Tab Refugio
            RoleTabUnderline(
                text = "Refugio",
                isSelected = selectedRole == UserRole.SHELTER,
                onClick = { onRoleSelected(UserRole.SHELTER) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RoleTabUnderline(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) PrimaryTeal else TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Línea inferior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    if (isSelected) PrimaryTeal else Color.Transparent
                )
        )
    }
}