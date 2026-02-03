# BattleRoyaleHD
![Java](https://img.shields.io/badge/Java-21-blue)
![Paper](https://img.shields.io/badge/Paper-1.21.11-green)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)
<br>

The plugin allows you to manage the Battle Royale world.

## Phases
Battle Royale includes 3 Phases:
- Mining Phase*
- Fight Phase*
- Overtime

**Mining Phase:**
- PvP is disabled
- Border is static
- Respawn after death

**Fight Phase:**
- PvP is enabled
- The border gets smaller
- You cannot respawn anymore

**Overtime:**
- Optional phase. Starts only if there's no winner after _Fight Phase_
- PvP is enabled
- Border gets very small
- You cannot respawn

## Commands

`/br setspawn` - set the center of the border and spawnpoint<br> <br>
`/br start <time_in_s_before_start>` - start Battle Royal in <$arg1> seconds<br>
Example: `/br start 3` - starts game in 3 seconds  <br>
`/br stop` - instantly stop the game

`/br reload` - reload plugin configuration
## FAQ
### What is Battle Royale?
-> Players fight each other with resources they got during _mining phase_. The last player alive wins. The world has a border which changes diameter by the time
### Which Minecraft versions are supported?
-> Officialy support only for 1.21.11 at the moment. The stability on other versions is not garanted but it may work on 1.21.X at least
