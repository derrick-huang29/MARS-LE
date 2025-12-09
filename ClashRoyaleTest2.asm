#Program 2: Double Elixir Cycle

 #Start at 2 elixir
 setelx $t0,2 #$t0 (elixir) = 2

 #Turn on Double Elixir (affects addelx)
 doubleelx #sets $s4 = 1

 #Gain "3" elixir twice; each is doubled to 6
 addelx $t0,3 #elixir: 2 + (3*2) = 8 (clamped to 10)
 addelx $t0,3 #elixir: 8 + (3*2) = 10 (maxed out)

 #Show maxed elixir
 showelx #prints Elixir: 10

 #Initialize card hand in $t2-$t5 as IDs 1,2,3,4
 setelx $t2,1 #card slot 0 = 1
 setelx $t3,2 #card slot 1 = 2
 setelx $t4,3 #card slot 2 = 3
 setelx $t5,4 #card slot 3 = 4

 #Cycle hand once: (1,2,3,4) -> (2,3,4,1)
 cycle #rotates $t2,$t3,$t4,$t5

 #Draw a random card
 randcard #$v0 = random 0-7, prints ID

 #Another cycle
 cycle

 #Draw another random card
 randcard

 #Show final elixir again (still 10)
 showelx

 #Emote
 emote #prints a laugh emote
