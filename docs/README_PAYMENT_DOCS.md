# SSAMS Payment Documentation Pack

Place these files under `docs/` in the existing SSAMS repository.

Read them before implementing payment. Also read the existing SSAMS source/docs and use the official eSewa developer site as the provider-specific source of truth.

Rules:
- Do not invent eSewa fields/endpoints.
- Do not hard-code secrets.
- Do not put merchant secrets in Flutter.
- Do not treat browser/client success as final payment success.
- Verify transactions server-side.
- Keep eSewa behind a gateway abstraction.
- Update this pack if the official provider documentation changes.

Official documentation:
https://developer.esewa.com.np/
