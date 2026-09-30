import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:ssams_admin/core/auth/auth_provider.dart';
import 'package:ssams_admin/features/academic/presentation/academic_screen.dart';
import 'package:ssams_admin/features/attendance/presentation/attendance_screen.dart';
import 'package:ssams_admin/features/auth/presentation/login_screen.dart';
import 'package:ssams_admin/features/dashboard/presentation/dashboard_screen.dart';
import 'package:ssams_admin/features/exams/presentation/exams_screen.dart';
import 'package:ssams_admin/features/payments/presentation/payments_screen.dart';
import 'package:ssams_admin/features/settings/presentation/settings_screen.dart';
import 'package:ssams_admin/features/shell/presentation/admin_shell.dart';
import 'package:ssams_admin/features/students/presentation/students_screen.dart';

final routerProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authStateProvider);

  return GoRouter(
    initialLocation: '/dashboard',
    redirect: (context, state) {
      final isAuth = authState.isAuthenticated;
      final isLoggingIn = state.matchedLocation == '/login';

      if (!isAuth && !isLoggingIn) return '/login';
      if (isAuth && isLoggingIn) return '/dashboard';
      return null;
    },
    routes: [
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginScreen(),
      ),
      ShellRoute(
        builder: (context, state, child) => AdminShell(child: child),
        routes: [
          GoRoute(
            path: '/dashboard',
            builder: (context, state) => const DashboardScreen(),
          ),
          GoRoute(
            path: '/academic',
            builder: (context, state) => const AcademicScreen(),
          ),
          GoRoute(
            path: '/students',
            builder: (context, state) => const StudentsScreen(),
          ),
          GoRoute(
            path: '/exams',
            builder: (context, state) => const ExamsScreen(),
          ),
          GoRoute(
            path: '/attendance',
            builder: (context, state) => const AttendanceScreen(),
          ),
          GoRoute(
            path: '/payments',
            builder: (context, state) => const PaymentsScreen(),
          ),
          GoRoute(
            path: '/settings',
            builder: (context, state) => const SettingsScreen(),
          ),
        ],
      ),
    ],
  );
});
