package com.jesdevtest.apprunner

import io.javalin.Context
import io.javalin.Javalin

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
class AppRunner (private val app: Javalin) {

    /*
    Register the basic handlers for the app...
     */
    init {

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