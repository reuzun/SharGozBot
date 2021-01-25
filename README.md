# SharGozBot :smile:
[![Github All Releases](https://img.shields.io/github/downloads/reuzun/SharGozBot/total.svg)]()

This bot is a simple bot with the following  properties : 
- Play music on a voice channel.
- Delete messages on a text channel.
- Create a [sync-tube](https://sync-tube.de/) room. (A platform to watch online videos synchronously).
- Typing top 9 news from [Eksi Sozluk](https://eksisozluk.com/) (Reddit of Turkey) to the text channel.
- Calculating the math in the identified syntax in sentences.

eg.

![image](https://user-images.githubusercontent.com/73116832/105742734-efc8ce80-5f4c-11eb-85b2-11e52d290bfc.png)
## Motivation
The motivation at implementing that project was improving skills about using libraries and connecting them together. 
Also it is very gratifying to use your own bot in Discord as a person who spends lots of time on it.
## How to use it ?
- Download latest version of JDK from oracle website.
- Download maven from [here](https://maven.apache.org/).
- Get a token from discord developer portal.
- Download SharGozBot via that page.
 ###  For Windows Users:
- Open cmd --> write cd PATH_TO_SharGozBot --> write mvn clean package
- Then edit your RunBot.bat file.
- Double click to RunBot.bat file to start the SharGozBot.

**Note : Make sure the Path environments of windows about Maven and JDK is correct.**
 ###  Alternative Way (Works for ubuntu users as well)
 - you can type  java -jar Location_To_SharGozBot.jar "Token_Here" to CMD or Terminal.
 ## How To Invite Bot To Your Server ?
 - https://discord.com/oauth2/authorize?client_id=YOURCLİENTID&scope=bot&permissions=NUMBER
 - To get clientId go to discord developer portal.
 - To calculate permission number go to bot section in the portal. or you can directly invite your bot as 
 https://discord.com/oauth2/authorize?client_id=YOURCLİENTID&scope=bot (Dont forget to give permission to delete message.).
 
## Used Libraries
- Jsoup
- HtmlUnit
- Discord4j
- LavaPlayer

Thank them all who has contributed to that amazing projects.

## Licence
Licensed under [MIT license](LICENSE).
