# Chat Head Font
Minecraft resource pack and API for creating dynamic player head chat icons.

![Alt Text](https://raw.githubusercontent.com/OGminso/ChatHeadFont/main/Actionbar.png)
![Alt Text](https://raw.githubusercontent.com/OGminso/ChatHeadFont/main/Chat.png)


## Note
This is a public resource/guide on how to add playerhead icons to the game chat. As theres not much information on how do this. I will not provide any further support for this resource unless required.

## Usage

### Resource Pack Installation
This was only tested on version 1.20.4

1. Download the resource pack ZIP file from the Releases section.
2. Place the ZIP file in the resourcepacks folder of your Minecraft installation directory.
3. In Minecraft, navigate to Options > Resource Packs.
4. Select the ChatHead resource pack and move it to the Selected Resource Packs column.
5. Click Done to apply the changes.

### Plugin Installation
Used to generate the player head icon and showcase them.

1. Download the plugin JAR file from the Releases section.
2. Place the JAR file in the plugins folder of your Minecraft server directory.
3. Start or restart your Minecraft server.

## Unicode Characters
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

## API Usage
Using the API class is as simple as this.

``` java
ChatHeadAPI chatHeadAPI = ChatHeadAPI.getInstance();
```

## [Examples](https://github.com/OGminso/ChatHeadFont/tree/main/src/main/java/net/minso/chathead/Examples)
- Join & Leave messages 
- ![Alt Text](https://raw.githubusercontent.com/OGminso/ChatHeadFont/main/joinleave.png)
- Actionbar
- ![Alt Text](https://raw.githubusercontent.com/OGminso/ChatHeadFont/main/Actionbar.png)

## License and use

This pack is availible under Creative Commons Attribution 4.0 International (see LICENSE.txt). This gives you a lot of freedom to spread and adapt it to suit your needs. For example, you could alter parts that don't suit your needs and/or merge it into a pack of your own and share it.

Just remember to include attribution. A link back to the repository is appreciated, but not required.

