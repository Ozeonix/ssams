import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:ssams_student/core/auth/auth_state_notifier.dart';

const _baseUrl = String.fromEnvironment('API_BASE_URL',
    defaultValue: 'http://10.0.2.2:8080/api/v1'); // Android emulator localhost

final dioProvider = Provider<Dio>((ref) {
  final dio = Dio(BaseOptions(
    baseUrl: _baseUrl,
    connectTimeout: const Duration(seconds: 15),
    receiveTimeout: const Duration(seconds: 30),
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  ));

  // Auth interceptor — attach token and handle 401 with refresh
  dio.interceptors.add(_AuthInterceptor(ref, dio));

  return dio;
});

class _AuthInterceptor extends Interceptor {
  final Ref ref;
  final Dio dio;
  bool _isRefreshing = false;

  _AuthInterceptor(this.ref, this.dio);

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    final authState = ref.read(authStateProvider);
    if (authState.accessToken != null) {
      options.headers['Authorization'] = 'Bearer ${authState.accessToken}';
    }
    handler.next(options);
  }

  @override
  Future<void> onError(DioException err, ErrorInterceptorHandler handler) async {
    if (err.response?.statusCode == 401 && !_isRefreshing) {
      _isRefreshing = true;
      try {
        final authState = ref.read(authStateProvider);
        if (authState.refreshToken == null) {
          await ref.read(authStateProvider.notifier).logout();
          handler.next(err);
          return;
        }

        final response = await dio.post('/auth/refresh',
            data: {'refreshToken': authState.refreshToken},
            options: Options(headers: {'Authorization': null}));

        final data = response.data['data'];
        final newAccess = data['accessToken'] as String;
        final newRefresh = data['refreshToken'] as String;

        await ref.read(authStateProvider.notifier)
            .updateTokens(newAccess, newRefresh);

        // Retry original request with new token
        err.requestOptions.headers['Authorization'] = 'Bearer $newAccess';
        final retryResponse = await dio.fetch(err.requestOptions);
        handler.resolve(retryResponse);
      } catch (_) {
        await ref.read(authStateProvider.notifier).logout();
        handler.next(err);
      } finally {
        _isRefreshing = false;
      }
    } else {
      handler.next(err);
    }
  }
}
