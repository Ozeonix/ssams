import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:ssams_admin/main.dart';

void main() {
  testWidgets('ArtmsAdminApp smoke test', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: ArtmsAdminApp()));
    await tester.pumpAndSettle();

    expect(find.text('ARTMS Admin'), findsOneWidget);
  });
}
