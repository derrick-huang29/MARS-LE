#Program 1: Opening Push
        .text
        .globl main

main:
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

        #Emote (1 = laugh)
        emote 1 #prints a laugh emote

        #Program end

#Program 2: Double Elixir Cycle
        .text
        .globl main

main:
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
        randcard #$v0 = random 0–7, prints ID

        #Another cycle
        cycle

        #Draw another random card
        randcard

        #Show final elixir again (still 10)
        showelx

        #Emote (2 = angry)
        emote 2 #prints an angry emote

#Program 3: Overtime Spell Finish
        .text
        .globl main

main:
        #Set starting elixir and towers
        setelx $t0,4 #exactly enough elixir for one spell
        sethpL $s0,1000 #left tower HP  = 1000
        sethpR $s1,1000 #right tower HP = 1000
        sethpK $s2,3000 #king tower HP  = 3000

        #Show initial HP
        showtowers

        #Turn on overtime (double damage)
        overtime #sets $s6 = 1

        #Cast a spell costing 4 elixir that does 300 base damage
        spell $t0,300 #-4 elixir, both arena towers take 300 (x2 in overtime)

        #Show towers after spell
        showtowers

        #Manually hit the king tower with 400 base damage
        damageK $s2,400 #400 becomes 800 damage in overtime

        #Show towers again
        showtowers

        #Use rage on some stat in $s0 (reusing it as a generic value)
        setelx $s0,10 #treat $s0 as "damage per hit"
        rage $s0 #double it to 20

        #Freeze the arena
        freeze #sets $s5 = 1 (freeze flag), prints message

        #Update crowns if towers are destroyed
        crownup #+1 crown for each arena tower with HP <= 0

        #Final HP snapshot
        showtowers

        #Dramatic victory emote (3 = crying)
        emote 3
