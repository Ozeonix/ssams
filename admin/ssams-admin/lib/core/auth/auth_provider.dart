import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AuthState {
  final bool isAuthenticated;
  final String? accessToken;
  final String? refreshToken;
  final String? userId;
  final String? tenantId;
  final String? tenantCode;
  final String? username;
  final List<String> permissions;

  const AuthState({
    this.isAuthenticated = false,
    this.accessToken,
    this.refreshToken,
    this.userId,
    this.tenantId,
    this.tenantCode,
    this.username,
    this.permissions = const [],
  });

  AuthState copyWith({
    bool? isAuthenticated,
    String? accessToken,
    String? refreshToken,
    String? userId,
    String? tenantId,
    String? tenantCode,
    String? username,
    List<String>? permissions,
  }) {
    return AuthState(
      isAuthenticated: isAuthenticated ?? this.isAuthenticated,
      accessToken: accessToken ?? this.accessToken,
      refreshToken: refreshToken ?? this.refreshToken,
      userId: userId ?? this.userId,
      tenantId: tenantId ?? this.tenantId,
      tenantCode: tenantCode ?? this.tenantCode,
      username: username ?? this.username,
      permissions: permissions ?? this.permissions,
    );
  }

  static const unauthenticated = AuthState();
}

const _kAccessToken = 'artms_admin_access_token';
const _kRefreshToken = 'artms_admin_refresh_token';
const _kUserId = 'artms_admin_user_id';
const _kTenantId = 'artms_admin_tenant_id';
const _kTenantCode = 'artms_admin_tenant_code';
const _kUsername = 'artms_admin_username';

final authStateProvider = StateNotifierProvider<AuthStateNotifier, AuthState>(
  (ref) => AuthStateNotifier(),
);

class AuthStateNotifier extends StateNotifier<AuthState> {
  AuthStateNotifier() : super(AuthState.unauthenticated) {
    _loadFromStorage();
  }

  Future<void> _loadFromStorage() async {
    final prefs = await SharedPreferences.getInstance();
    final accessToken = prefs.getString(_kAccessToken);
    final refreshToken = prefs.getString(_kRefreshToken);
    final userId = prefs.getString(_kUserId);
    final tenantId = prefs.getString(_kTenantId);
    final tenantCode = prefs.getString(_kTenantCode);
    final username = prefs.getString(_kUsername);

    if (accessToken != null && refreshToken != null) {
      state = AuthState(
        isAuthenticated: true,
        accessToken: accessToken,
        refreshToken: refreshToken,
        userId: userId,
        tenantId: tenantId,
        tenantCode: tenantCode,
        username: username,
      );
    }
  }

  Future<void> login({
    required String accessToken,
    required String refreshToken,
    required String userId,
    required String tenantId,
    required String tenantCode,
    required String username,
    List<String> permissions = const [],
  }) async {
    final prefs = await SharedPreferences.getInstance();
    await Future.wait([
      prefs.setString(_kAccessToken, accessToken),
      prefs.setString(_kRefreshToken, refreshToken),
      prefs.setString(_kUserId, userId),
      prefs.setString(_kTenantId, tenantId),
      prefs.setString(_kTenantCode, tenantCode),
      prefs.setString(_kUsername, username),
    ]);

    state = AuthState(
      isAuthenticated: true,
      accessToken: accessToken,
      refreshToken: refreshToken,
      userId: userId,
      tenantId: tenantId,
      tenantCode: tenantCode,
      username: username,
      permissions: permissions,
    );
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();
    state = AuthState.unauthenticated;
  }

  Future<void> updateTokens(String accessToken, String refreshToken) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_kAccessToken, accessToken);
    await prefs.setString(_kRefreshToken, refreshToken);
    state = state.copyWith(accessToken: accessToken, refreshToken: refreshToken);
  }
}
