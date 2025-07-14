import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

const stripe = require("stripe")(functions.config().stripe.secret_key);

admin.initializeApp();
const db = admin.firestore();

export const createPaymentIntent = functions.https.onCall(async (data, context) => {
  const uid = context.auth?.uid;
  const requestId = data.requestId as string;

  if (!uid) {
    throw new functions.https.HttpsError("unauthenticated", "User must be logged in.");
  }

  const requestRef = db.collection("rental_requests").doc(requestId);
  const requestDoc = await requestRef.get();
  const rentalData = requestDoc.data();

  if (!rentalData) {
    throw new functions.https.HttpsError("not-found", "Rental request not found.");
  }

  const paymentIntent = await stripe.paymentIntents.create({
    amount: Math.round(rentalData.totalPrice * 100),
    currency: "usd",
    metadata: {
      rentalRequestId: requestId,
    },
  });

  await requestRef.update({
    status: "APPROVED",
    paymentIntentId: paymentIntent.id,
    clientSecret: paymentIntent.client_secret,
  });

  return { clientSecret: paymentIntent.client_secret };
});