import 'package:flutter/material.dart';

/// ARTMS app theme — clean academic management aesthetic.
/// Note: google_fonts package not added as dependency;
/// using SystemFontFamily for now (add google_fonts when package is ready).
class AppTheme {
  AppTheme._();

  static const _primaryColor = Color(0xFF1565C0);  // Deep blue — authority, trust
  static const _secondaryColor = Color(0xFF26A69A); // Teal — knowledge
  static const _errorColor = Color(0xFFC62828);

  static ThemeData light() {
    return ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.fromSeed(
        seedColor: _primaryColor,
        secondary: _secondaryColor,
        error: _errorColor,
        brightness: Brightness.light,
      ),
      textTheme: _textTheme(),
      inputDecorationTheme: _inputDecorationTheme(),
      cardTheme: _cardTheme(),
      appBarTheme: AppBarTheme(
        centerTitle: false,
        elevation: 0,
        scrolledUnderElevation: 1,
        backgroundColor: Colors.white,
        foregroundColor: _primaryColor,
        titleTextStyle: const TextStyle(
          fontSize: 20,
          fontWeight: FontWeight.w600,
          color: Color(0xFF1A1A2E),
        ),
      ),
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        selectedItemColor: _primaryColor,
        unselectedItemColor: Color(0xFF9E9E9E),
        backgroundColor: Colors.white,
        elevation: 8,
      ),
    );
  }

  static ThemeData dark() {
    return ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.fromSeed(
        seedColor: _primaryColor,
        secondary: _secondaryColor,
        brightness: Brightness.dark,
      ),
      textTheme: _textTheme(),
    );
  }

  static TextTheme _textTheme() {
    return const TextTheme(
      headlineLarge: TextStyle(fontSize: 28, fontWeight: FontWeight.w700),
      headlineMedium: TextStyle(fontSize: 24, fontWeight: FontWeight.w600),
      titleLarge: TextStyle(fontSize: 20, fontWeight: FontWeight.w600),
      titleMedium: TextStyle(fontSize: 16, fontWeight: FontWeight.w500),
      bodyLarge: TextStyle(fontSize: 16),
      bodyMedium: TextStyle(fontSize: 14),
      labelLarge: TextStyle(fontSize: 14, fontWeight: FontWeight.w600),
    );
  }

  static InputDecorationTheme _inputDecorationTheme() {
    return InputDecorationTheme(
      filled: true,
      fillColor: const Color(0xFFF5F5F5),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: BorderSide.none,
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: Color(0xFFE0E0E0)),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: _primaryColor, width: 2),
      ),
      errorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: _errorColor),
      ),
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
    );
  }

  static CardThemeData _cardTheme() {
    return CardThemeData(
      elevation: 2,
      shadowColor: Colors.black12,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      margin: EdgeInsets.zero,
    );
  }
}
