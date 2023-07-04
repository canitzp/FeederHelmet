47.0.1:
- Fixed a major bug, where a client can't join any server due to recipe packets not being handled correct (Fixes #34)

47.0.0:
- Update to 1.20.1 (for real now)

46.0.1:
- Fix bug where previous nbt (like custom name) is lost, when applying the module

46.0.0:
- Update to 1.20/1.20.1

40.1.0:
43.2.0:
45.1.0:
- Reworked recipes:
  - modules are now applied within the smiting table (upgrade)
  - removal of the module is now possible by simply putting th helmet into a crafting grid (Fixes #32)
- 43.2.0 and 45.1.0 only:
  - Add new energy extraction method for mekanism mekasuit 
- 40.1.0 only:
  - New energy extraction method, which enables EnderIO "empowered"-upgrade and IC2-EU (1EU=4FE) capability. Closes #31

45.0.0:
- Update to 1.19.4 (Not backwards compatible)

44.0.0:
- Update to 1.19.3 (Not backwards compatible)

43.1.0:
- New energy extraction method, which enables EnderIO "empowered"-upgrade and IC2-EU (1EU=4FE) capability. Closes #31

43.0.0:
- Update to 1.19.2

41.0.1:
- Fix forge breaking changes within Forge 41.0.94

41.0.0:
- Update to 1.19

40.0.0:
- Update to 1.18.2 and removing 1.18.0 and 1.18.1 from compatability list
- Fix interaction with Cyclic Uncrafting Grinder.
  - The Grinder will take a helmet that does not have the feeder module installed in it and uncraft it into itself and a feeder module, allowing for infinite duplication of the helmet and modules.

36.0.0:
- 1.16.5 Bugfix with new versioning system
- Fix interaction with Cyclic Uncrafting Grinder.
  - The Grinder will take a helmet that does not have the feeder module installed in it and uncraft it into itself and a feeder module, allowing for infinite duplication of the helmet and modules.

38.0.0:
- Update to 1.18
- New versioning numbering, still compatible with semver, but now the major number is equal to the major of the used minecraft forge version

1.12.3:
- Fixed config option 'can_break' not working at all
- Fixed recipe creation on client side

1.12.2:
- Fixed recipe injection again D:. It should work properly now, I have tested it together with SolarHelmet

1.12.1:
- Fixed crash on world join, when a large modpack is used, where helmet names have duplicates (eg: obsidian_helmet), now the recipe registry also includes the modid.

1.12.0:
- Update to 1.17.1 (Forge 1.17.1-37.0.9), not compatible with 1.16.5
- New way of registering recipes, without the access transformer. This may fix some registering issues.

1.11.2:
- Once again fixed recipe creation. This time the server crashes on world load due to RecipeManager#replaceRecipes(...) being @OnlyIn(Dist.CLIENT) in MC-1.16.5, but not in higher versions

1.11.1:
- Update Forge to 1.16.5-36.2.0
- New way of registering recipes, without the access transformer. This may fix some registering issues. (Same as FeederHelmet-1.12.2)
- Fixed crash on world join, when a large modpack is used, where helmet names have duplicates (eg: obsidian_helmet), now the recipe registry also includes the modid. (Same as FeederHelmet-1.12.1)
- Fixed config option 'can_break' not working at all
- Fixed recipe creation on client side

1.11.0:
- Update to 1.16.5 (Forge 1.16.5-36.1.0), not compatible with older versions.
- New config option to disable smeltable foods to be eaten. This prevents eating raw food, but if some mod adds an smelting recipe for cocked food, it can also not be eaten anymore. This is why it is disabled by default. (#24)
- Added defaults to the food_blacklist, like raw or poisonous food. (#22)

1.10.2:
- Fixed strange crafting behaviour (#20)

1.10.1:
- Energy usage is now working for all helmets, using the "Energy" nbt tag, instead of strictly using the capability extraction - Reported by danail23 with the help of FoxMcloud5655
- Added whitelist only config option - Request by zutechugan (Closes #17)
- Compatible with 1.16.4 and 1.16.5

1.10.0:
- Update to 1.16.4 (Forge 1.16.4-35.1.0), not compatible with older versions.
- New config options to overwrite the "eat wait until enough hunger" options, when the player hearts are below or equal 50% (5 Hearts / 10 Damage points)

1.9.0:
- Update to 1.16.2 (Forge 1.16.2-33.0.3), not compatible with 1.16.1

1.8.0:
- Update to 1.16.1 (Forge BETA: 1.16.1-32.0.7), doesn't work with 1.15.x or 1.14.4 anymore :(

1.7.0:
- Update to 1.15.1, still compatible with 1.14
- Added a creative tag, where every possible helmet and the feeder module is located

1.6.2:
- Fixes #12, crash on load when forge version is above 1.14.4-28.0.45 due to the package movement of TickEvent

1.6.1:
- Update to 1.14.4 (still works with 1.14.3 and 1.14.2)
- Item events are now fired correct to assure compatibility with more mods (Closes #11)

1.6.0:
- Update to 1.14

1.5.0:
- Update to 1.13
- New config 'hungry_enough_wait': it waits until no food value is wasted before feeding. default enabled

1.4.4:
- Now works with any helmets by ConstructsArmory
- Fixed crafting issue, where the helmet input stack becomes a feeder helmet, without crafting it.

1.4.3:
- Energy usage is now working for all helmets, using the "Energy" nbt tag, instead of strictly using the capability extraction - Reported by danail23 with the help of FoxMcloud5655
- Added whitelist only config option - Request by zutechugan (Closes #17)

1.4.2:
- Added a creative tab for the module and all possible helmets (same as 1.7.0 for 1.15)

1.4.1:
- Adding Item use finish and start event to be more compatible with mods like Nutrition

1.4.0:
- Feeding now consume the Energy of a helmet if present
- The config 'DURABILITY' now works like the tooltip says

1.3.0:
- Fixed #7 (Helmets consume all durability when no food is found)
- If feeder helmets are repaired in an anvil, the feeder modules aren't destroyed

1.2.0:
- Added Configs for everything

1.1.0:
- Fix: NBT gets removed from the helmets when their get crafted (Fixes #2)

1.0.0:
- Initial version