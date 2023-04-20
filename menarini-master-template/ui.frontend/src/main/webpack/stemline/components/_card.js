import { _findSiblingsWithClass } from '../../mastertemplate/site/_util';


document.addEventListener("DOMContentLoaded", function () {
    const cardTeaser = document.querySelectorAll('.card.teaser');
    for (var i = 0; i < cardTeaser.length; i++) {
        if (cardTeaser[i]) {
            const mySiblings = _findSiblingsWithClass(cardTeaser[i],'card');
            if(mySiblings.length <= 1){
                for (let i = 0; i < mySiblings.length; i++) {
                    mySiblings[i].classList.add('teaser-font-bold');
                }
            }
            if(mySiblings.length == 0){
                cardTeaser[i].classList.add('teaser-font-bold');
            }
            console.log(mySiblings);
        } else {
          console.log("no card teaser found");
        }
    }

    
});