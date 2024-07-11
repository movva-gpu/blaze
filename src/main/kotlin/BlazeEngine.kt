package works.danyella

import org.lwjgl.glfw.GLFW

class BlazeEngine {
    private val gameObjects: MutableList<GameObject> = mutableListOf()
    private var isRunning: Boolean = false
    private var deltaTime = 0.0

    private val renderer: BlazeRenderer = BlazeRenderer()

    init {
        renderer.init()
    }

    fun startGameLoop() {
        if (isRunning) {
            println("The game is already running")
            return
        }

        isRunning = true

        var lastFrameTime = System.nanoTime()

        var iterations = 0

        while (isRunning) {
            if (iterations > 100) {
                println("100 iterations")
                stopGameLoop()
            }
            val currentTime = System.nanoTime()
            deltaTime = (currentTime - lastFrameTime) * 1e-6
            lastFrameTime = currentTime

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
    }

    fun removeGameObject(gameObject: GameObject) {
        gameObjects.remove(gameObject)
    }
}

