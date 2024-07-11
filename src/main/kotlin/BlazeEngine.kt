package works.danyella

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.nio.IntBuffer

class BlazeEngine {
    private val gameObjects = mutableListOf<GameObject>()
    private var isRunning = false
    private var deltaTime = 0.0

    private val renderer = BlazeRenderer(gameObjects)

    init {

        val width: IntBuffer = IntBuffer.allocate(1)
        val height: IntBuffer = IntBuffer.allocate(1)
        val comp: IntBuffer = IntBuffer.allocate(1)

        val iconSTB = STBImage.stbi_load("src/main/resources/icon.png", width, height, comp, 4)
            ?: throw RuntimeException("Failed to load image: ${STBImage.stbi_failure_reason()}")

        val w = width[0]
        val h = height[0]

        MemoryStack.stackPush().use { stack ->
            val icon = GLFWImage.malloc(1, stack)
            icon.width(w)
            icon.height(h)
            icon.pixels(iconSTB)

            renderer.init()

            GLFW.glfwSetWindowIcon(renderer.getWindow(), icon)
        }

        STBImage.stbi_image_free(iconSTB)
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

}

