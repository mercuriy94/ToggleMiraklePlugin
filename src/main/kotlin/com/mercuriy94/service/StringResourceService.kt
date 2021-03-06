package com.mercuriy94.service

import com.intellij.openapi.components.Service
import java.util.*

@Service
class StringResourceService {

    private val stringResources: ResourceBundle by lazy {
        ResourceBundle.getBundle("strings.bundle")
    }

    fun getString(key: String): String {
        return stringResources.getString(key)
    }
}

const val ERROR = "error"
const val TOGGLE_MIRAKLE = "toggle_mirakle"
const val ERROR_CREATE_LOCAL_PROPS = "error_create_local_props"
const val ERROR_CREATE_MIRAKLE_TOGGLE_PROPERTY = "error_create_mirakle_toggle_property"
