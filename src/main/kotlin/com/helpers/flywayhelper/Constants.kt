package com.helpers.flywayhelper

object Constants {

    //plugin
    val PLUGIN_NAME = "Flyway Helper"
    val PLUGIN_DIRECTORY_PATH = ".idea/flyway-helper"
    val PLUGIN_README_NAME = "readme.md"
    val PLUGIN_README_IMAGE_NAME = "flyway-helper-image.png"

    val MIGRATION_DIR_PATH = "src/main/resources/db/migration"

    //settings
    val LOCAL_BRANCH = "HEAD"
    val SYNC_BRANCH_SETTING_KEY = "syncBranch"
    val ON_INSTALL_SETTING_KEY = "onInstall"

    val NOTIFICATION_GROUP_ID = "flyway.migration.notification"
    val WELCOME_MESSAGE = "Work with your flyway migrations easier.\n" +
            "Avoid having version conflicts and get an auto-calculated version for any new migration file."

    //actions
    val CONFIGURE_SYNC_BRANCH_ACTION = "configure sync branch"
    val READ_MORE_ACTION = "Read More..."
}