package works.danyella

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.GL_RGBA
import org.lwjgl.stb.STBImage
import java.io.File
import java.nio.ByteBuffer
import java.nio.IntBuffer
import javax.imageio.ImageIO

class BlazeEngine {
    private val gameObjects = mutableListOf<GameObject>()
    private var isRunning = false
    private var deltaTime = 0.0

    private val renderer = BlazeRenderer(gameObjects)

    init {
        renderer.init()
    }

    fun startGameLoop() {
        if (isRunning) {
            println("The game is already running")
            return
        }

        isRunning = true

        val startTime = System.nanoTime()
        var lastFrameTime = System.nanoTime()

        var iterations = 0

        while (isRunning) {
            if (iterations % 10 == 0) {
                println("Game loop iteration $iterations")
            }
            val currentTime = System.nanoTime()
            deltaTime = (currentTime - lastFrameTime) * 1e-6
            lastFrameTime = currentTime

            /*renderer.setTimeSinceStart(
                (currentTime - startTime).times(1e-9)
                    .toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
            )*/

            GLFW.glfwPollEvents()

            if (GLFW.glfwWindowShouldClose(renderer.getWindow())) {
                isRunning = false
            }

            gameObjects.forEach { it.update(deltaTime) }

            renderer.renderScene(deltaTime)

            iterations++
        }

        renderer.cleanup()
    }

    fun stopGameLoop() {
        isRunning = false
    }

    fun addGameObject(gameObject: GameObject) {
        gameObjects.add(gameObject)
        renderer.addGameObject(gameObject)
    }

    fun removeGameObject(gameObject: GameObject) {
        gameObjects.remove(gameObject)
        renderer.removeGameObject(gameObject)
    }

    companion object {
        fun loadImage(path: String): Pair<ByteBuffer, IntArray> {
            val imageFile = ImageIO.read(File(path));

            val width: IntBuffer = ByteBuffer.allocateDirect(imageFile.width).asIntBuffer()
            val height: IntBuffer = ByteBuffer.allocateDirect(imageFile.height).asIntBuffer()
            val comp: IntBuffer = ByteBuffer.allocateDirect(GL_RGBA).asIntBuffer()

            val imageSTB = STBImage.stbi_load(path, width, height, comp, 4)
                ?: throw RuntimeException("Failed to load image: ${STBImage.stbi_failure_reason()}")

            val w = width[0]
            val h = height[0]

            return Pair(imageSTB, intArrayOf(w, h))
        }
    }
}

