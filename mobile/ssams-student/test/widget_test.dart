import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:ssams_student/main.dart';

void main() {
  testWidgets('App renders login screen with institution and credentials fields', (WidgetTester tester) async {
    await tester.pumpWidget(
      const ProviderScope(
        child: ArtmsStudentApp(),
      ),
    );

    // Initial frame renders login screen
    await tester.pumpAndSettle();

    // Verify key login UI elements
    expect(find.text('Institution Code'), findsOneWidget);
    expect(find.text('Username / Email / Phone'), findsOneWidget);
    expect(find.text('Password'), findsOneWidget);
    expect(find.text('Sign In'), findsOneWidget);
  });
}
