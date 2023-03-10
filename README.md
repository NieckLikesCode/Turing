# 🎇 Turing
Yet another Minecraft forge ghost/legit cheat aiming to provide an open-source alternative to paid cheats.

I believe the cheating community is stagnant is for two main reasons:
1. There are few open-source ghost cheats out there. Originally I didn't want to do my own project but wanted to contribute to [Raven B+](https://github.com/Kopamed/Raven-bPLUS) (which is a fantastic project by the way). But as I went through the code, I realized that the codebase is pretty messy, and the increased barrier to entry made me discourage contributing.
2. There are a lot of paid upcoming projects that are then abandoned after a short period of time or ratted releases which (understandingly) causes users to distrust developers and cheats

So in the end I decided to start my own project in hopes of overcoming these problems and encouraging many other people to contribute, which is especially needed given the current state of affairs.

## ❓ What this Project is and isn't
For now, Turing is still very rudimentary and needs a lot of work to become as polished as other options. Although Turing is a ghost cheat, it tries not to masquerade as a normal mod (e.g. KeyStrokes or TcpNoDelay). In the case of a screen share, the mod will most likely be discovered as no measures have been taken to prevent it. By default, strings are not encrypted or packages are renamed. These measures can be implemented relatively easily and I would be more than happy if someone in the community would want to spend time on that, but until I receive several requests I will first expand the functions and fix bugs.
Turing is a forge ghost client and this can not be injected or unloaded during runtime.

It does aim to make as many of the implemented features actually usable on the majority of the servers (even Criticals which im particularly proud of) but AntiCheat updates could patch features from one day to the next and ban you for it, so never cheat on an account that you actually value.

## 📁 Notes for developers/contributors 
This project is based on [romangraef's Architectury Loom template for 1.8.9 forge mods](https://github.com/romangraef/Forge1.8.9Template/)

This project uses [DevAuth](https://github.com/DJtheRedstoner/DevAuth), so you can log in using your real
minecraft account.

To run the mod you will need two JDKs, one Java 17 jdk and one Java 1.8 jdk. You can download those
from [here](https://adoptium.net/temurin/releases) (or use your own downloads).

When you import your project into IntelliJ, you need to set the gradle jvm to the Java 17 JDK in the gradle tab, and the
Project SDK to the Java 1.8 JDK. Then click on the sync button in IntelliJ, and it should create a run task
called `Minecraft Client`. If it doesn't then try relaunching your IntelliJ. **Warning for Mac users**: You might have to remove the `-XStartOnFirstThread` vm argument from your run configuration. In the future, that should be handled by the plugin, but for now you'll probably have to do that manually. 

As of writing this the runClient task does not work and there is no intention to fix it. Instead, use the genIntelliJRuns (or genEclipseRuns in case your using Eclipse) and use the generated run configuration to start the client 

To export your project, run the `gradle build` task, and give other people the
file `build/libs/<modid>-<version>-all.jar`
