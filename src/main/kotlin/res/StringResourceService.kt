package res

import java.util.*

interface StringResourceService {

    fun getString(key: String): String

}

class StringResourceServiceImpl : StringResourceService {

    private val stringResources: ResourceBundle by lazy {
        ResourceBundle.getBundle("strings.bundle")
    }

    override fun getString(key: String): String {
        return stringResources.getString(key)
    }
}