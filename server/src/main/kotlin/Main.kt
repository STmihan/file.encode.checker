import java.net.ServerSocket

fun main() {
    val serverSocket = ServerSocket(8080)
    println("Listening on port 8080")
    while (true) {
        val clientSocket = serverSocket.accept()
        println("Accepted connection from ${clientSocket.inetAddress}")

        val fromClient = clientSocket.getInputStream().bufferedReader()

        val readLine = fromClient.readLine()
        println("Received: $readLine")
        val encrypt = encrypt(readLine, readPublicKey("key.pub"))
        println("Encrypted: $encrypt")
        clientSocket.getOutputStream().write(encrypt.toByteArray())
        clientSocket.close()
    }
}
