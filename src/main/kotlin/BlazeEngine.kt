package works.danyella

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.GL_RGBA
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
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
        /**
         * Explanation:
         *
         * `stbi_load mentions` this:
         *
         * ```cpp
         * // Basic usage (see HDR discussion below for HDR usage):
         * //    int x,y,n;
         * //    unsigned char *data = stbi_load(filename, &x, &y, &n, 0);
         * //    // ... process data if not NULL ...
         * //    // ... x = width, y = height, n = # 8-bit components per pixel ...
         * //    // ... replace '0' with '1'..'4' to force that many components per pixel
         * //    // ... but 'n' will always be the number that it would have been if you said 0
         * ```
         *
         * So in [your code](https://github.com/movva-gpu/blaze/blob/82e98cedf772e7aaeec351d5796227c2acf2adcb/src/main/kotlin/BlazeEngine.kt#L79)
         * you allocate 3 direct buffers that are the size of your image.
         *
         * The buffers should be the size of an integer in bytes not the image size.
         * Though a second issue occurs that the byteBuffers use big endian by default based on this:
         *
         * Allocates a new direct byte buffer.
         *
         * The new buffer's position will be zero, its limit will be its capacity, its mark will be undefined, each of its elements will be initialized to zero, and its byte order will be BIG_ENDIAN. Whether or not it has a backing array is unspecified.
         *
         * So basically you would need to set up your 3 buffers as so:
         * ```kt
         *   val width: IntBuffer = ByteBuffer.allocateDirect(Int.SIZE_BYTES).order(ByteOrder.nativeOrder()).asIntBuffer()
         *   val height: IntBuffer = ByteBuffer.allocateDirect(Int.SIZE_BYTES).order(ByteOrder.nativeOrder()).asIntBuffer()
         *   val comp: IntBuffer = ByteBuffer.allocateDirect(Int.SIZE_BYTES).order(ByteOrder.nativeOrder()).asIntBuffer()
         *   comp.put(GL_RGBA)
         *   comp.flip()
         * ```
         *
         * Authors: Someone on the [LWJGL Discord Server](https://discord.gg/6CywMCs)
         *
         */
        fun loadImage(path: String): Pair<ByteBuffer, IntArray> {
            MemoryStack.stackPush().use { stack ->
                val width: IntBuffer = stack.mallocInt(1);
                val height: IntBuffer = stack.mallocInt(1);
                val comp: IntBuffer = stack.mallocInt(1);
                comp.put(GL_RGBA)
                comp.flip()

                val imageSTB = STBImage.stbi_load(path, width, height, comp, 4)
                    ?: throw RuntimeException("Failed to load image: ${STBImage.stbi_failure_reason()}")

                val w = width[0]
                val h = height[0]

                return Pair(imageSTB, intArrayOf(w, h))
            }
        }
    }
}

