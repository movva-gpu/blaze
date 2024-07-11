package works.danyella

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20

class Cube(
    private val x: Float,
    private val y: Float,
    private val z: Float,
    private val scale: Scale
) : GameObject {
    private val vertices = floatArrayOf(
        // Front face
        -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f,

        // Back face
        -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f,

        // Top face
        -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,

        // Bottom face
        -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f,

        // Left face
        -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f,

        // Right face
        1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f
    )

    private val colors = floatArrayOf(
        1.0f, 0.0f, 0.0f, // Red
        0.0f, 1.0f, 0.0f, // Green
        0.0f, 0.0f, 1.0f, // Blue
        1.0f, 1.0f, 0.0f, // Yellow
        1.0f, 0.0f, 1.0f, // Magenta
        0.0f, 1.0f, 1.0f  // Cyan
    )

    private val indices = intArrayOf(
        0, 1, 2, 2, 3, 0, // Front face
        4, 5, 6, 6, 7, 4, // Back face
        8, 9, 10, 10, 11, 8, // Top face
        12, 13, 14, 14, 15, 12, // Bottom face
        16, 17, 18, 18, 19, 16, // Left face
        20, 21, 22, 22, 23, 20  // Right face
    )

    override fun update(deltaTime: Double) {
        // No need to, yet
    }

    override fun render(deltaTime: Double) {
        println("Rendering cube")
        GL11.glPushMatrix()
        GL11.glTranslatef(x, y, z)

        when (scale) {
            is NonUniformScale -> GL11.glScalef(scale.x, scale.y, scale.z)
            is UniformScale -> GL11.glScalef(scale.value, scale.value, scale.value)
        }

        val vboVertices = GL15.glGenBuffers()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertices)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW)

        val vboColors = GL15.glGenBuffers()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboColors)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colors, GL15.GL_STATIC_DRAW)

        val vboIndices = GL15.glGenBuffers()
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndices)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW)

        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)

        GL20.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboVertices)
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0)

        GL20.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboColors)
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0)

        GL11.glDrawElements(GL11.GL_TRIANGLES, indices.size, GL11.GL_UNSIGNED_INT, 0)

        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)

        GL15.glDeleteBuffers(vboVertices)
        GL15.glDeleteBuffers(vboColors)
        GL15.glDeleteBuffers(vboIndices)

        GL11.glPopMatrix()
    }
}
