# eSewa Integration Guide

## Official source
Provider-specific behavior must be verified against the official eSewa developer documentation:
https://developer.esewa.com.np/

The current documentation reviewed for this pack includes ePay/ePay V2, status checking, HMAC-SHA256 signing, IPN/verification behavior, Android/iOS SDKs and Intent.

## Recommended first integration
For SSAMS, implement one gateway path behind an eSewa adapter. The ePay V2 web/form flow is suitable for a backend-controlled first implementation. Do not mix ePay, SDK and Intent credentials or protocols.

## ePay V2 request
The current official ePay V2 documentation describes a POST form containing:
- amount
- tax_amount
- total_amount
- transaction_uuid
- product_code
- product_service_charge
- product_delivery_charge
- success_url
- failure_url
- signed_field_names
- signature

Do not hard-code these values in Flutter. The backend creates the payment request from the authoritative invoice.

## Signature
eSewa documents HMAC-SHA256 with Base64 output. The documented signing example uses the signed fields in the configured order, including:
total_amount,transaction_uuid,product_code

Implementation must construct the signed message exactly according to the current eSewa documentation and `signed_field_names`.

## Production endpoint
Current official ePay V2 documentation lists:
https://epay.esewa.com.np/api/epay/main/v2/form

Put provider URLs in configuration rather than scattering them through source code.

## Verification
A successful browser redirect is not sufficient. The backend must validate response integrity and use the documented eSewa transaction verification/status mechanism before finalizing the SSAMS payment.

## Status checking
The current ePay V2 documentation describes statuses including:
- PENDING
- COMPLETE
- FULL_REFUND
- PARTIAL_REFUND
- AMBIGUOUS
- NOT_FOUND
- CANCELED

Map provider statuses into an internal SSAMS state machine rather than exposing provider states everywhere.

## Important identifiers
Persist:
- internal payment ID
- invoice ID
- institution ID
- student ID
- transaction_uuid
- product_code
- gateway reference ID
- requested amount
- verified amount
- provider status
- internal status
- timestamps

## UAT
Use eSewa's UAT/test environment and test credentials only in development/staging. Never commit credentials. Production credentials are supplied through the merchant onboarding process.

## Timeout/recovery
Support:
- PENDING
- AMBIGUOUS/unknown internal state
- status verification retry
- scheduled reconciliation
- manual review

Never create a second successful payment merely because a callback was delayed.

## Mobile options
Official eSewa documentation also describes Android/iOS SDK and Intent integrations. If SSAMS later chooses one, implement it as a separate provider adapter/path. Do not expose merchant secrets through Flutter.

## Security
- HTTPS only
- backend-only secret key
- signature verification
- server-side amount verification
- transaction UUID verification
- product code verification
- idempotent callback handling
- rate limiting
- redacted logs

## Official references
https://developer.esewa.com.np/
https://developer.esewa.com.np/pages/Epay
https://developer.esewa.com.np/pages/Epay-V2
https://developer.esewa.com.np/pages/Test-credentials
https://developer.esewa.com.np/pages/Android
https://developer.esewa.com.np/pages/iOS
https://developer.esewa.com.np/pages/Intent
