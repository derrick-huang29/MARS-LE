#Program 1: Opening Push
 #Start with 5 elixir in $t0
 setelx $t0,5 #$t0 = 5 (elixir), clamped to [0,10]

 #Initialize tower HP
 sethpL $s0,2000 #left tower HP = 2000
 sethpR $s1,2000 #right tower HP = 2000
 sethpK $s2,4000 #king tower HP = 4000

 #Show initial state
 showelx #prints: Elixir: 5
 showtowers #prints HP for left/right/king

 #Try to summon a 3-cost troop using $t0 as elixir
 summon $t1,$t0,3 #if $t0>=3: $t1=1 and $t0-=3 ; else $t1=0

 #Show elixir after summoning attempt
 showelx #see remaining elixir

 #Simulate damage from that troop to the enemy left tower
 damageL $s0,500 #left tower HP = max(0, $s0 - 500)

 #Recompute crowns for destroyed towers
 crownup #if left/right HP <= 0, $s3++ for each

 #Show towers again after the attack
 showtowers

 #Emote
 emote #prints a laugh emote
 