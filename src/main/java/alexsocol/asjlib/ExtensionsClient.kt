package alexsocol.asjlib

// PORT: импорты 1.7.10 заменены на 1.20.1; остальное закомментировано до КТ, которой понадобится
import net.minecraft.client.Minecraft

/* PORT: по мере надобности — GL11 фиксированного конвейера в 1.20.1 нет, рендер идёт через PoseStack (КТ-7)
fun glTranslated(d: Double) = GL11.glTranslated(d, d, d)
fun glTranslatef(f: Float) = GL11.glTranslatef(f, f, f)
fun glScaled(d: Double) = GL11.glScaled(d, d, d)
fun glScalef(f: Float) = GL11.glScalef(f, f, f)
*/

val mc: Minecraft get() = Minecraft.getInstance()

/* PORT: по мере надобности — RenderBlocks и достижений в 1.20.1 нет
val renderBlocks = RenderBlocks()

fun EntityClientPlayerMP.hasAchievement(a: Achievement?) = if (a == null) false else statFileWriter.hasAchievementUnlocked(a)
*/
