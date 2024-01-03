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
        <version>1.1</version>
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
    implementation 'it.fulminazzo.CustomEnchants:1.1'
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
                this, "telepathy", 2,
                EnchantmentTarget.TOOL, Enchantment.getByName("silk_touch"));
    }
}
```
As you can see, four parameters are required:
- `plugin`: the plugin creating this enchantment. 
The plugin name will NOT be used for the key of the enchantment (1.13+).
This means that its identifier will be `minecraft:enchantment_name`, despite the plugin passed;
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
        CustomEnchantment.getEnchant(0); // Assuming this is the first enchantment created
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
        CustomEnchantmentUtils.newInstance(this, "telepathy", 2, EnchantmentTarget.TOOL, Enchantment.getByName("silk_touch"));
        // Creating new CustomEnchantment lightning.
        CustomEnchantmentUtils.newInstance(this, "lightning", 3, EnchantmentTarget.WEAPON);
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

## EventHandlers
Along with custom enchantments, this library offers a minimal event system to give functionality to your creations:
[EventHandlers](/common/src/main/java/it/fulminazzo/customenchants/handlers/EventHandler.java).
An **EventHandler** is just a wrapper for a given event,
that allows you to ignore some of the filtering usually required.

To better understand, let's make an example:
Say you want to give an effect to the `lightning` enchantment previously created.
All you have to do, either before or after **registering it**, is call the `CustomEnchantment#addEventHandler` method:
```java
CustomEnchantment lightningEnchant = (CustomEnchantment) Enchantment.getByName("lightning");
lightningEnchant.addEventHandler("summonlightningbolt", new EventHandler<>(PlayerTeleportEvent.class, e -> {
    Location to = e.getTo();
    to.getWorld().strikeLightningEffect(to);
}));
```

**CustomEnchants** will be responsible for:
- checking if the called event is of type **PlayerTeleportEvent**;
- checking if the entity of the event is a **living entity** (in this case, we are dealing with a player so no check is necessary);
- checking if the player has equipped either in his **main/off hand** or in his **armor** slots an **item** with the `lightning` effect.

The library will automatically load and unload any required listener for making the event work.

**NOTE:** the one given is a very simple example.
This system will work with any given event, but some additional checks might be required from the user.
Say you want to strike the entity that the player just hit:
```java
CustomEnchantment lightningEnchant = (CustomEnchantment) Enchantment.getByName("lightning");
lightningEnchant.addEventHandler("summonlightningbolt", new EventHandler<>(EntityDamageByEntityEvent.class, e -> {
    Location damagedLocation = e.getEntity().getLocation();
    damagedLocation.getWorld().strikeLightningEffect(damagedLocation);
}));
```
This implementation is considered **wrong** and will generate **some bugs**.
The **CustomEnchants** system has no way of knowing if the developer is interested in a player, or an entity,
nor it can distinguish between two or more entities (like in this case).
This means that if the **damaged** has an item enchanted with **lightning**,
but the **damager doesn't**, this event will still be triggered.
Not only that, but it is **not assured** that the **damager** will be a **player**.

All these checking are left up to the user:
```java
CustomEnchantment lightningEnchant = (CustomEnchantment) Enchantment.getByName("lightning");
lightningEnchant.addEventHandler("summonlightningbolt", new EventHandler<>(EntityDamageByEntityEvent.class, e -> {
    Entity damager = e.getDamager();
    Location damagedLocation = e.getEntity().getLocation();
    if (!(damager instanceof Player)) return;

    PlayerInventory playerInventory = ((Player) damager).getInventory();
    List<ItemStack> itemStacks = new ArrayList<>();
    itemStacks.add(playerInventory.getItemInMainHand());
    itemStacks.add(playerInventory.getItemInOffHand());
    itemStacks.addAll(Arrays.asList(playerInventory.getArmorContents()));
    for (ItemStack itemStack : itemStacks)
        if (itemStack.getEnchantments().containsKey(lightningEnchant)) {
            damagedLocation.getWorld().strikeLightningEffect(damagedLocation);
            break;
        }
}));
```