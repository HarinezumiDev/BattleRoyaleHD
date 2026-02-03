# BattleRoyalHD
Paper plugin for battle royal fights. Developed for 1.21.11 Paper, other versions aren‘t officialy supported but may work

The plugin allows you to manage the Battle Royal world.

## Phases
Battle Royal includes 3 Phases:
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
`/br_settings <mining_border_diameter> <mining_phase_time_in_m> <fight_border_diameter> <fight_phase_time_in_m> <overtime_border_diameter> <overtime_phase_time_in_m>` - set settings for Battle Royal
`/br_setspawn` - set the center of the border and spawnpoint
`/br_start <time_in_s_before_start>` - start Battle Royal in <$arg1> seconds

## FAQ
### What is Battle Royal?
-> Players fight each other with resources they got during _mining phase_. The last player alive wins. The world has a border which changes diameter by the time
