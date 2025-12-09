package mars.mips.instructions.customlangs;

import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.instructions.syscalls.*;
import mars.*;
import mars.util.*;
import java.util.*;
import java.io.*;
import mars.mips.instructions.*;
import java.util.Random;

/**
 * Custom Clash Royale themed assembly language for MARS.
 *
 * Conventions:
 *   $t0 (8)  - elixir
 *   $t1 (9)  - extra resource
 *   $t2- $t5 (10-13) - card slots / hand
 *   $s0 (16) - left tower HP
 *   $s1 (17) - right tower HP
 *   $s2 (18) - king tower HP
 *   $s3 (19) - crowns
 *   $s4 (20) - double elixir flag (0/1)
 *   $s5 (21) - freeze flag (0/1)
 *   $s6 (22) - overtime flag (0/1)
 *   $v0 (2)  - generic result / card ID
 */
public class test extends CustomAssembly {

    private static final int REG_V0 = 2;
    private static final int REG_T0 = 8;
    private static final int REG_T2 = 10;
    private static final int REG_T3 = 11;
    private static final int REG_T4 = 12;
    private static final int REG_T5 = 13;

    private static final int REG_S0 = 16;  // left tower HP
    private static final int REG_S1 = 17;  // right tower HP
    private static final int REG_S2 = 18;  // king tower HP
    private static final int REG_S3 = 19;  // crowns
    private static final int REG_S4 = 20;  // double elixir flag
    private static final int REG_S5 = 21;  // freeze flag
    private static final int REG_S6 = 22;  // overtime flag

    @Override
    public String getName() {
        return "Clash Royale";
    }

    @Override
    public String getDescription() {
        return "Simulate a Clash Royale match: elixir, towers, crowns, and cards!";
    }

    @Override
    protected void populate() {

        SystemIO.printString(
            "Welcome to the Clash Royale Arena!\n" +
            "Convention:\n" +
            "  $t0 = elixir, $s0/$s1/$s2 = tower HP (L/R/King), $s3 = crowns.\n" +
            "  $t2-$t5 = your 4-card hand, $v0 = card result / random.\n\n"
        );

        // ==========================================================
        // BASIC INSTRUCTIONS (10)
        // ==========================================================

        // 1) setelx $t0,5  -- set elixir to immediate, clamped [0,10]
        // I-FORMAT: 1 register + immediate
        // mask has two operand groups: fffff (reg), t... (imm)
        instructionList.add(
            new BasicInstruction("setelx $t0,10",
                "Set elixir: register gets signed 16-bit immediate (clamped to [0,10])",
                BasicInstructionFormat.I_FORMAT,
                "100000 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();   // [reg, imm]
                        int destReg = operands[0];
                        int imm = operands[1] << 16 >> 16;         // sign-extend
                        int elx = imm;
                        if (elx < 0) elx = 0;
                        if (elx > 10) elx = 10;
                        RegisterFile.updateRegister(destReg, elx);
                        SystemIO.printString("Elixir set to: " + elx + "\n");
                    }
                })
        );

        // 2) addelx $t0,3  -- add elixir (clamped [0,10]), double if $s4 != 0
        instructionList.add(
            new BasicInstruction("addelx $t0,3",
                "Add elixir to register by signed 16-bit immediate (clamped to [0,10], double if $s4!=0)",
                BasicInstructionFormat.I_FORMAT,
                "100001 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();   // [reg, imm]
                        int destReg = operands[0];
                        int imm = operands[1] << 16 >> 16;

                        int elx = RegisterFile.getValue(destReg);
                        int delta = imm;

                        int doubleFlag = RegisterFile.getValue(REG_S4);
                        if (doubleFlag != 0) {
                            delta *= 2;
                            SystemIO.printString("(Double elixir active!) ");
                        }

                        int newVal = elx + delta;
                        if (newVal < 0) newVal = 0;
                        if (newVal > 10) newVal = 10;

                        RegisterFile.updateRegister(destReg, newVal);
                        SystemIO.printString("Elixir changed to: " + newVal + "\n");
                    }
                })
        );

        // 3) sethpL $s0,2000  -- set left tower HP
        instructionList.add(
            new BasicInstruction("sethpL $s0,2000",
                "Set left tower HP: register gets signed 16-bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "100010 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();   // [reg, imm]
                        int destReg = operands[0];
                        int hp = operands[1] << 16 >> 16;
                        if (hp < 0) hp = 0;
                        RegisterFile.updateRegister(destReg, hp);
                        SystemIO.printString("Left tower HP set to: " + hp + "\n");
                    }
                })
        );

        // 4) sethpR $s1,2000  -- set right tower HP
        instructionList.add(
            new BasicInstruction("sethpR $s1,2000",
                "Set right tower HP: register gets signed 16-bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "100011 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int destReg = operands[0];
                        int hp = operands[1] << 16 >> 16;
                        if (hp < 0) hp = 0;
                        RegisterFile.updateRegister(destReg, hp);
                        SystemIO.printString("Right tower HP set to: " + hp + "\n");
                    }
                })
        );

        // 5) sethpK $s2,4000  -- set king tower HP
        instructionList.add(
            new BasicInstruction("sethpK $s2,4000",
                "Set king tower HP: register gets signed 16-bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "100100 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int destReg = operands[0];
                        int hp = operands[1] << 16 >> 16;
                        if (hp < 0) hp = 0;
                        RegisterFile.updateRegister(destReg, hp);
                        SystemIO.printString("King tower HP set to: " + hp + "\n");
                    }
                })
        );

        // 6) damageL $s0,200  -- damage left tower (double in overtime if you use that feature)
        instructionList.add(
            new BasicInstruction("damageL $s0,200",
                "Damage left tower: subtract signed 16-bit immediate from register (min 0, double if $s6!=0)",
                BasicInstructionFormat.I_FORMAT,
                "100101 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();   // [reg, imm]
                        int destReg = operands[0];
                        int hp = RegisterFile.getValue(destReg);
                        int dmg = operands[1] << 16 >> 16;

                        int overtime = RegisterFile.getValue(REG_S6);
                        if (overtime != 0) {
                            dmg *= 2;
                            SystemIO.printString("(Overtime damage!) ");
                        }

                        int newHP = hp - dmg;
                        if (newHP < 0) newHP = 0;
                        RegisterFile.updateRegister(destReg, newHP);
                        SystemIO.printString("Left tower took " + dmg +
                                             " damage! HP now: " + newHP + "\n");
                    }
                })
        );

        // 7) damageR $s1,200  -- damage right tower
        instructionList.add(
            new BasicInstruction("damageR $s1,200",
                "Damage right tower: subtract signed 16-bit immediate from register (min 0, double if $s6!=0)",
                BasicInstructionFormat.I_FORMAT,
                "100110 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();   // [reg, imm]
                        int destReg = operands[0];
                        int hp = RegisterFile.getValue(destReg);
                        int dmg = operands[1] << 16 >> 16;

                        int overtime = RegisterFile.getValue(REG_S6);
                        if (overtime != 0) {
                            dmg *= 2;
                            SystemIO.printString("(Overtime damage!) ");
                        }

                        int newHP = hp - dmg;
                        if (newHP < 0) newHP = 0;
                        RegisterFile.updateRegister(destReg, newHP);
                        SystemIO.printString("Right tower took " + dmg +
                                             " damage! HP now: " + newHP + "\n");
                    }
                })
        );

        // 8) damageK $s2,400  -- damage king tower
        instructionList.add(
            new BasicInstruction("damageK $s2,400",
                "Damage king tower: subtract signed 16-bit immediate from register (min 0, double if $s6!=0)",
                BasicInstructionFormat.I_FORMAT,
                "100111 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();   // [reg, imm]
                        int destReg = operands[0];
                        int hp = RegisterFile.getValue(destReg);
                        int dmg = operands[1] << 16 >> 16;

                        int overtime = RegisterFile.getValue(REG_S6);
                        if (overtime != 0) {
                            dmg *= 2;
                            SystemIO.printString("(Overtime damage!) ");
                        }

                        int newHP = hp - dmg;
                        if (newHP < 0) newHP = 0;
                        RegisterFile.updateRegister(destReg, newHP);
                        SystemIO.printString("King tower took " + dmg +
                                             " damage! HP now: " + newHP + "\n");
                    }
                })
        );

        // 9) showelx -- print elixir in $t0
        instructionList.add(
            new BasicInstruction("showelx",
                "Display current elixir in $t0",
                BasicInstructionFormat.R_FORMAT,
                "101000 00000 00000 00000 00000 000001",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int elx = RegisterFile.getValue(REG_T0);
                        SystemIO.printString("Elixir: " + elx + "\n");
                    }
                })
        );

        // 10) showtowers -- print all tower HP
        instructionList.add(
            new BasicInstruction("showtowers",
                "Display HP of left, right, and king towers ($s0,$s1,$s2)",
                BasicInstructionFormat.R_FORMAT,
                "101000 00000 00000 00000 00000 000010",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int left  = RegisterFile.getValue(REG_S0);
                        int right = RegisterFile.getValue(REG_S1);
                        int king  = RegisterFile.getValue(REG_S2);
                        SystemIO.printString(
                            "Towers HP -> Left: " + left +
                            ", Right: " + right +
                            ", King: " + king + "\n"
                        );
                    }
                })
        );

        // ==========================================================
        // UNIQUE / FUN CLASH INSTRUCTIONS (10)
        // ==========================================================

        // 11) summon $t1,$t0,3  -- try to summon card of cost imm using elixir in $t0
        // I-FORMAT: 2 registers + imm
        // operands: [0]=result, [1]=elixir reg, [2]=imm
        instructionList.add(
            new BasicInstruction("summon $t0,$t1,5",
                "Summon a troop: if elixirReg>=cost, result=1 and elixirReg-=cost, else result=0",
                BasicInstructionFormat.I_FORMAT,
                "110000 fffff sssss tttttttttttttttt",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();
                        int resultReg = operands[0];
                        int elxReg    = operands[1];
                        int cost      = operands[2] << 16 >> 16;

                        int elx = RegisterFile.getValue(elxReg);
                        if (elx >= cost) {
                            RegisterFile.updateRegister(resultReg, 1);
                            RegisterFile.updateRegister(elxReg, elx - cost);
                            SystemIO.printString(
                                "Summon successful! -" + cost +
                                " elixir. Remaining: " + (elx - cost) + "\n"
                            );
                        } else {
                            RegisterFile.updateRegister(resultReg, 0);
                            SystemIO.printString("Not enough elixir to summon that card.\n");
                        }
                    }
                })
        );

        // 12) spell $t0,200  -- cost 4 elixir, damage both lane towers by imm (double in overtime)
        // I-FORMAT: 1 register + imm
        instructionList.add(
            new BasicInstruction("spell $t0,200",
                "Cast a spell (cost 4 elixir): damages both arena towers by imm (double in overtime)",
                BasicInstructionFormat.I_FORMAT,
                "110001 fffff 00000 ssssssssssssssss",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();   // [elxReg, imm]
                        int elxReg = operands[0];
                        int damage = operands[1] << 16 >> 16;
                        int elx = RegisterFile.getValue(elxReg);
                        int cost = 4;

                        if (elx < cost) {
                            SystemIO.printString("Not enough elixir to cast spell (need 4).\n");
                            return;
                        }

                        int overtime = RegisterFile.getValue(REG_S6);
                        if (overtime != 0) {
                            damage *= 2;
                            SystemIO.printString("(Overtime spell!) ");
                        }

                        int left  = RegisterFile.getValue(REG_S0);
                        int right = RegisterFile.getValue(REG_S1);

                        int newLeft  = left  - damage;
                        int newRight = right - damage;
                        if (newLeft  < 0) newLeft  = 0;
                        if (newRight < 0) newRight = 0;

                        RegisterFile.updateRegister(REG_S0, newLeft);
                        RegisterFile.updateRegister(REG_S1, newRight);
                        RegisterFile.updateRegister(elxReg, elx - cost);

                        SystemIO.printString("Spell cast! -" + cost +
                            " elixir, towers damaged by " + damage +
                            ". Left HP: " + newLeft +
                            ", Right HP: " + newRight + "\n");
                    }
                })
        );

        // 13) rage $s0  -- double stat in that register
        instructionList.add(
            new BasicInstruction("rage $s0",
                "Rage buff: double the value in the given register",
                BasicInstructionFormat.R_FORMAT,
                "110010 00000 00000 fffff 00000 000001",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int[] operands = statement.getOperands();   // [reg]
                        int reg = operands[0];
                        int val = RegisterFile.getValue(reg);
                        int buffed = val * 2;
                        RegisterFile.updateRegister(reg, buffed);
                        SystemIO.printString("Rage spell! Value doubled to: " + buffed + "\n");
                    }
                })
        );

        // 14) freeze  -- set freeze flag
        instructionList.add(
            new BasicInstruction("freeze",
                "Freeze both towers: sets $s5 (freeze flag) to 1",
                BasicInstructionFormat.R_FORMAT,
                "110010 00000 00000 00000 00000 000010",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        RegisterFile.updateRegister(REG_S5, 1);
                        SystemIO.printString("Both towers are frozen! (freeze flag set)\n");
                    }
                })
        );

        // 15) cycle  -- rotate $t2-$t5 (4-card hand)
        instructionList.add(
            new BasicInstruction("cycle",
                "Cycle 4-card hand: rotate $t2,$t3,$t4,$t5",
                BasicInstructionFormat.R_FORMAT,
                "110011 00000 00000 00000 00000 000001",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int c0 = RegisterFile.getValue(REG_T2);
                        int c1 = RegisterFile.getValue(REG_T3);
                        int c2 = RegisterFile.getValue(REG_T4);
                        int c3 = RegisterFile.getValue(REG_T5);

                        // (c0,c1,c2,c3) -> (c1,c2,c3,c0)
                        RegisterFile.updateRegister(REG_T2, c1);
                        RegisterFile.updateRegister(REG_T3, c2);
                        RegisterFile.updateRegister(REG_T4, c3);
                        RegisterFile.updateRegister(REG_T5, c0);

                        SystemIO.printString("Cycled cards: [$t2,$t3,$t4,$t5] rotated.\n");
                    }
                })
        );

        // 16) randcard  -- random card ID 0-7 into $v0
        instructionList.add(
            new BasicInstruction("randcard",
                "Draw a random card: store ID 0-7 in $v0",
                BasicInstructionFormat.R_FORMAT,
                "110011 00000 00000 00000 00000 000010",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        Random r = new Random();
                        int id = r.nextInt(8); // 0..7
                        RegisterFile.updateRegister(REG_V0, id);
                        SystemIO.printString("You drew card ID: " + id + "\n");
                    }
                })
        );

        // 17) crownup -- gain crowns for destroyed towers
        instructionList.add(
            new BasicInstruction("crownup",
                "Check towers and increase $s3 (crowns) for each destroyed arena tower",
                BasicInstructionFormat.R_FORMAT,
                "110100 00000 00000 00000 00000 000001",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        int left  = RegisterFile.getValue(REG_S0);
                        int right = RegisterFile.getValue(REG_S1);
                        int crowns = RegisterFile.getValue(REG_S3);

                        if (left <= 0) {
                            crowns++;
                            SystemIO.printString("Left tower destroyed! +1 crown.\n");
                        }
                        if (right <= 0) {
                            crowns++;
                            SystemIO.printString("Right tower destroyed! +1 crown.\n");
                        }

                        RegisterFile.updateRegister(REG_S3, crowns);
                        SystemIO.printString("Total crowns: " + crowns + "\n");
                    }
                })
        );

        // 18) doubleelx -- enable double elixir
        instructionList.add(
            new BasicInstruction("doubleelx",
                "Enable double elixir: sets $s4 flag to 1 (affects addelx)",
                BasicInstructionFormat.R_FORMAT,
                "110100 00000 00000 00000 00000 000010",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        RegisterFile.updateRegister(REG_S4, 1);
                        SystemIO.printString("Double Elixir time! ($s4 flag set)\n");
                    }
                })
        );

        // 19) overtime -- enable overtime
        instructionList.add(
            new BasicInstruction("overtime",
                "Enable overtime: sets $s6 flag to 1 (damage and spell use double damage)",
                BasicInstructionFormat.R_FORMAT,
                "110101 00000 00000 00000 00000 000001",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        RegisterFile.updateRegister(REG_S6, 1);
                        SystemIO.printString("Overtime! Towers take extra damage. ($s6 flag set)\n");
                    }
                })
        );

        // 20) emote 1  -- print emote based on immediate code
        // I-FORMAT: only an immediate (no register operand)
        // 20) emote $t0,1  -- print emote based on immediate code
// I-FORMAT: register + immediate (we ignore the register in simulation)
// emote -- print a default emote (no operands, like yell/hm)
        instructionList.add(
            new BasicInstruction("emote",
                "Print a Clash Royale emote message",
                BasicInstructionFormat.R_FORMAT,
                "110101 00000 00000 00000 00000 000010",
                new SimulationCode() {
                    public void simulate(ProgramStatement statement) throws ProcessingException {
                        // No operands: just print something fun
                        SystemIO.printString("[Emote] 😆  (Laugh)\n");
                    }
                })
        );
    }
}
