## RUN COMMANDS
```bash
   mvn package
   java -jar target/clash-pong.jar
```

# clash-pong

This is the game created during the event of [Together-Java](https://togetherjava.org/) discord server. 
The twist of the game is that it features similar gameplay as [Clash Royale](https://supercell.com/en/games/clashroyale/).
There are cards that you can use and energy bars which get drained on every card use. Cards cycle as well and have costs.
There are only 8 cards at the moment in the game:

1. Barricade: 3 barricades appear in the middle of the playfield
2. Big Paddle: Paddle gets wider
3. Shrink: Opponent's paddle shrinks
4. Freeze: Opponent's paddle stops moving
5. Smash: Current player paddle's next collision with ball increases ball speed
6. Slow Field: Slows down the ball
7. Strafe: Paddle can move horizontally as well 
8. Goal Shield: Invisible shield appears behind paddle which bounces the ball off

