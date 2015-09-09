## 2048 in reagent

I've made this implementation for fun. There are no transitions. I'm not sure if my implementation matches the real 2048 implementation completely, I tried to figure it out by looking at the source and experimenting. It's fun to play.

## Caution

Code is bad, wrote to make it work, it is still small, easy to read, 270 lines cljs file, check src/cljs/reagent-2048/core.cljs. Hopefully I won't get interested by anything else and I'll make this look good, but this is just a possibility.

### How I build one clone like this? Of course, with better code, maybe with test coverage.

-2. Sorry for the not so nice README, I'm tired of this game. Any doubts shoot me an email(check my github profile), I like helping people.
-1. Build it interactively with the repl, it's fun.
0. When in doubt, check the source code, ask on irc, use the repl, use stackoverflow.
1. Use lein new reagent my-2048-clone, check reagent-template repo for more info on how to run it. In the end you should not need to refresh the page.
2. Copypaste the css from http://codepen.io/camsong/pen/wcKrg/ in resources/public/css/site.css
3. Create the hiccup templates, almost a copy and paste from the same previous url.
4. Add atoms: score(score from the game), score-addition(+score that appears temporarily), tiles(tiles, a matrix 4x4), game-over(stores if the game has ended or not)
5. Adapt hiccup templates so they use the values contained in those atoms, to access that just use @atom-name. This is called derefing an atom.
6. Play a bit with the values of those atoms, do you see a game over screen when game-over atom = true? Nice. If not, check source code and try to understand how can you do it.
7. Now get keyboard events, check hook-keypress-detection!, use the keycodes provided in the source code.
8. So now all is good, you just have to write something that:
8.1. Pops up a 2 random tiles in different places to begin. Those random tiles have 10% chance to be 2 and 10% to be 4.
8.2. After every move you make you add another random tile with the same previous rule.
8.3. When you press right(you can come up with something better, probably), you just have to:
8.3.1. Compare two by two elements the matrice, if they are equal, you make the first element 0 and the other the value of the element*2(times two). Otherwise you do nothing.
8.3.2. Push all the values of the matrix to the right side if they aren't zero.
8.3.3. Send me an e-mail with a better implementation of the movements, I'll learn something new.
8.4 For other keys(right, up...), just do transposition, reverse the matrix and stuff. If you aren't into this kind of math, consider that you can rotate the board using a transposition + matrix reversal(not sure what people call this in linear algebra(?)). You can check in the source the implementation of those functions. This is fun.
9. Have fun.

### Tips

* Use defonce for the atoms, otherwise they'll be reloaded everytime you save the file.
* There's to fn's you need to know to use the atom: reset! and swap!, with reset you just override the value of the atom, swap! gives you the value of the atom, so you can play with it.
* There's more to know about atoms in reagent: watchers and cursors.
* Use the init! method to setup your game.
* People doesn't seem to use many atoms, they usually use just one and access it with cursors(I think).
* For better architecture in reagent, google re-frame.
* Belive in yourself.