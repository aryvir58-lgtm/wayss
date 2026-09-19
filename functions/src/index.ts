import {setGlobalOptions} from "firebase-functions/v2";
import {onCall, HttpsError} from "firebase-functions/v2/https";
import {getAuth} from "firebase-admin/auth";
import {getFirestore, FieldValue} from "firebase-admin/firestore";
import {initializeApp} from "firebase-admin/app";

initializeApp();
setGlobalOptions({maxInstances: 10});

export const approveVerification = onCall(async (request) => {
  if (!request.auth?.token.admin) {
    throw new HttpsError("permission-denied", "Admin role required.");
  }

  const requestId = String(request.data?.requestId ?? "");
  const applicantUid = String(request.data?.uid ?? "");
  if (!requestId || !applicantUid) {
    throw new HttpsError("invalid-argument", "requestId and uid are required.");
  }

  const db = getFirestore();
  await getAuth().setCustomUserClaims(applicantUid, {
    ...(await getAuth().getUser(applicantUid)).customClaims,
    verified: true
  });

  await db.doc(`users/${applicantUid}`).update({
    verified: true,
    verificationUpdatedAt: FieldValue.serverTimestamp()
  });

  await db.doc(`verificationRequests/${requestId}`).update({
    status: "APPROVED",
    reviewedBy: request.auth.uid,
    reviewedAt: FieldValue.serverTimestamp()
  });

  await db.collection("adminActions").add({
    type: "VERIFY_USER",
    targetUid: applicantUid,
    actorUid: request.auth.uid,
    createdAt: FieldValue.serverTimestamp()
  });

  return {ok: true};
});

export const rejectVerification = onCall(async (request) => {
  if (!request.auth?.token.admin) {
    throw new HttpsError("permission-denied", "Admin role required.");
  }
  const requestId = String(request.data?.requestId ?? "");
  if (!requestId) throw new HttpsError("invalid-argument", "requestId required.");

  await getFirestore().doc(`verificationRequests/${requestId}`).update({
    status: "REJECTED",
    reviewedBy: request.auth.uid,
    reviewedAt: FieldValue.serverTimestamp()
  });

  return {ok: true};
});
