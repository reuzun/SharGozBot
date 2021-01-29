
# SharGozBot :smile: [Invite](https://discord.com/oauth2/authorize?client_id=802497711877521418&scope=bot&permissions=3226624)
This bot is a discord bot with ability to play songs, delete messages, setting music, quote rooms. The more detailed properties are listed below :
- Play music on a voice channel with keywords or links (It supports live streams via youtube just works with links not keywords.)
- Can list musics that added. Last added song plays first.
- Delete messages on a text channel. That is messaged in last 2 weeks.
- Setting a music channel to let main channel to be clean.
- Setting a quote channel to post random quote (Credit:[here](https://miniwebtool.com/random-quote-generator/)) for every 24 hour.
- Create a [sync-tube](https://sync-tube.de/) room. (A platform to watch online videos simultaneously).
- Typing top 9 news from [Eksi Sozluk](https://eksisozluk.com/) (Reddit of Turkey) to the text channel.
- Calculating the math in the identified syntax in sentences.

![mathwork](https://user-images.githubusercontent.com/73116832/106276405-96acb380-6248-11eb-9719-3e5c49102b61.png)
## How to use it? 
- You can see commands with typing "help to chat.
![help](https://user-images.githubusercontent.com/73116832/106276410-97dde080-6248-11eb-9a86-54821e0b43e6.png)
## Invite link (If you will not host locally.)
- You can invite bot directly via [Invite Link](https://discord.com/oauth2/authorize?client_id=802497711877521418&scope=bot&permissions=3226624).
## How to host it locally ?
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
 - https://discord.com/oauth2/authorize?client_id=YOURCLİENTID&scope=bot (Dont forget to give permission to delete message.)
 
## Used Libraries
- Jsoup
- HtmlUnit
- Discord4j
- LavaPlayer
Thank them all who has contributed to that amazing projects

## Licence
Licensed under [GPL-3.0 License](LICENSE).
