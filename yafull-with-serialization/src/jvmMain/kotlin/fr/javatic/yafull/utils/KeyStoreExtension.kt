package fr.javatic.noteapp.config.security

import java.security.KeyStore
import java.util.*

fun KeyStore.getPrivateKeyAlgorithm(alias: String, password: CharArray?): String =
    getKey(alias, password).algorithm

fun KeyStore.getPrivateKeyAsPEM(alias: String, password: CharArray?): String = buildString {
    val key = getKey(alias, password)
    when (key.format) {
        "PKCS#1" -> appendLine("-----BEGIN RSA PRIVATE KEY-----")
        "PKCS#8" -> appendLine("-----BEGIN PRIVATE KEY-----")
        else -> throw Error("Unsupported key format `${key.format}`")
    }
    appendLine(Base64.getEncoder().encode(key.encoded).decodeToString())
    appendLine("-----END PRIVATE KEY-----")
}

fun KeyStore.getCertPublicKeyAlgorithm(alias: String) =
    getCertificate(alias).publicKey.algorithm

fun KeyStore.getCertPublicKeyAsPEM(alias: String): String = buildString {
    val cert = getCertificate(alias)
    appendLine("-----BEGIN PUBLIC KEY-----")
    appendLine(Base64.getEncoder().encode(cert.publicKey.encoded).decodeToString())
    appendLine("-----END PUBLIC KEY-----")
}

fun KeyStore.getCertAsPEM(alias: String): String = buildString {
    val cert = getCertificate(alias)
    appendLine("-----BEGIN CERTIFICATE-----")
    appendLine(Base64.getEncoder().encode(cert.encoded).decodeToString())
    appendLine("-----END CERTIFICATE-----")
}

