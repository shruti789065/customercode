ISTRUZIONI CSS DA SEGUIRE PER IL PROGETTO 'RELIFE'


Al fine di migliorare la manutenibilità e di rimanere aggiornati con le ultime tendenze CSS3, tentiamo di seguire alcune indicazioni.

### **NO-PX** 

NON utilizzare i px.
La base del sito è di 16px, che corrisponde ad 1rem.
Ai fini della responsività sarà importante evitare l'utilizzo dei px come unità di misura da ora in poi.
In caso di problemi ad effettuare la conversione da px a rem si può utilizzare questo sito.(https://nekocalc.com/px-to-rem-converter). 


#### **!IMPORTANT**

Limitare solamente ad alcuni casi limite l'utilizzo degli !important.
preferire invece l'utilizzo dei @layer per definire le priorità di stile. (https://css-tricks.com/css-cascade-layers/)

#### **CLAMP()** & **CALC()**

Utilizzare ai fini della responsività queste due funzioni per definire le dimensioni degli elementi.

calc(https://developer.mozilla.org/en-US/docs/Web/CSS/calc);
clamp(https://developer.mozilla.org/en-US/docs/Web/CSS/clamp)

#### **:HAS()** & **:IS()**

Queste due pseudo-classi possono essere incredibilmente potenti, permettendi di targettizzare elementi in maniera molo più semplice ed efficace:

:has(https://developer.mozilla.org/en-US/docs/Web/CSS/:has)
:is(https://developer.mozilla.org/en-US/docs/Web/CSS/:is)

#### **MOBILE FIRST**
Per lo sviluppo utilizziamo le regole del mobile firat, vale a dire che prima si esegue il css del mobile, per poi adattarsi alle 
successive risoluzioni attraverso le media-queries.

#### **MEDIA QUERIES**

Utilizzare le media queries predefinite, cosi da evitare che ogni developer possa crearsi le sue e che contrastino così con quelle degli altri. 

  
  @include respond-to(small-devices) {}   ##320px
  
  @include respond-to(mobile) {}          ##480px
   
  @include respond-to(tablet) {}          ##992px
  
  @include respond-to(laptop) {}          ##1200px
  
  @include respond-to(desktop) {}         ##1440px
  
  @include respond-to(high-definition) {} ##1920px


