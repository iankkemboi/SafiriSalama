package com.safirisalama.bot.android.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.safirisalama.bot.android.R

val default = FontFamily(
    Font(
        R.font.gotham_medium,
    ),
)
val Typography = Typography(
    bodyMedium = TextStyle(
        fontFamily = default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    ),
)
