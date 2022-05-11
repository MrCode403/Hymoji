<p align='center'>
<img width='200px' height='200px' src='https://play-lh.googleusercontent.com/jlkakDRrKrNkjgQe1Mh1W-LOJ-89Iw-vqe4PWf6VWCSlsFKDKawZFVojp0IiBmIUTeQ=s180-rw'>
</p>
  <h2 align='center'>Hymoji - Emojis for Discord, Twitch & Slack</h2>
  
<div align='center'>
  <a href='https://play.google.com/store/apps/details?id=com.nerbly.bemoji&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'>
    <img width='180px%' alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/>
  </a>
</div>

<div align='center'>
  
[![stability-mature](https://img.shields.io/badge/stability-mature-008000.svg)](https://github.com/mkenney/software-guides/blob/master/STABILITY-BADGES.md#mature)
[![Chat](https://img.shields.io/badge/chat-on%20Discord-7289da)](https://discord.gg/nxy2Qq4YP4)
</div>

Hymoji is a platform that works as a library of emojis for Discord, Twitch and Slack provided by emoji.gg.
 
## To-do
### Stability:
Currently, `EmojisActivity` uses 2 fragments to show both main and packs emojis. However, the way it shows emojis is so inconsistent and needs to be improved.
 - Improve the way `HomeActivity` handles server responses.
 - Recreate `PacksEmojisFragment` pagination to work seamlessly with +10K emojis.
 
### Massive refactoring:
This app was initially created using Sketchware, and the current code base uses old methods and approaches to do a certain job. Due to that, the app needs improvements in terms of code.
 - Binding instead of `findViewById`.
 - Split methods to proper classes.

### Improve Readme.md

## Contributing

Fork this repository and contribute back using
[pull requests](https://github.com/ilyassesalama/Hymoji/pulls).

The project is open source because I can't work on it on a regular basis, and there are many users who use the app daily and they need to stay updated with the latest fixes and features. Any contributions, large or small, major features, bug fixes, are welcomed and appreciated, but will
be thoroughly reviewed.

### How to contribute

- Fork the repository to your GitHub account
- Make a branch if necessary
- Clone the forked repository to your local device (optional, you can edit files through GitHub's
  web interface)
- Make changes to files
- (IMPORTANT) Test out those changes
- Create a pull request in this repository
- The repository members will review your pull request, and merge it when they are accepted.

### Commit message

When you've made changes to one or more files, you have to *commit* that file. You also need a
*message* for that *commit*.

You should read [these](https://www.freecodecamp.org/news/writing-good-commit-messages-a-practical-guide/)
guidelines, or that summarized:

- Short and detailed
- Prefix one of these commit types:
   - `feat:` A feature, possibly improving something already existing
   - `fix:` A fix, for example of a bug
   - `style:` Feature and updates related to styling
   - `refactor:` Refactoring a specific section of the codebase
   - `test:` Everything related to testing
   - `docs:` Everything related to documentation
   - `chore:` Code maintenance (you can also use emojis to represent commit types)

Examples:
 - `feat: Speed up compiling with new technique`
 - `fix: Fix crash during launch on certain phones`
 - `refactor: Reformat code at File.java`

