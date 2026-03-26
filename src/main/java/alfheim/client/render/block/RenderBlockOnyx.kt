package alfheim.client.render.block

import alexsocol.asjlib.render.RenderShaderBlock
import alexsocol.patcher.PatcherPreConfigHandler
import alfheim.api.lib.*
import org.lwjgl.opengl.GL20

val RenderBlockOnyx = RenderShaderBlock(LibRenderIDs.idOnyx, LibShaderIDs.idFresnel) { block ->
	{ shaderId ->
		GL20.glUniform1i(GL20.glGetUniformLocation(shaderId, "useFog"), if (PatcherPreConfigHandler.allowLWJGLTransform && block) 1 else 0)
	}
}
