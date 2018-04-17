package com.jesdevtest.pages

/**
 * Service Configuration for Dev Site
 * This is really the heart of how it works.
 */
data class ServiceConfig (
        val path: String,
        val scriptLanguage: String,
        val scriptFile: String,
        val scriptSrc: String
)