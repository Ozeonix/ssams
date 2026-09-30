import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:ssams_student/core/auth/auth_state_notifier.dart';
import 'package:ssams_student/features/auth/application/auth_repository.dart';

class ProfileScreen extends ConsumerWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authStateProvider);
    return Scaffold(
      appBar: AppBar(title: const Text('Profile')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          ListTile(
            leading: const CircleAvatar(child: Icon(Icons.person)),
            title: Text(authState.username ?? 'Student'),
          ),
          const Divider(),
          ListTile(
            leading: const Icon(Icons.payments_outlined, color: Color(0xFF10B981)),
            title: const Text('My Fees & Invoices'),
            subtitle: const Text('View dues and pay online via eSewa'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () => context.push('/fees'),
          ),
          ListTile(
            leading: const Icon(Icons.history_rounded, color: Color(0xFF6366F1)),
            title: const Text('Payment History'),
            subtitle: const Text('Receipts and transaction records'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () => context.push('/fees/history'),
          ),
          const Divider(),
          ListTile(
            leading: const Icon(Icons.logout),
            title: const Text('Sign Out'),
            onTap: () async {
              final refreshToken = authState.refreshToken;
              if (refreshToken != null) {
                await ref.read(authRepositoryProvider).logout(refreshToken);
              } else {
                await ref.read(authStateProvider.notifier).logout();
              }
              if (context.mounted) context.go('/login');
            },
          ),
        ],
      ),
    );
  }
}
