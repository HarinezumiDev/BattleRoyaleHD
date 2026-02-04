# BattleRoyaleHD
![Java](https://img.shields.io/badge/Java-21-blue)
![Paper](https://img.shields.io/badge/Paper-1.21.11-green)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)


The plugin allows you to manage the Battle Royale world.

## Instalation
Download the Jar file from releases. Move it to your servers plugins folder (`server_dir_name/plugins/`). Start the server. After starting the server plugin should create a folder with the same name as plugins name. Enter the folder and change `config.yml` if you want to change settings of plugin. Text after '#' in configuration file will be ignored.

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
- Border shrinks to a very small size
- Respawning disabled
- Special event (quick death) if enabled in config

## Quick Death in Overtime
**Types:**
- lava
- arrows
- damage_dealt
- box
- hunger
- random
- none -> nothing special. fight until only one players stays alive
_More datails about every event will be added later_


## Commands

`/br setspawn` - set the center of the border and spawnpoint


`/br start <time_in_s_before_start>` - start Battle Royal in <$arg1> seconds

Example: `/br start 3` - starts game in 3 seconds


`/br stop` - instantly stop the game


`/br reload` - reload plugin configuration


## FAQ
### What is Battle Royale?
-> Players fight each other with resources they collect during _mining phase_. The last player alive wins. The world has a border that shrinks over time
### Which Minecraft versions are supported?
-> Officially support only for 1.21.11 at the moment. The stability on other versions is not guaranteed but it may work on 1.21.X at least
