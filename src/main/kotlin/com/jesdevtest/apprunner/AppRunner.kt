package com.jesdevtest.apprunner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jesdevtest.pages.PageConfig
import com.jesdevtest.pages.PagesConfig
import com.jesdevtest.pages.ServiceConfig
import io.javalin.Context
import io.javalin.Javalin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Enumeration for Request Types
 */
enum class RequestType {GET, POST, PUT, DELETE}
/**
 * App Runner for DevSite
 *
 * Should receive the Javalin App Server from the main app
 * @property app The Javalin App being passed in for management
 *
 */
class AppRunner (private val app: Javalin, private var pagesConfig: PagesConfig = PagesConfig(ArrayList(), ArrayList())) {

    private val Logger = LoggerFactory.getLogger(this.javaClass)

    /*
    Register the basic handlers for the app...
     */
    init {
        Logger.info("Starting up App Runner...")
        loadPages()
    }

    private fun loadPages() {
        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerModule(KotlinModule())
        Logger.debug("Loading pages configuration")
        val pagesConfigFile = ClassLoader.getSystemResource("pages.yml").path.substring(1)
        pagesConfig = Files.newBufferedReader(Paths.get(pagesConfigFile)).use {
            mapper.readValue(it, PagesConfig::class.java)
        }
        Logger.info("%d pages loaded!".format(pagesConfig.pages.size))
        Logger.info("%d services loaded!".format(pagesConfig.services.size))
        Logger.info("Initializing pages...")
        initPages()
        Logger.info("Initializing services")
        initServices()
    }

    private fun initPages() {
        pagesConfig.pages.forEach { page -> initPage(page) }
    }

    private fun initServices() {
        pagesConfig.services.forEach { service -> initService(service) }
    }

    /**
     * Initial a page as a GET request for that path.
     */
    //TODO: Add template engine features?
    private fun initPage(page: PageConfig) {
        val path = page.path
        //check if this page config has a file to load from
        if (page.fileName.isNotBlank()) {
            //grab a handler that'll load this file.
            registerGetHandler(path, {ctx: Context -> ctx.result(FileInputStream(page.fileName)) })
            return
        }
        //return the pageSrc
        registerGetHandler(path, {ctx: Context -> ctx.result(page.pageSrc) })
    }

    private fun initService(service: ServiceConfig) {

    }

    /**
     * Start the Application Server
     * @return this
     */
    fun start(): AppRunner {
        app.start()
        return this
    }

    /**
     * Register a GET handler for a path.
     * @param path The path you want handled
     * @param handler The expression you want executed on a Context
     * @return this
     */
    fun registerGetHandler(path: String, handler: (ctx: Context) -> Unit): AppRunner {
        return registerHandler(RequestType.GET, path, handler)
    }

    /**
     * Register a POST handler for a path.
     * @param path The path you want handled
     * @param handler The expression you want executed on a Context
     * @return this
     */
    fun registerPostHandler(path: String, handler: (ctx: Context) -> Unit): AppRunner {
        return registerHandler(RequestType.POST, path, handler)
    }

    /**
     * Register a PUT handler for a path.
     * @param path The path you want handled
     * @param handler The expression you want executed on a Context
     * @return this
     */
    fun registerPutHandler(path: String, handler: (ctx: Context) -> Unit): AppRunner {
        return registerHandler(RequestType.PUT, path, handler)
    }

    /**
     * Register a DELETE handler for a path.
     * @param path The path you want handled
     * @param handler The expression you want executed on a Context
     * @return this
     */
    fun registerDeleteHandler(path: String, handler: (ctx: Context) -> Unit): AppRunner {
        return registerHandler(RequestType.DELETE, path, handler)
    }

    private fun registerHandler(type: RequestType, path: String, handler: (ctx: Context) -> Unit): AppRunner {
        when(type) {
            RequestType.GET -> app.get(path, handler)
            RequestType.POST -> app.post(path, handler)
            RequestType.PUT -> app.put(path, handler)
            RequestType.DELETE -> app.delete(path, handler)
        }
        return this
    }
}