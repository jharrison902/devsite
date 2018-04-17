import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jesdevtest.apprunner.AppRunner
import io.javalin.Context
import io.javalin.Javalin
import java.nio.file.Files
import java.nio.file.Paths

data class Config(
        val contextPath: String,
        val port: Int,
        val defaultContentType: String,
        val defaultCharacterEncoding: String,
        val enableCorsForOrigin: String,
        val enableRouteOverview: String,
        val enableDynamicGzip: Boolean,
        val enableStaticFiles: String,
        val maxBodyForRequestCache: Long
)

private fun loadConfig(): Config {
    val mapper = ObjectMapper(YAMLFactory())
    mapper.registerModule(KotlinModule())
    val configFile = ClassLoader.getSystemResource("config.yml").path.substring(1)
    return Files.newBufferedReader(Paths.get(configFile)).use {
        mapper.readValue(it, Config::class.java)
    }
}

fun main(args: Array<String>) {
    val conf = loadConfig()
    val app = Javalin.create()
    app.apply {
        contextPath(conf.contextPath)
        port(conf.port)
        defaultContentType(conf.defaultContentType)
        defaultCharacterEncoding(conf.defaultCharacterEncoding)
        enableCorsForOrigin(conf.enableCorsForOrigin)
        enableRouteOverview(conf.enableRouteOverview)
        enableStaticFiles(conf.enableStaticFiles)
        maxBodySizeForRequestCache(conf.maxBodyForRequestCache)
    }
    if (conf.enableDynamicGzip) {
        app.enableDynamicGzip()
    }
    val appRunner = AppRunner(app).start()
    appRunner.registerGetHandler("/", {ctx -> ctx.result("Hello, World!")})


}