import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

fun main(args: Array<String>) {
    val randomString = generateRandomString()
    println("Generated random string: $randomString")

    val privateKey = readPrivateKey("key")

    val client = Socket("localhost", 8080)

    val toServer = PrintWriter(client.getOutputStream(), true)
    val fromServer = BufferedReader(InputStreamReader(client.getInputStream()))

    println("Sending: $randomString")
    toServer.println(randomString)

    val response = fromServer.readLine()
    println("Received: $response")
    client.close()
    val decrypted = decrypt(response, privateKey)
    println("Decrypted: $decrypted")
    println(randomString == decrypted)
}