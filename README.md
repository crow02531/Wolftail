# Wolftail
There have been some minecraft mods offering experience of playing as monster such as creeper or zombie etc. Traditionally, the implement of these mods is realized by the so-called "EntityPlayer Decoration".

#### EntityPlayer Decoration ---- Traditional approach
In this approach modder simply add new codes to the class EntityPlayer, or modify codes in it, or just leave it untouched but change the logic in class World. One could also use forge's eventbus to do so. There're loads of ways toward setting up a new race, nevertheless, such setup are all based on EntityPlayer. So all the races built this way couldn't completely get rid of the "shadow" of steve. In other words, all these races are varieties of race steve. What you get is essential a steve with new features.

This method is ok when your race isn't so different from steve. However a thankless job if you want to make race like creeper, especially when your mod was running in a multimods enviroment, which would emit incredible bugs.

#### Connection Replacement ---- New direction
In order to liberate us from the seemingly inevitable "shadow" of steve, there is another approach named "Connection Replacement", the essence of which is to intercept the login request from client, so as to prevent the creation of EntityPlayer. Then we will spawn a new Entity(like EntityCreeper) in WorldServer for the client to control.

Such implement of race will make no troubles in server side. In fact, it performs unexpected well. There wouldn't be any EntityPlayer associated to the client in server. What the client plays is truly the race you design, not a steve.

However there exist some deficits in client side, where modders have to deal with the rendering codes which is completely designed for steve(the EntityPlayer). This could be the biggest obstacle we are facing.

#### The mod Wolftail
I put the second method into use and create this mod called Wolftail, which provides a steve-free race system using Connection Replacement, features low-level api and out-of-box utilities. It's spacially designed for modders willing to make some race involving no EntityPlayer.
