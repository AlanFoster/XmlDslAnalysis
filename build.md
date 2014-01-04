Continuous Integration
----------------------

### File System Dependency ( Windows Only )

This step is only required for windows. Your mileage may vary for other file systems.

Copy from your IntelliJ Idea bin folder, for instance  `C:\Program Files (x86)\JetBrains\IntelliJ IDEA 13.0\bin`
the following files.

    - IdeaWin32.dll
    - IdeaWin64.dll

Place them into your testing folder directory, under a folder called `bin`

For instance; after running tests your testing directory structure will look like

    - bin
        - IdeaWin32.dll
        - IdeaWin64.dll
    - config
        - ...
    - system
        - ...

Without this step you may run into exceptions such as

    java.lang.UnsatisfiedLinkError: 'IdeaWin64.dll'
    ...
    java.io.FileNotFoundException: Native filesystem .dll is missing, home: ... path here ...

or

    failed: Could not initialize class com.intellij.openapi.vfs.newvfs.ManagingFS$ManagingFSHolder
    [error]     at com.intellij.openapi.vfs.newvfs.ManagingFS.getInstance(ManagingFS.java:35)
    [error]     at com.intellij.openapi.vfs.newvfs.persistent.PersistentFS.getInstance(PersistentFS.java:52)




### Dependencies

### Environment Variables

When running Unit tests it is important to use the following Java Environment Variables

  - -Didea.load.plugins.id=${pluginid}
  - -Didea.home.path=${ideaHome}
  - -Didea.plugins.path=${pluginsPath}

Note, Java environment always have the prefix of `-D`

Example values may be -

  - -Didea.load.plugins.id=yourPlugin
  - -Didea.home.path=C:\Users\alan\.IntelliJIdea13\system\plugins-sandbox\test
  - -Didea.plugins.path=C:\Users\alan\.IntelliJIdea13\system\plugins-sandbox\plugins