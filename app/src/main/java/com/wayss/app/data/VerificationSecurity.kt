package com.wayss.app.data

/**
 * Client-side models must never be treated as authoritative for role, verified,
 * or creatorBadge. Those values are enforced by Firebase custom claims,
 * Cloud Functions and Firestore rules.
 */
object VerificationSecurity {
    const val ADMIN_CLAIM = "admin"
    const val VERIFIED_CLAIM = "verified"
}
