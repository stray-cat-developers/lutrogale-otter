package io.mustelidae.otter.lutrogale.api.permission

object RoleHeader {

    sealed interface Role

    data object XAdmin : Role {
        const val NAME = "admin ID"
        const val KEY = "x-admin-id"
        const val DATA_TYPE = "long"
        const val PARAM_TYPE = "header"
    }

    data object XPartner : Role {
        const val NAME = "partner ID"
        const val KEY = "x-partner-id"
        const val DATA_TYPE = "long"
        const val PARAM_TYPE = "header"
    }

    data object XUser : Role {
        const val NAME = "user ID"
        const val KEY = "x-user-id"
        const val DATA_TYPE = "long"
        const val PARAM_TYPE = "header"
    }

    data object XSystem : Role {
        const val NAME = "System ID"
        const val KEY = "x-system-id"
        const val DATA_TYPE = "String"
        const val PARAM_TYPE = "header"
    }
}
