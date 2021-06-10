/** App.js
 * This file contains the game logic for the main activity
 * All objects of the player, enemy, and power-up classes are instantiated here.
 */

// ---------------------------GLOBAL VARIABLES----------------------------------

'use strict';

let hearts = 3;

let score = 0;

let score_multiplier = 1;

let player; // stores the player object for handleJoyStick() and gainInvincibilityBonus()

// ---------------------------GLOBAL FUNCTIONS----------------------------------

/* NOTE: Some functions are intentionally defined globally rather than inside their appropriate
   classes because Android can only call global JS functions. Examples of such functions include:
        1) All gem bonus functions, called by their respective Android gem buttons
        2) The joystick listener function, called by an Android thread
        3) The setPlayerSprite function, called when selecting a character from the Android rules activity */

// Temporarily increases the score multiplier to 2 (GREEN GEM)
function doubleScoreBonus() {
    score_multiplier = 2;
    setTimeout(() => {
      score_multiplier = 1;
    }, 10000);
}

// Temporarily slows down all enemies (BLUE GEM)
function slowEnemiesBonus() {
   // for each enemy, redefine its update function using a slower speed
   allEnemies.forEach(enemy => enemy.update = function(dt) {
     if (this.x < 500) {
       this.x += (50 * dt);
     }
     else if (this.x > 500) {
       this.reset();
     }

     if (this.x < player.x + 20 && this.x + 50 > player.x && this.y < player.y + 50 && this.y + 30 > player.y && this.shield === false) {
       player.reset();
       loseHeart();
     }

     if (hearts === 0) {
       gameOver('lose');
     }
  });

   // after 10 seconds, for each enemy, redefine its update function using the original speed
   setTimeout(() => {
     Android.startSpeedUpAudio();
     allEnemies.forEach(enemy => enemy.update = function(dt) {
      if (this.x < 500) {
        this.x += (150 * dt);
      }
      else if (this.x > 500) {
        this.reset();
      }

      if (this.x < player.x + 20 && this.x + 50 > player.x && this.y < player.y + 50 && this.y + 30 > player.y && this.shield === false) {
        player.reset();
        loseHeart();
      }

      if (hearts === 0) {
        gameOver('lose');
      }
   });
   }, 10000);
}

// Temporarily grants invincibility from enemy collision (ORANGE GEM)
function gainInvincibilityBonus(sprite) {
    let s = sprite;
    s = s.substr(0, s.lastIndexOf(".")) + "_shield.png"; // tack on "shield" to the sprite path
    player.sprite = s;

    allEnemies.forEach(enemy => enemy.shield = true); // turn on shield flag

    setTimeout(() => {
      allEnemies.forEach(enemy => enemy.shield = false); // turn off shield flag
      player.sprite = sprite;
    }, 5000);
}

// Moves player based on current direction of Android joystick.
function handleJoyStick(direction) {
  if (direction === 'left' && player.x > 0) {
          player.x -= 10;
  }
  if (direction === 'right' && player.x < 400) {
          player.x += 10;
  }
  if (direction === 'up' && player.y > 5) {
          player.y -= 10;
  }
  if (direction === 'down' && player.y < 400) {
          player.y += 10;
  }
}

/* Receives the character sprite from the Android rules activity
   and sets the player sprite to it. */
function setPlayerSprite(sprite) {
    player.sprite = sprite;
}

// Removes heart
function loseHeart() {
  if (hearts === 3) {
    document.querySelector('.heart3').classList.add('lose');
    hearts = 2;
  } else if (hearts === 2) {
    document.querySelector('.heart2').classList.add('lose');
    hearts = 1;
  } else if (hearts === 1) {
    document.querySelector('.heart1').classList.add('lose');
    hearts = 0;
  }
}

// Adds heart
function gainHeart() {
  if (hearts === 1) {
    document.querySelector('.heart2').classList.remove('lose');
    hearts = 2;
  } else if (hearts === 2) {
    document.querySelector('.heart3').classList.remove('lose');
    hearts = 3;
  }
}

// Updates score by 1
function updateScore(num) {
  score += (num * score_multiplier);
  document.querySelector('.score-number').innerHTML = score;
}

// Returns a random number from the specified array
function randNum(array) {
  return array[Math.floor(Math.random() * array.length)];
};

/* Listens for key presses and passes the keys to the
   Player.handleMouseInput() method. Immediately invoked. */
(function keyListener() {
  document.addEventListener('keyup', function(e) {
    const allowedKeys = {
        37: 'left',
        38: 'up',
        39: 'right',
        40: 'down'
    };

    player.handleMouseInput(allowedKeys[e.keyCode]);
  });
})();

/* Listens for swipes on the screen and passes changes in the X and Y
   coordinates to the Player.handleTouchInput() method. Immediately invoked. */
(function touchListener() {
  let clientX, clientY;

  // Save the first touch coordinates
  document.addEventListener('touchstart', function(e) {
    clientX = e.touches[0].clientX;
    clientY = e.touches[0].clientY;
  }, false);

  // compute the change in X and Y coordinates
  // between the ending touch and the first touch
  document.addEventListener('touchend', function(e) {
    let deltaX, deltaY;
    deltaX = e.changedTouches[0].clientX - clientX;
    deltaY = e.changedTouches[0].clientY - clientY;

    // pass the data ...
    player.handleTouchInput(deltaX, deltaY);
  }, false);
})();

/* Resets each enemy with random x and y values when
  returning focus to the window */
window.onfocus = function() {
  for (let i = 0; i < allEnemies.length; i++) {
    allEnemies[i].x = Math.random() * 450;
    allEnemies[i].y = Math.random() * 350;
  }
}

// Prepares the game-over state
function gameOver(state) {
  allEnemies = [];
  gem_green = null;
  gem_blue = null;
  gem_orange = null;
  player.handleMouseInput = undefined;
  player.handleTouchInput = undefined;
  Android.disableJoyStick();
  Android.resetGems();

  if (state === 'win') {
    document.querySelector('.gameover-heading').innerHTML = "YOU WON!"
    Android.startWinAudio();
  } else {
    Android.startGameOverAudio();
  }

  document.querySelector('.gameover-popup').style.display = 'flex';
  document.querySelector('.gameover-background').style.display = 'flex';
  document.querySelector('.gameover-button').addEventListener('click', function() {
    location.reload();
  });

}

// -----------------------------CLASSES-----------------------------------------

// A power-up super class
class PowerUp {
    constructor() {
      setTimeout(() => { // after a random delay, instantiate this power-up
        this.sprite;
        this.x = randNum([25, 125, 225, 325, 425]);
        this.y = randNum([115, 200, 285, 370]);
        this.hasCollided = false;
      }, randNum([5000, 6000, 7000, 8000, 9000, 10000, 11000, 12000, 13000, 14000, 15000,
                  16000, 17000, 18000, 19000, 20000]));
    }

    // Power-up and player collision
    update(dt) {
      if (this.x < player.x + 30 && this.x + 60 > player.x && this.y < player.y + 50 && this.y + 30 > player.y) {

        if (this.hasCollided) { // prevents multiple collisions on the same power-up
            return;
        }

        this.sprite = './gem_blank.png' // hide the power-up
        this.bonus();
        this.reset();
        this.hasCollided = true;
        Android.startGemAudio();
      }
    }

    // Actions to perform on obtaining this power-up
    bonus() {
      updateScore(1);
    }

    // After a random delay, reset the power-up at a random location
    reset(image) {
      setTimeout(() => {
        this.sprite = image;
        this.x = randNum([25, 125, 225, 325, 425]);
        this.y = randNum([115, 200, 285, 370]);
        this.hasCollided = false;
      }, randNum([10000, 11000, 12000, 13000, 14000, 15000, 16000, 17000, 18000, 20000]));
    }

    // Draws the power-up on the screen
    render() {
      ctx.drawImage(Resources.get(this.sprite), this.x, this.y);
    }
}

// Green Gem
class Gem_Green extends PowerUp {
    constructor() {
      super();
      this.sprite = './gem_green.png';
    }

    bonus() {
      super.bonus();
      Android.highlightGem("green");
      console.log("The green gem's bonus function worked!");
    }

    reset() {
      super.reset('./gem_green.png');
    }
}

// Blue Gem
class Gem_Blue extends PowerUp {
    constructor() {
      super();
      this.sprite = './gem_blue.png';
    }

    bonus() {
      super.bonus();
      Android.highlightGem("blue");
      console.log("The blue gem's bonus function worked!");
    }

    reset() {
      super.reset('./gem_blue.png');
    }
}

// Orange Gem
class Gem_Orange extends PowerUp {
    constructor() {
      super();
      this.sprite = './gem_orange.png';
    }

    bonus() {
      super.bonus();
      Android.highlightGem("orange");
      console.log("The orange gem's bonus function worked!");
    }

    reset() {
      super.reset('./gem_orange.png');
    }
}

// Extra life heart
class Heart_Life extends PowerUp {
    constructor() {
      super();
      this.sprite = './heart_life.png';
    }

    bonus() {
      if (hearts === 3) {
        super.bonus();
      } else {
        gainHeart();
      }
      console.log("The heart's bonus function worked!");
    }

    reset() {
      super.reset('./heart_life.png');
    }
}

// Enemies our player must avoid
class Enemy {
  constructor() {
    this.sprite = './enemy_bug.png';
    this.x = randNum([45, 90, 135, 180, 225, 260, 305, 350, 395, 450]);
    this.y = randNum([60, 145, 230, 310]);
    this.shield = false;
  }

  /* Update the enemy's position,
     Parameter: dt = a time delta between ticks */
  update(dt) {
    if (this.x < 500) {
      this.x += (150 * dt);
    }
    else if (this.x > 500) {
      this.reset();
    }

    // Enemy and player collision
    if (this.x < player.x + 20 && this.x + 50 > player.x && this.y < player.y + 50 && this.y + 30 > player.y && this.shield === false) {
      Android.startHitAudio();
      player.reset();
      loseHeart();
    }

    // GAME OVER
    if (hearts === 0) {
      gameOver('lose');
    }
  }

  // Resets enemies to the left with a random y-value
  reset() {
    this.x = 0;
    this.y = randNum([60, 145, 230, 310]);
  }

  // Draws the enemy on the screen
  render() {
    ctx.drawImage(Resources.get(this.sprite), this.x, this.y);
  }
}

// Player whose goal is to cross into water
class Player {
  constructor() {
    this.sprite = './char_empty.png';
    this.x = 200;
    this.y = 400;
  }

  /* Update the player's position
     when the player reaches the water */
  update(dt) {
    if (player.y < 20) {
      Android.startWaterAudio();
      player.reset();
      updateScore(1);

      // WIN
      if (score === 50) {
        gameOver('win');
      }
    }
  }

  // Resets player to original position if water is reached or if hit by enemy
  reset() {
    this.x = 200;
    this.y = 400;
  }

  // Draws the player on the screen
  render() {
    ctx.drawImage(Resources.get(this.sprite), this.x, this.y);
  }

  // Moves player based on key press
  handleMouseInput(arrow) {
    if (arrow === 'left' && this.x > 0) {
        this.x -= 100;
    }
    if (arrow === 'right' && this.x < 400) {
        this.x += 100;
    }
    if (arrow === 'up' && this.y > 5) {
        this.y -= 95;
    }
    if (arrow ==='down' && this.y < 400) {
        this.y += 95;
    }
  }

  // Moves player based on swipes
  handleTouchInput(deltaX, deltaY) {
    if (deltaX < 0 && this.x > 0 && deltaY > 0 && deltaY < 200) { // left
        this.x -= 100;
    } else if (deltaX > 0  && this.x < 400 && deltaY > 0 && deltaY < 200) { // right
        this.x += 100;
    }

    if (deltaY < 0 && this.y > 5 && deltaX > 0 && deltaX < 200) { // up
        this.y -= 95;
    } else if (deltaY > 0 && this.y < 400 && deltaX > 0 && deltaX < 200) { // down
        this.y += 95;
    }
  }
}

// ---------------------------INSTANTIATION-------------------------------------

let allEnemies = [
  new Enemy(this.x, this.y),
  new Enemy(this.x, this.y),
  new Enemy(this.x, this.y),
  new Enemy(this.x, this.y),
  new Enemy(this.x, this.y),
  new Enemy(this.x, this.y)
];

player = new Player();

let gem_green = new Gem_Green(this.x, this.y);
let gem_blue = new Gem_Blue(this.x, this.y);
let gem_orange = new Gem_Orange(this.x, this.y);
let heart_life = new Heart_Life(this.x, this.y);
