import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_student/core/auth/auth_state_notifier.dart';
import 'package:ssams_student/core/network/api_client.dart';

class LoginException implements Exception {
  final String message;
  LoginException(this.message);
}

final authRepositoryProvider = Provider<AuthRepository>((ref) {
  return AuthRepository(ref.watch(dioProvider), ref);
});

class AuthRepository {
  final Dio _dio;
  final Ref _ref;

  AuthRepository(this._dio, this._ref);

  Future<void> login({
    required String tenantCode,
    required String username,
    required String password,
  }) async {
    try {
      final response = await _dio.post('/auth/login', data: {
        'tenantCode': tenantCode,
        'username': username,
        'password': password,
      });

      final data = response.data['data'];

      await _ref.read(authStateProvider.notifier).login(
        accessToken: data['accessToken'] as String,
        refreshToken: data['refreshToken'] as String,
        userId: data['user']['id'] as String,
        tenantId: tenantCode, // store tenant code as identifier
        username: data['user']['username'] as String,
      );
    } on DioException catch (e) {
      final body = e.response?.data;
      final message = body is Map ? (body['message'] ?? 'Login failed') : 'Login failed';
      throw LoginException(message.toString());
    }
  }

  Future<void> logout(String refreshToken) async {
    try {
      await _dio.post('/auth/logout', data: {'refreshToken': refreshToken});
    } catch (_) {
      // Best-effort — still clear local state
    } finally {
      await _ref.read(authStateProvider.notifier).logout();
    }
  }
}
