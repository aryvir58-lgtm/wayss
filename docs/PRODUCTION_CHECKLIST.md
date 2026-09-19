# Production completion checklist

External infrastructure still required:

- Firebase project + google-services.json
- Google OAuth client configuration
- Phone Auth configuration and billing/quotas where applicable
- Firestore indexes based on deployed query patterns
- Storage CORS/policies as needed
- App Check enforcement after validation
- FCM notification payloads and deep links
- Secure TURN credentials for WebRTC
- Call signaling backend
- Story cleanup scheduled function
- Account deletion cascade/retention policy
- Abuse/rate-limit controls
- Crash/performance monitoring
- Privacy policy, terms, data retention text
- Play signing and release keystore

Do not ship with development credentials or an admin UID embedded in the APK.
