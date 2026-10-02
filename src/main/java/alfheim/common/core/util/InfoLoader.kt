package alfheim.common.core.util

import alexsocol.asjlib.ASJUtilities
import alfheim.AlfheimCore
import alfheim.api.ModInfo
// PORT: StatCollector → прослойка порта; MinecraftForge.MC_VERSION → MCPVersion
import alfheim.port.legacy.StatCollector
import net.minecraftforge.versions.mcp.MCPVersion
import org.w3c.dom.Node
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

object InfoLoader {
	
	val info: MutableList<String> = ArrayList()
	
	var doneChecking = false
	var triedToWarnPlayer = false
	
	fun start() {
		ThreadLoadInfo()
	}
	
	fun getVersionValText(root: Node, targetVersion: String): String {
		val versions = root.childNodes
		for (i in 0 until versions.length) {
			val version = versions.item(i)
			if (!version.hasChildNodes() || !version.hasAttributes() || !version.attributes.getNamedItem("id").nodeValue.endsWith(targetVersion)) continue
			
			val vals = version.childNodes
			for (j in 0 until vals.length) {
				val aval = vals.item(j)
				if (!aval.hasChildNodes()) continue
				
				return aval.childNodes.item(0).nodeValue
			}
		}
		return ""
	}
	
	internal class ThreadLoadInfo: Thread() {
		init {
			name = "Alfheim Version Checker Thread"
			isDaemon = true
			start()
		}
		
		override fun run() {
			try {
				val root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(URL("https://bitbucket.org/AlexSocol/alfheim/raw/" + (if (ModInfo.DEV) "development" else "master") + "/news/" + MCPVersion.getMCVersion() + ".xml").openStream()).documentElement
				val latest = getVersionValText(root, "LATEST")
				
				val onlineVersion = latest.split("-").let { it.getOrNull(1) ?: it.getOrNull(0) ?: "0" }.toInt()
				// PORT: версия порта — «67-port.N»; номер релиза автора — до «-port»
				var localVersion = AlfheimCore.meta.version.toString().substringBefore("-port").replace("\\D".toRegex(), "").toInt()
				
				if (onlineVersion > localVersion)
					info.add(StatCollector.translateToLocalFormatted("alfheimmisc.update", localVersion, onlineVersion))
				
				info.add(getVersionValText(root, "UNIVERSAL"))
				
				var addedLines = false
				while (localVersion < onlineVersion) {
					if (!addedLines) {
						info.add("=====================================================")
						addedLines = true
					}
					
					getVersionValText(root, localVersion.toString()).apply { 
						if (isNotEmpty()) info.add(this)
					}
					
					localVersion++
				}
				
				ASJUtilities.log("Successfully loaded news & version")
			} catch (e: Exception) {
				ASJUtilities.error("Unable to load news & version from official repo. Check your internet connection.", e)
			}
			
			doneChecking = true
		}
	}
}
