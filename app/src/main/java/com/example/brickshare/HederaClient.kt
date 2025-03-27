package com.example.brickshare

import com.hedera.hashgraph.sdk.AccountId
import com.hedera.hashgraph.sdk.Client
import com.hedera.hashgraph.sdk.PrivateKey
import android.util.Log

object HederaClient {
    val client: Client by lazy {
        try {
            val client = Client.forTestnet()
            val operatorId = AccountId.fromString("0.0.5777541")
            val operatorKey = PrivateKey.fromString("3030020100300706052b8104000a042204200ac7437f8b86a91a5ec619935fd93868742a3e1aa2ef98b81833e7f8ded01b14")
            client.setOperator(operatorId, operatorKey)
            Log.d("HederaClient", "Hedera testnet client initialized successfully")
            client
        } catch (e: Exception) {
            Log.e("HederaClient", "Failed to initialize Hedera client: ${e.message}", e)
            throw e // Re-throw to ensure caller handles the failure
        }
    }
}