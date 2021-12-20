# Wolftail
There have been some minecraft mod offering experience of playing as "monster",  actually I mean playing as any non-steve creature but not only creeper. We call such mods "race mod" in terms of they all creating races for us to choose. Traditionally, the implement of these race mods is realized by the so-called "EntityPlayer Decoration".

#### EntityPlayer Decoration ---- Traditional approach
In this approach modder simply add new codes to the class EntityPlayer, or modify codes in it, or just leave it untouched but change the logic in class World, one could also use forge's eventbus to do so, there're loads of ways toward setting up a new race, nevertheless, such setup are all based on EntityPlayer, so all the races built this way couldn't completely(actually 70% is the utmost I think) get rid of the "shadow" of steve, in other words, all these races are varieties of race steve, what you get is essential a steve with new features or restrictions.

This method is ok when your race isn't so different from steve, however a thankless job if you want to make race like creeper, especially when your mod was running in a multimods enviroment, which would emit incredible bugs.

#### Connection Replacement ---- New direction
In order to liberate us from the seemingly inevitable "shadow" of steve, there is another approach named Connection Replacement, the essence of which is to intercept the login request from client, so as to prevent the creation of EntityPlayer, then spawn a new Entity(like EntityCreeper) in World for the client to control.

Such implement of race will make no troubles in server side, in fact, it performs unexpected well, there wouldn't be any EntityPlayer associated to the client in server, what the client plays is truly the race you design, not a steve.

However there exist some deficits in client side, where modders have to deal with the rendering codes which is completely designed for steve(the EntityPlayer), we have mainly to ways in client, creating new game loop or making a fake EntityPlayer so that vanilla codes would work, for the first one, a load of works required however theoretically free of malignant bugs, as for the latter, would easily crash in practice.

#### The mod Wolftail
I put the second method into use and produce this mod called Wolftail, which provides a steve-free race system using Connection Replacement, features low-level api and out-of-box util. It's spacially designed for modders willing to make some race involving no EntityPlayer.
