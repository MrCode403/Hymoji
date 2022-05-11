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
<p align='center'>
<img src="https://repository-images.githubusercontent.com/362965204/c598643a-5af3-4581-a9ba-87022cc2f7c2" >
  </p>
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

## Contributions
The project is open source because I can't work on it on a regular basis, and there are many users who use the app daily and they need to stay updated with the latest fixes and features. Each contribution would be appreciated in any way. Thanks.
