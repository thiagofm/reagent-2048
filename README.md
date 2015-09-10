## 2048 in reagent

I've made this implementation for fun. There are no transitions. I'm not sure if my implementation matches the real 2048 implementation completely, I tried to figure it out by looking at the source and experimenting. It's fun to play, but also very fun to write.


## Caution

Code is bad, wrote to make it work, it is still small, easy to read, 270 lines cljs file, check src/cljs/reagent-2048/core.cljs.


## How I build one clone like this? Of course, with better code, maybe with test coverage.

1. Sorry for the not so nice README, I'm tired of this game. Any doubts shoot me an email(check my github profile), I like helping people.
2. Build it interactively with the repl, it's fun.
3. When in doubt, check the source code, ask on irc, use the repl, use stackoverflow.
4. Use lein new reagent my-2048-clone, check reagent-template repo for more info on how to run it. In the end you should not need to refresh the page.
5. Copypaste the css from http://codepen.io/camsong/pen/wcKrg/ in resources/public/css/site.css
6. Create the hiccup templates, almost a copy and paste from the same previous url.
7. Add atoms: score(score from the game), score-addition(+score that appears temporarily), tiles(tiles, a matrix 4x4), game-over(stores if the game has ended or not)
8. Adapt hiccup templates so they use the values contained in those atoms, to access that just use @atom-name. This is called derefing an atom.
9. Play a bit with the values of those atoms, do you see a game over screen when game-over atom = true? Nice. If not, check source code and try to understand how can you do it.
10. Now get keyboard events, check hook-keypress-detection!, use the keycodes provided in the source code.
11. So now, you have to write some logic to change the tiles given the keys pressed.


## Game logic

* Start adding 2 random tiles in the board. Those random tiles have 90% chance to be 2 and 10% to be 4.
* After every move you make you add another random tile with the same previous rule.

When you press right, you just have to:

* Compare two by two elements the matrice, if they are equal, you make the first element 0 and the other the value of the element*2(times two). Otherwise you do nothing.
* Push all the values of the matrix to the right side if they aren't zero.

For other keys(right, up...), just do transposition, reverse the matrix and whatever necessary. If you aren't into this kind of math, consider that you can rotate the board using a transposition + matrix reversal(not sure what people call this in linear algebra(?)). You can check in the source the implementation of those functions. This is fun.

I'm sure it's possible to come up with very elegant solutions for that. If you do, please write me.



## Tips

* Use defonce for the atoms, otherwise they'll be reloaded everytime you save the file.
* There's two fn's you need to know to use the atom: reset! and swap!, with reset you just override the value of the atom, swap! gives you the value of the atom, so you can play with it.
* There's more to know about atoms in reagent: watchers and cursors.
* Use the init! method to setup your game.
* It's not a good idea to use many atoms in a real world application, one is enoght.
* For better architecture, check https://github.com/Day8/re-frame
* Belive in yourself.