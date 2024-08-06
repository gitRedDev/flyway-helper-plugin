package com.helpers.flywayhelper

object Constants {

    //plugin
    const val PLUGIN_NAME = "Flyway Helper"
    const val PLUGIN_DIRECTORY_PATH = ".idea/flyway-helper"
    const val PLUGIN_README_NAME = "readme.md"
    const val PLUGIN_README_IMAGE_NAME = "flyway-helper-image.png"


    //settings
    const val DEFAULT_MIGRATION_DIR_PATH = "src/main/resources/db/migration"
    const val LOCAL_BRANCH = "HEAD"
    const val SYNC_BRANCH_SETTING_KEY = "syncBranch"
    const val ON_INSTALL_SETTING_KEY = "onInstall"
    const val MIGRATION_ROOT_FOLDER_PATH_SETTING_KEY = "onInstall"

    const val NOTIFICATION_GROUP_ID = "flyway.migration.notification"
    const val WELCOME_MESSAGE = "Work with your flyway migrations easier.\n" +
            "Avoid having version conflicts and get an auto-calculated version for any new migration file."

    //actions
    const val CONFIGURE_SYNC_BRANCH_ACTION = "configure sync branch"
    const val READ_MORE_ACTION = "Read More..."
}