package ru.virgil.spring.tools.testing

import org.apache.http.client.utils.URIBuilder

interface UriHelper {

    fun uri() = URIBuilder()

}
