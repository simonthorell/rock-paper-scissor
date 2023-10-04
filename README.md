## Sten Sax Påse

## Allmänt
Programmera det fantastiskt roliga spelet Sten sax påse

## Betyg
Det går att få G, eller IG på uppgiften. Den är obligatorisk tillsammans med andra
uppgifter, examinerande laborationer och tentamen för att få G på̊ hela kursen. För
att få G på inlämningsuppgiften ska alla krav märkta med G vara uppfyllda.

## Redovisning
Privat github ..bjud in mig som collaborator (marcus.brederfalt@systementor.se) så
jag kan se kod i repo(s). Skriv in länk till ert repo i mappen Inlämningsuppgift nedan -
det är där jag kopplat repo till person och betygsätter. Jag ska kunna ladda ner er
kod och trycka F5 så ska allt funka!
Muntlig redovisning sker med mig, högst 5-10 minuter.

## Krav för godkänt
● Programmet ska fungera enligt exemplet ovan eller på annat sätt kunna
redovisa logiken bakom sten sax och påse.  
● Arbetet ska bedrivas i grupp och alla medlemmar ska vara delaktiga och förstå
koden.  
● Programmet ska vara utvecklat objektorienterat, tänk klasser och metoder.

## Git / Github

# klona Github repo från din terminal: 
gh repo clone simonthorell/StenSaxPase

# Pusha endast commits till "dev" branch:
git checkout dev

# Hämta senaste commits från collaborators:
git pull

# För att stage och commit:
git add .
git commit -m "SKRIV DIN KOMMENTAR HÄR PÅ ENGELSKA"
git push

# För att merge dev branch to main när version av program är redo för produktion:
git checkout main
git merge dev
git push origin main