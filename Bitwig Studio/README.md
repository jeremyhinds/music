# Controller Scripts for Bitwig Studio

Note: the Javascript version of the Taktile controller stopped working for me with BWS 2.1.3 (?). Prefer the Java version.

To build Java extension

1. Install Maven
2. Pull the repository and cd to `music/Bitwig Studio/Extensions/Taktile 49`
3. Execute `mvn install`. This should create a new file named something like `~/Bitwig Studio/Extensions/Taktile49.bwextension`.
4. Put your Taktile 49 controller in "Scene 9 Basic MIDI" mode.

