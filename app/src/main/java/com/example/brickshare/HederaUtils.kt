package com.example.brickshare

import com.hedera.hashgraph.sdk.AccountCreateTransaction
import com.hedera.hashgraph.sdk.Hbar
import com.hedera.hashgraph.sdk.PrivateKey
import android.util.Log

object HederaUtils {
    suspend fun createHederaAccount(): Pair<String, String> {
        try {
            val newPrivateKey = PrivateKey.generateED25519()
            val newPublicKey = newPrivateKey.publicKey

            Log.d("HederaUtils", "Generated new key pair for Hedera account")

            val transaction = AccountCreateTransaction()
                .setKey(newPublicKey)
                .setInitialBalance(Hbar(5))
                .freezeWith(HederaClient.client)

            val response = transaction.execute(HederaClient.client)
            val receipt = response.getReceipt(HederaClient.client)

            val newAccountId = receipt.accountId ?: throw Exception("Failed to retrieve new account ID from receipt")

            Log.d("HederaUtils", "Hedera account created: $newAccountId")
            return Pair(newAccountId.toString(), newPrivateKey.toString())
        } catch (e: Exception) {
            Log.e("HederaUtils", "Failed to create Hedera account: ${e.javaClass.name} - ${e.message}", e)
            throw e // Re-throw to let the caller handle it
        }
    }
}