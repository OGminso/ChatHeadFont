# Chat Head Font v0.0.5

Minecraft Resource Pack and API for creating dynamic player head chat icons.

![Actionbar Example](https://raw.githubusercontent.com/OGminso/ChatHeadFont/main/Actionbar.png)
![Chat Example](https://raw.githubusercontent.com/OGminso/ChatHeadFont/main/Chat.png)

---

## Note

This is a public resource/guide on how to add player head icons to the game chat. As there isn’t much information on how to do this, I will not provide any further support for this resource unless required.

---

## Features

- **Dynamic Player Heads:** Generate an 8×8 pixel representation of a player's head using their skin.
- **Multiple Skin Sources:** Choose between different skin retrieval services:
    - **MOJANG** (default)
    - **CRAFATAR**
    - **MINOTAR**
    - **MCHEADS**
- **Caching Mechanism:** The API now caches head representations for up to **5 minutes** to reduce asynchronous requests.
- **Multiple Display Options:** Display player heads in chat messages, action bars, bossbars, and more.
- **PlaceholderAPI Integration:** Provides placeholders to easily insert head icons into your server’s messages.

---

## How the Core Plugin Works

The core of Chat Head Font is built around a combination of a dynamic API and a custom resource pack that work together to render player head icons in-game. Here’s how it all comes together:

1. **Skin Retrieval and Processing**  
   The API fetches the player's skin from one of several supported sources (MOJANG, CRAFATAR, MINOTAR, or MCHEADS). It then extracts the 8×8 pixel region corresponding to the player’s face. Each pixel's color is read in hexadecimal format.

2. **Conversion to Chat Components**  
   The extracted pixel colors are converted into an 8×8 grid of `BaseComponent` objects. Each pixel is represented by a specific Unicode character. These components are color-coded with the corresponding pixel color, and negative space characters are added to ensure the grid aligns correctly when displayed in chat.

3. **Custom Texture Pack Integration**  
   The resource pack includes a custom font located in `assets/chathead/font/playerhead.json`. This file maps a set of custom Unicode characters to small bitmap images (the individual pixels). The custom font is then referenced in Minecraft's default font (`assets/minecraft/font/default.json`), enabling the game to render these pixels when the corresponding Unicode characters are used in chat.

4. **Negative Space Adjustment**  
   To create a seamless 8×8 grid, negative space Unicode characters are used to adjust the spacing between pixels. This prevents unwanted gaps and ensures that the reconstructed head icon appears correctly.

### Unicode Characters

This works by coloring a set of unicodes which are set in the Resource Pack under a custom font called "playerhead".  
It is then arranged into a grid of 8x8 of pixels using negative space.

- `\uF001`: Pixel 1 (1st Row)
- `\uF002`: Pixel 2 (2nd Row)
- `\uF003`: Pixel 3 (3rd Row)
- `\uF004`: Pixel 4 (4th Row)
- `\uF005`: Pixel 5 (5th Row)
- `\uF006`: Pixel 6 (6th Row)
- `\uF007`: Pixel 7 (7th Row)
- `\uF008`: Pixel 8 (8th Row)
- `\uF101`: Negative space (Moves back 1px)
- `\uF102`: Negative space (Moves back 2px)

---
## Installation

### Resource Pack Installation
This resource pack was only tested on Minecraft 1.20.4.

Note: By default, the plugin will automatically download and apply the resource pack for every player.
If you are using other plugins that also require resource packs, disable the auto-download feature by setting auto-download-pack in the configuration to false.

### Manual installation:
1. Download the resource pack ZIP file from the Releases section.
2. Place the ZIP file in the `resourcepacks` folder of your Minecraft installation directory.
3. In Minecraft, navigate to **Options > Resource Packs**.
4. Select the ChatHead resource pack and move it to the **Selected Resource** Packs column.
5. Click Done to apply the changes.

### Plugin Installation
Used to generate the player head icon and showcase them.

1. Download the plugin JAR file from the Releases section.
2. Place the JAR file in the `plugins` folder of your Minecraft server directory.
3. Start or restart your Minecraft server.

### Configuration
After installation, a `config.yml` is created in your server’s plugin folder with the following default options:

```yml
# Which skin source to use (MOJANG, CRAFATAR, MINOTAR, or MCHEADS). Default is MOJANG.
skin-source: MOJANG

# Whether the resource pack will be automatically downloaded and applied for every player.
auto-download-pack: true

# Should the server be treated as running in online mode.
online-mode: true

# Whether to display the player head with its hat overlay.
enable-skin-overlay: true

# Should join messages include the player head.
enable-join-messages: true

# Should leave messages include the player head.
enable-leave-messages: true

# Should chat messages include the player head.
enable-chat-messages: true

# Should death messages include the player head.
enable-death-messages: true

# Delay (in seconds) before join messages are broadcast.
# This helps ensure that textures have loaded. Default is 3.
join-messages-delay-seconds: 3

```
---

## PlaceholderAPI Integration

If PlaceholderAPI is installed on your server, Chat Head Font automatically registers a hook that provides the following placeholders:

- **`%chathead%` or `%chathead_self%`**  
  Returns the head of the player using the placeholder.  
  **Usage:** In chat plugins or configuration files, simply use `%chathead%` to show the head of the player who requested it.

- **`%chathead_other:<player>%`**  
  Returns the head of the specified player.  
  **Example:** `%chathead_other:Notch%` will display Notch’s head.

If a player is not found or is offline when using `%chathead_self%`, an appropriate message will be returned.

---

## Adding the Dependency

For developers who wish to use the Chat Head Font API in their own projects, add the following dependency:

### Maven
```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>    
        <dependency>
            <groupId>com.github.OGminso</groupId>
            <artifactId>ChatHeadFont</artifactId>
            <version>API-v0.0.5</version>
        </dependency>
    </dependencies>
```

### Gradle
```gradle
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            mavenCentral()
            maven { url 'https://jitpack.io' }
        }
    }

    dependencies {
        implementation 'com.github.OGminso:ChatHeadFont:API-v0.0.5'
    }
```
---
## API Usage
In your plugin’s main class, initialize the API in the `onEnable()` method:
``` java
@Override
public void onEnable() {
    ChatHeadAPI.initialize(this);
    // ... additional initialization code
}
```

### Retrieving a Player Head
The API now uses caching internally. The default skin source is automatically selected from your configuration (`skin-source`), but you can also specify your own source if desired.
#### Examples
``` java
// Get a player's head (with skin overlay) as a BaseComponent[] (cached for 5 minutes)
BaseComponent[] headComponents = ChatHeadAPI.getInstance().getHead(player);

// Get a player's head without the overlay:
BaseComponent[] headComponentsNoOverlay = ChatHeadAPI.getInstance().getHead(player, false);

// Get a player's head as a legacy formatted String:
String headString = ChatHeadAPI.getInstance().getHeadAsString(player);
```
***Note: The API caches each player’s head for 5 minutes, reducing the need for repeated asynchronous skin fetches.***

---

## Examples
Several example classes are provided in the `src/main/java/net/minso/chathead/Examples` package to showcase different use cases:

## [Examples](https://github.com/OGminso/ChatHeadFont/tree/main/src/main/java/net/minso/chathead/Examples)

![Alt Text](https://raw.githubusercontent.com/OGminso/ChatHeadFont/main/joinleave.png)

*Join & Leave messages*

![Alt Text](https://raw.githubusercontent.com/OGminso/ChatHeadFont/main/Actionbar.png)

*Actionbar*

![Alt Text](https://raw.githubusercontent.com/OGminso/ChatHeadFont/main/Bossbar.png)

*Bossbar*

---
## Contribute
Feel free to contribute to this project. Most pull requests are welcome, whether they add new features, improve the API, or fix bugs.

## License and use
This resource pack is available under the Creative Commons Attribution 4.0 International License (see LICENSE.txt). You are free to use, modify, and distribute this project as long as you include proper attribution.

