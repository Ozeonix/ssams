import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

/// Authentication state for the app.
class AuthState {
  final bool isAuthenticated;
  final String? accessToken;
  final String? refreshToken;
  final String? userId;
  final String? tenantId;
  final String? username;

  const AuthState({
    this.isAuthenticated = false,
    this.accessToken,
    this.refreshToken,
    this.userId,
    this.tenantId,
    this.username,
  });

  AuthState copyWith({
    bool? isAuthenticated,
    String? accessToken,
    String? refreshToken,
    String? userId,
    String? tenantId,
    String? username,
  }) {
    return AuthState(
      isAuthenticated: isAuthenticated ?? this.isAuthenticated,
      accessToken: accessToken ?? this.accessToken,
      refreshToken: refreshToken ?? this.refreshToken,
      userId: userId ?? this.userId,
      tenantId: tenantId ?? this.tenantId,
      username: username ?? this.username,
    );
  }

  static const unauthenticated = AuthState();
}

final _storage = const FlutterSecureStorage();
const _kAccessToken = 'artms_access_token';
const _kRefreshToken = 'artms_refresh_token';
const _kUserId = 'artms_user_id';
const _kTenantId = 'artms_tenant_id';
const _kUsername = 'artms_username';

final authStateProvider = StateNotifierProvider<AuthStateNotifier, AuthState>(
  (ref) => AuthStateNotifier(),
);

class AuthStateNotifier extends StateNotifier<AuthState> {
  AuthStateNotifier() : super(AuthState.unauthenticated) {
    _loadFromStorage();
  }

  Future<void> _loadFromStorage() async {
    final accessToken = await _storage.read(key: _kAccessToken);
    final refreshToken = await _storage.read(key: _kRefreshToken);
    final userId = await _storage.read(key: _kUserId);
    final tenantId = await _storage.read(key: _kTenantId);
    final username = await _storage.read(key: _kUsername);

    if (accessToken != null && refreshToken != null) {
      state = AuthState(
        isAuthenticated: true,
        accessToken: accessToken,
        refreshToken: refreshToken,
        userId: userId,
        tenantId: tenantId,
        username: username,
      );
    }
  }

  Future<void> login({
    required String accessToken,
    required String refreshToken,
    required String userId,
    required String tenantId,
    required String username,
  }) async {
    // Persist tokens securely — NEVER to SharedPreferences
    await Future.wait([
      _storage.write(key: _kAccessToken, value: accessToken),
      _storage.write(key: _kRefreshToken, value: refreshToken),
      _storage.write(key: _kUserId, value: userId),
      _storage.write(key: _kTenantId, value: tenantId),
      _storage.write(key: _kUsername, value: username),
    ]);

    state = AuthState(
      isAuthenticated: true,
      accessToken: accessToken,
      refreshToken: refreshToken,
      userId: userId,
      tenantId: tenantId,
      username: username,
    );
  }

  Future<void> logout() async {
    await _storage.deleteAll();
    state = AuthState.unauthenticated;
  }

  Future<void> updateTokens(String accessToken, String refreshToken) async {
    await Future.wait([
      _storage.write(key: _kAccessToken, value: accessToken),
      _storage.write(key: _kRefreshToken, value: refreshToken),
    ]);
    state = state.copyWith(accessToken: accessToken, refreshToken: refreshToken);
  }
}
