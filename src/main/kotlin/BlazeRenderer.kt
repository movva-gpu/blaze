package works.danyella

import org.lwjgl.glfw.*
import org.lwjgl.opengl.*

class BlazeRenderer {
    private val gameObjects: MutableList<GameObject> = mutableListOf()
    private var window: Long = 0
    private var width = 800
    private var height = 600

    fun init() {
        if (!GLFW.glfwInit()) {
            throw RuntimeException("Failed to initialize GLFW")
        }

        window = GLFW.glfwCreateWindow(width, height, "Blaze Engine", 0, 0)
        if (window == 0L) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        GLFW.glfwMakeContextCurrent(window)

        GL.createCapabilities()

        GL11.glViewport(0, 0, width, height)

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    fun renderScene(deltaTime: Double) {
        // Clear the screen
        clearScreen()

        // Iterate over the game objects and call their render() methods
        gameObjects.forEach { it.render(deltaTime) }

        // Swap the front and back buffers to display the newly rendered frame
        swapBuffers()
    }

    private fun clearScreen() {
        // Clear the screen, setting the background color to black
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
    }

    private fun swapBuffers() {
        // Swap the front and back buffers to display the newly rendered frame
        GLFW.glfwSwapBuffers(window)
    }

    fun addGameObject(gameObject: GameObject) {
        gameObjects.add(gameObject)
    }

    fun removeGameObject(gameObject: GameObject) {
        gameObjects.remove(gameObject)
    }

    fun cleanup() {
        // Terminate GLFW and release any allocated resources
        GLFW.glfwTerminate()
    }

    fun getWindow(): Long {
        return window
    }
}