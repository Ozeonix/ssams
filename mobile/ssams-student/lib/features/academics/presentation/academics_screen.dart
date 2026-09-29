import 'package:flutter/material.dart';

class AcademicsScreen extends StatelessWidget {
  const AcademicsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Academics')),
      body: const Center(child: Text('Academic calendar & subjects — coming next sprint')),
    );
  }
}
