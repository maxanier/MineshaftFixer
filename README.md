# MineshaftFixer
Frequently throw Mineshaft data out of the memory.

I frequently had trouble with my Minecraft server running out of memory. (1-4 players, 3GB RAM, only few mods)
The server was using Forge, Sponge and a few mods and plugins.  
But I was unable to make any of those responsible for the issue. The issue occured with MC1.10 as well as MC.11.  
After analyzing a few memory dumps, it looked like the Mineshaft Structure data (NBTTagCompound) was consuming a lot of memory (about 80% of total usage).  
Since I was not able to find the root of the problem, I decided to write this small mod.  

It frequently (about every 30 secs or so) clears the Mineshaft NBT and structure data (if there are more than 100 entries). 
This might mess with the world gen if generating larger worlds, but it solves the issue. I prefer some bugged Mineshafts over a constantly crashing server.

To install just put the file into your mods folder. That's it. (Maybe check your server console).
https://github.com/maxanier/MineshaftFixer/releases
