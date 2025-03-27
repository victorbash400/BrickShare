package com.example.brickshare

import com.hedera.hashgraph.sdk.*
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object HederaUtils {
    private val db = Firebase.firestore
    private val treasuryAccountId = AccountId.fromString("0.0.5777541")
    private val operatorKey = PrivateKey.fromString("3030020100300706052b8104000a042204200ac7437f8b86a91a5ec619935fd93868742a3e1aa2ef98b81833e7f8ded01b14")

    // Create a new Hedera account
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
            throw e
        }
    }

    // Create a property token
    suspend fun createPropertyToken(propertyId: String, totalTokens: Int): String {
        try {
            val client = HederaClient.client

            val tokenCreateTx = TokenCreateTransaction()
                .setTokenName("REAL_ESTATE_$propertyId")
                .setTokenSymbol(propertyId.take(4))
                .setDecimals(0)
                .setInitialSupply(totalTokens.toLong())
                .setTreasuryAccountId(treasuryAccountId)
                .setAdminKey(operatorKey.publicKey)
                .setSupplyKey(operatorKey.publicKey)
                .freezeWith(client)

            val signedTx = tokenCreateTx.sign(operatorKey)
            val txResponse = signedTx.execute(client)
            val receipt = txResponse.getReceipt(client)
            val tokenId = receipt.tokenId?.toString() ?: throw Exception("Failed to create token")

            Log.d("HederaUtils", "Property token created: $tokenId")
            return tokenId
        } catch (e: Exception) {
            Log.e("HederaUtils", "Failed to create property token: ${e.message}", e)
            throw e
        }
    }

    // Associate token with account
    suspend fun associateTokenWithAccount(tokenId: String, accountId: String, buyerPrivateKey: PrivateKey) {
        try {
            val client = HederaClient.client
            val account = AccountId.fromString(accountId)

            Log.d("HederaUtils", "Checking token association for account $accountId with token $tokenId")

            val accountInfo = AccountInfoQuery()
                .setAccountId(account)
                .execute(client)
            val isAssociated = accountInfo.tokenRelationships.containsKey(TokenId.fromString(tokenId))

            if (isAssociated) {
                Log.d("HederaUtils", "Token $tokenId already associated with account $accountId, skipping association")
                return
            }

            Log.d("HederaUtils", "Associating token $tokenId with account $accountId")
            val associateTx = TokenAssociateTransaction()
                .setAccountId(account)
                .setTokenIds(listOf(TokenId.fromString(tokenId)))
                .freezeWith(client)

            val signedTx = associateTx.sign(buyerPrivateKey)
            val txResponse = signedTx.execute(client)
            val receipt = txResponse.getReceipt(client)

            Log.d("HederaUtils", "Token $tokenId associated with account $accountId. Transaction ID: ${receipt.transactionId}")
        } catch (e: Exception) {
            Log.e("HederaUtils", "Failed to associate token $tokenId with account $accountId: ${e.message}", e)
            throw e
        }
    }

    // Update HBAR balance in Firestore
    suspend fun updateHbarBalance(userId: String, hbarChange: Hbar) {
        try {
            val userDocRef = db.collection("users").document(userId)
            val currentBalance = (userDocRef.get().await().getLong("hbarBalance") ?: 0).toLong()
            val newBalance = currentBalance + (hbarChange.toTinybars() / 100_000_000)
            userDocRef.update("hbarBalance", newBalance).await()
            Log.d("HederaUtils", "Updated HBAR balance for user $userId: $newBalance HBAR")
        } catch (e: Exception) {
            Log.e("HederaUtils", "Failed to update HBAR balance for $userId: ${e.message}", e)
            throw e
        }
    }

    // Transfer tokens for purchase with HBAR payment
    suspend fun purchasePropertyTokens(
        tokenId: String,
        buyerAccountId: String,
        buyerUserId: String,
        amount: Int,
        pricePerToken: Double,
        buyerPrivateKey: String
    ) {
        try {
            val client = HederaClient.client
            val buyerId = AccountId.fromString(buyerAccountId)
            val buyerKey = PrivateKey.fromString(buyerPrivateKey)
            val totalHbarCostInHbar = amount * pricePerToken
            val totalHbarCost = Hbar.fromTinybars((totalHbarCostInHbar * 100_000_000).toLong())

            Log.d("HederaUtils", "Starting purchase: $amount tokens of $tokenId for $totalHbarCostInHbar HBAR by $buyerAccountId")

            val buyerDoc = db.collection("users").document(buyerUserId).get().await()
            val currentBalance = (buyerDoc.getLong("hbarBalance") ?: 0).toDouble()
            if (currentBalance < totalHbarCostInHbar) {
                throw IllegalStateException("Insufficient HBAR balance: $currentBalance HBAR available, $totalHbarCostInHbar required")
            }

            associateTokenWithAccount(tokenId, buyerAccountId, buyerKey)

            val transferTx = TransferTransaction()
                .addHbarTransfer(buyerId, Hbar.fromTinybars((-totalHbarCostInHbar * 100_000_000).toLong()))
                .addHbarTransfer(treasuryAccountId, Hbar.fromTinybars((totalHbarCostInHbar * 100_000_000).toLong()))
                .addTokenTransfer(TokenId.fromString(tokenId), treasuryAccountId, -amount.toLong())
                .addTokenTransfer(TokenId.fromString(tokenId), buyerId, amount.toLong())
                .freezeWith(client)

            val signedTx = transferTx.sign(buyerKey).sign(operatorKey)
            val txResponse = signedTx.execute(client)
            val receipt = txResponse.getReceipt(client)

            // Update buyer’s balance and property’s collected HBAR
            updateHbarBalance(buyerUserId, Hbar.fromTinybars((-totalHbarCostInHbar * 100_000_000).toLong()))
            val propertyId = "mhLsMjBmrOltqGW7dAE2" // Hardcoded for now, replace with dynamic logic later
            db.collection("properties").document(propertyId)
                .update("totalHbarCollected", FieldValue.increment(totalHbarCostInHbar))
                .await()

            Log.d("HederaUtils", "Purchase completed: $amount tokens of $tokenId to $buyerAccountId for $totalHbarCostInHbar HBAR. Transaction ID: ${receipt.transactionId}")
        } catch (e: Exception) {
            Log.e("HederaUtils", "Failed to purchase tokens: ${e.message}", e)
            throw e
        }
    }

    // Transfer collected HBAR from treasury to owner
    suspend fun transferToOwner(propertyId: String) {
        try {
            val propertyDoc = db.collection("properties").document(propertyId).get().await()
            val ownerId = propertyDoc.getString("ownerId") ?: throw IllegalStateException("No ownerId found")
            val ownerDoc = db.collection("users").document(ownerId).get().await()
            val ownerAccountId = AccountId.fromString(ownerDoc.getString("hederaAccountId") ?: throw IllegalStateException("No hederaAccountId for owner"))
            val totalHbarCollected = (propertyDoc.getDouble("totalHbarCollected") ?: 0.0)
            val hbarToTransfer = Hbar.fromTinybars((totalHbarCollected * 100_000_000).toLong())

            if (totalHbarCollected <= 0) {
                Log.d("HederaUtils", "No HBAR to transfer for property $propertyId")
                return
            }

            val transferTx = TransferTransaction()
                .addHbarTransfer(treasuryAccountId, Hbar.fromTinybars((-totalHbarCollected * 100_000_000).toLong()))
                .addHbarTransfer(ownerAccountId, hbarToTransfer)
                .freezeWith(HederaClient.client)
                .sign(operatorKey)
                .execute(HederaClient.client)
            transferTx.getReceipt(HederaClient.client)

            updateHbarBalance(ownerId, hbarToTransfer)
            db.collection("properties").document(propertyId).update("totalHbarCollected", 0.0).await()

            Log.d("HederaUtils", "Transferred $totalHbarCollected HBAR to owner $ownerId for property $propertyId")
        } catch (e: Exception) {
            Log.e("HederaUtils", "Failed to transfer HBAR to owner for $propertyId: ${e.message}", e)
            throw e
        }
    }

    // Distribute HBAR to investors
    suspend fun distributeHbar(
        propertyId: String,
        ownerUserId: String,
        totalHbarToDistribute: Hbar,
        investors: List<Pair<String, Int>>
    ) {
        try {
            val client = HederaClient.client
            val ownerDoc = db.collection("users").document(ownerUserId).get().await()
            val ownerAccountId = AccountId.fromString(ownerDoc.getString("hederaAccountId") ?: throw IllegalStateException("Owner has no Hedera account"))
            val ownerKey = PrivateKey.fromString(ownerDoc.getString("hederaPrivateKey") ?: throw IllegalStateException("Owner has no private key"))

            val currentBalance = (ownerDoc.getLong("hbarBalance") ?: 0).toDouble()
            if (currentBalance < (totalHbarToDistribute.toTinybars() / 100_000_000.0)) {
                throw IllegalStateException("Insufficient HBAR balance: $currentBalance HBAR available, $totalHbarToDistribute required")
            }

            Log.d("HederaUtils", "Starting HBAR distribution for property $propertyId: $totalHbarToDistribute HBAR to ${investors.size} investors from $ownerAccountId")

            val totalShares = investors.sumOf { it.second }
            if (totalShares == 0) throw IllegalStateException("No shares to distribute to")

            val transferTx = TransferTransaction()
            investors.forEach { (investorAccountId, shares) ->
                val hbarAmount = Hbar.fromTinybars((totalHbarToDistribute.toTinybars() * shares) / totalShares)
                if (hbarAmount.toTinybars() > 0) {
                    transferTx.addHbarTransfer(AccountId.fromString(investorAccountId), hbarAmount)
                    transferTx.addHbarTransfer(ownerAccountId, hbarAmount.negated())
                    Log.d("HederaUtils", "Distributing $hbarAmount HBAR to $investorAccountId for $shares shares")
                }
            }

            val signedTx = transferTx.freezeWith(client).sign(ownerKey)
            val txResponse = signedTx.execute(client)
            val receipt = txResponse.getReceipt(client)

            updateHbarBalance(ownerUserId, totalHbarToDistribute.negated())
            investors.forEach { (investorAccountId, shares) ->
                val investorUserId = db.collection("users")
                    .whereEqualTo("hederaAccountId", investorAccountId)
                    .get()
                    .await()
                    .documents
                    .firstOrNull()?.id ?: return@forEach
                val hbarAmount = Hbar.fromTinybars((totalHbarToDistribute.toTinybars() * shares) / totalShares)
                updateHbarBalance(investorUserId, hbarAmount)
            }

            Log.d("HederaUtils", "HBAR distribution completed for property $propertyId. Transaction ID: ${receipt.transactionId}")
        } catch (e: Exception) {
            Log.e("HederaUtils", "Failed to distribute HBAR for property $propertyId: ${e.message}", e)
            throw e
        }
    }
}