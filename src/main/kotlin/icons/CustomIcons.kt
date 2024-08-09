package icons

import com.intellij.openapi.util.IconLoader


object CustomIcons {

    @JvmField
    val PluginIcon = IconLoader.getIcon("/plugin/icons/plugin-icon.svg", javaClass)

    object Dark {

        @JvmField
        val MigrationFileIcon = IconLoader.getIcon("/plugin/icons/dark/migration-file.svg", javaClass)

        @JvmField
        val MigrationFolderRootIcon = IconLoader.getIcon("/plugin/icons/dark/migration-folder-root.svg", javaClass)
    }

    object White {

        @JvmField
        val MigrationFileIcon = IconLoader.getIcon("/plugin/icons/white/migration-file.svg", javaClass)

        @JvmField
        val MigrationFolderRootIcon = IconLoader.getIcon("/plugin/icons/white/migration-folder-file.svg", javaClass)
    }
}