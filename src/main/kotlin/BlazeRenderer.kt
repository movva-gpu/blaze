package works.danyella

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import kotlin.math.tan

class BlazeRenderer(private val gameObjects: MutableList<GameObject>, private var timeSinceStart: Double = 0.0) {
    private var window = 0L
    private var width = 800
    private var height = 600

    fun init() {
        if (!GLFW.glfwInit()) {
            val code = GLFW.glfwGetError(null)
            throw RuntimeException("Failed to initialize GLFW: $code")
        }

        window = GLFW.glfwCreateWindow(width, height, "Blaze Engine", 0, 0)
        if (window == 0L) {
            val code = GLFW.glfwGetError(null)
            throw RuntimeException("Failed to create the GLFW window: $code")
        }

        GLFW.glfwMakeContextCurrent(window)

        GL.createCapabilities()

        val path = "src/main/resources/icon.png"

        val iconPair = BlazeEngine.loadImage(path)

        val w = iconPair.second[0]
        val h = iconPair.second[1]

        MemoryStack.stackPush().use { stack ->
            val icon = GLFWImage.malloc(1, stack)
            icon.width(w)
            icon.height(h)
            icon.pixels(iconPair.first)

            GLFW.glfwSetWindowIcon(window, icon)
        }

        STBImage.stbi_image_free(iconPair.first)

//      GL11.glMatrixMode(GL11.GL_PROJECTION)
//      GL11.glLoadIdentity()
//      GL11.glMatrixMode(GL11.GL_MODELVIEW)
//      GL11.glLoadIdentity()
        val aspectRatio = width.toDouble() / height.toDouble()
        val near = 0.1
        val far = 50000.0
        val fov = 90
        val top = tan(Math.toRadians(fov / 2.0)) * near
        val right = top * aspectRatio
        GL11.glFrustum(-right, right, -top, top, near, far)

        GL11.glViewport(0, 0, width, height)

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        val code = GLFW.glfwGetError(null)
        if (code != 0) {
            throw RuntimeException("Failed to initialize OpenGL: ${code.toString(16)}")
        }
    }

    fun renderScene(deltaTime: Double) {
        clearScreen()
        gameObjects.forEach { it.render(deltaTime) }
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