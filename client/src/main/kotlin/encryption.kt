import java.io.File
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

fun encrypt(plainText: String, publicKey: PublicKey): String {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    val encoded = cipher.doFinal(plainText.toByteArray())
    return Base64.getEncoder().encodeToString(encoded)
}

fun encrypt(plainText: String, privateKey: PrivateKey): String {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.ENCRYPT_MODE, privateKey)
    val encoded = cipher.doFinal(plainText.toByteArray())
    return Base64.getEncoder().encodeToString(encoded)
}

fun decrypt(encryptedText: String, privateKey: PrivateKey): String {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.DECRYPT_MODE, privateKey)
    val decoded = Base64.getDecoder().decode(encryptedText)
    val decodedBytes = cipher.doFinal(decoded)
    return String(decodedBytes)
}

fun generateRandomString(length: Int = 50): String {
    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..length)
        .map { charset.random() }
        .joinToString("")
}

fun keygen(filename: String): KeyPair {
    val generator = KeyPairGenerator.getInstance("RSA")
    val keypair = generator.genKeyPair()
    File(filename).writeText(
        addKeySpec(Base64.getEncoder().encodeToString(keypair.private.encoded)),
        charset = Charsets.US_ASCII
    )
    File("$filename.pub").writeText(
        addKeySpec(Base64.getEncoder().encodeToString(keypair.public.encoded)),
        charset = Charsets.US_ASCII
    )

    return keypair
}

fun readPublicKey(filename: String): PublicKey {
    val kf = KeyFactory.getInstance("RSA")

    val publicKeyText = File(filename).readText(charset = Charsets.US_ASCII)
    val publicKeyStr = removeKeySpec(publicKeyText)
    val publicKeyBytes = Base64.getDecoder().decode(publicKeyStr)
    val publicSpec = X509EncodedKeySpec(publicKeyBytes)
    val publicKey = kf.generatePublic(publicSpec)

    return publicKey
}

fun readPrivateKey(filename: String): PrivateKey {
    val kf = KeyFactory.getInstance("RSA")

    val privateKeyText = File(filename).readText(charset = Charsets.US_ASCII)
    val privateKeyStr = removeKeySpec(privateKeyText)
    val privateKeyBytes = Base64.getDecoder().decode(privateKeyStr)
    val privateSpec = PKCS8EncodedKeySpec(privateKeyBytes)
    val privateKey = kf.generatePrivate(privateSpec)

    return privateKey
}

fun readKeyPair(filename: String): KeyPair {
    val publicKey = readPublicKey("$filename.pub")
    val privateKey = readPrivateKey(filename)

    return KeyPair(publicKey, privateKey)
}

fun removeKeySpec(str: String): String {
    var result = str
    result = result.replace("-----BEGIN PUBLIC KEY-----\n", "")
    result = result.replace("\n-----END PUBLIC KEY-----\n", "")
    return result
}

fun addKeySpec(str: String): String {
    var key = "-----BEGIN PUBLIC KEY-----\n"
    key += str
    key += "\n-----END PUBLIC KEY-----\n"
    return key
}
