# Simple Radio

This is a simple radio application for use with the AHT-1.

This application serves as both an out-of-the-box radio, usable as soon as the
device is powered on, and as an example application, useful to developers
wishing to make their own apps for the AHT series.

The Simple Radio application is written in Scala, and uses the public Android
Radio API. As of this writing, the aforementioned API is under heavy
development.

# Building

### Prereqs

* Android SDK (and a valid `ANDROID_HOME` env. variable)
* Working SBT setup.

### Process

* Clone the repo
* `git submodule update --init`
* `sbt android:package-debug`

You will now have an APK in the `target/` directory.

# Updating

Stash or commit any pending changes and switch to the `master` branch.
Run `./update-from-git.sh` from the root of the project.

To apply any new commits to your branches, switch to the branch and run
`git rebase master`.

# Licensing

The Simple Radio application is released under the GPL version 2 or, at your
option, any later version (GPLv2+).
