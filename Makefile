
# PLUGINNAME=$@

CommandAlias: PLUGINNAME = CommandAlias
CommandAlias: compile

compile:
	echo Compiling ${PLUGINNAME}
	javac -encoding UTF-8 -classpath bukkit.jar:/home/minecraft/plugins/Permissions.jar ./org/chrisinajar/${PLUGINNAME}/*.java ./org/chrisinajar/JarPlugin.java
	jar -cf ${PLUGINNAME}.jar ./org/chrisinajar/JarPlugin.class -C . ./org/chrisinajar/${PLUGINNAME}/ -C ./org/chrisinajar/${PLUGINNAME} plugin.yml
	sudo cp ${PLUGINNAME}.jar /home/minecraft/plugins
	sudo chown minecraft.minecraft /home/minecraft/plugins/${PLUGINNAME}.jar
	smpadmin-console -c 'reload'

