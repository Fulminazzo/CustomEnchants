# CustomEnchants
**CustomEnchants** is a simple Java extension for **Bukkit plugins**.
As the name suggests, it allows for creating new **user-defined enchantments** and adding them into the game.

It also allows for other plugins, like [Essentials](https://essentialsx.net/), to interact with them.

## How to start
First thing first, you will need to import **CustomEnchants** into your project.

| Table of Contents |
|-------------------|
| [Maven](#maven)   |
| [Gradle](#gradle) |

### Maven
First, add the repository:
```xml
<repositories>
    <repository>
        <id>fulminazzo</id>
        <url>https://repo.fulminazzo.it/releases</url>
    </repository>
</repositories>
```
Then, import the project:
```xml
<dependencies>
    <dependency>
        <groupId>it.fulminazzo</groupId>
        <artifactId>CustomEnchants</artifactId>
        <version>1.0</version>
        <!-- DO NOT USE provided -->
        <scope>compile</scope>
    </dependency>
</dependencies>
```
Since we are working with Spigot plugin, if you or other developers are using this library on multiple projects, this might create conflicts during runtime.
To avoid them, you can use relocations with the **Shade Plugin**.

Say your project base package is `it.fulminazzo.testplugin`.
Then, you can add:
```xml
<build>
    <plugins>
        <!-- ... -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.4.1</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <createDependencyReducedPom>false</createDependencyReducedPom>
                        <relocations>
                            <relocation>
                                <pattern>it.fulminazzo.customenchants</pattern>
                                <shadedPattern>it.fulminazzo.testplugin</shadedPattern>
                            </relocation>
                        </relocations>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        <!-- ... -->
    </plugins>
    <!-- ... -->
</build>
```
That's it! You are now ready to start [using CustomEnchants](#using-customenchants).

### Gradle
First, add the repository:
```groovy
repositories {
    maven { url = "https://repo.fulminazzo.it/releases" }
}
```
Then, import the project:
```groovy
dependencies {
    // DO NOT USE compileOnly
    implementation 'it.fulminazzo.CustomEnchants:1.0'
}
```
Since we are working with Spigot plugin, if you or other developers are using this library on multiple projects, this might create conflicts during runtime.
To avoid them, you can use relocations with the **Shade Plugin**.

Say your project base package is `it.fulminazzo.testplugin`.
Then, you can add:
```groovy
plugins {
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}

shadowJar {
    relocate("it.fulminazzo.customenchants", "it.fulminazzo.testplugin")
}

jar {
    dependsOn(shadowJar)
}
```
That's it! You are now ready to start [using CustomEnchants](#using-customenchants).

## Using CustomEnchants
Once you have fully imported **CustomEnchants** in your project, it is time to start using it.
Creating a new enchantment is trivial:
```java
import it.fulminazzo.customenchants.enchants.CustomEnchantment;
import it.fulminazzo.customenchants.enchants.CustomEnchantmentUtils;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CustomEnchantment customEnchantment = CustomEnchantmentUtils.newInstance(
                "telepathy", 2,
                EnchantmentTarget.TOOL, Enchantment.getByName("silk_touch"));
    }
}
```
As you can see, four parameters are required:
- `name`: the name of the enchantment (has to be unique);
- `maxLevel`: the max vanilla level reachable from this enchantment;
- `itemTarget`: the target items for this enchantment (check [EnchantmentTarget](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/enchantments/EnchantmentTarget.html) for more);
- `conflics`: all the enchantments that conflict with our custom one.

You don't have to save the enchantment in any list since it is retrievable either from **Spigot API** or from **CustomEnchants** itself:
```java
public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        /* ... */
        Enchantment.getByName("telepathy");
        CustomEnchantment.CUSTOM_ENCHANTMENTS.get(0); // Assuming this is the first enchantment created
        /* ... */
    }
}
```
However, do not that by default, this enchantment is **NOT** registered.
This means that it will not be available in the game (and will return `null` when using `Enchantment#getByName(String)`).

To register it, you can call the `CustomEnchantment#register()` method on it, or the `registerAll()` static method on the `CustomEnchantment` interface.
**NOTE:** registering twice might result in an error: 
you should always check using `CustomEnchantment#isRegistered()` if the current enchantment **is registered**.

Finally, when **disabling** the plugin, it is **highly advised** to **unregister all** the enchantments.
Here is an example, wrapping all up:

```java
import it.fulminazzo.customenchants.enchants.CustomEnchantment;
import it.fulminazzo.customenchants.enchants.CustomEnchantmentUtils;

public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Creating new CustomEnchantment telepathy.
        CustomEnchantmentUtils.newInstance("telepathy", 2, EnchantmentTarget.TOOL, Enchantment.getByName("silk_touch"));
        // Creating new CustomEnchantment lightning.
        CustomEnchantmentUtils.newInstance("lightning", 3, EnchantmentTarget.WEAPON);
        // Registering all custom enchantments.
        CustomEnchantment.registerAll();
    }

    @Override
    public void onDisable() {
        // Unregistering all custom enchantments.
        CustomEnchantment.unregisterAll();
    }
    
}
```