package works.danyella

fun main() {
    val engine = BlazeEngine()

    val cube = Cube(-5f, 0f, -5f, NonUniformScale(1f, 3f, 1f))
    val cube2 = Cube(5f, 0f, -5f, UniformScale(1f))

    engine.addGameObject(cube)
    engine.addGameObject(cube2)

    engine.startGameLoop()
}
