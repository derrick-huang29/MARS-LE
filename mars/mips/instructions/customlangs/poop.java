
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
