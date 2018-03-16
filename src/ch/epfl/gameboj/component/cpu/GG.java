package ch.epfl.gameboj.component.cpu;

import ch.epfl.gameboj.component.cpu.Cpu.FlagSrc;

public class GG {

    public static void main(String[] args) {
        Cpu.combineAluFlags(128, FlagSrc.ALU, FlagSrc.V0 , FlagSrc.V0  , FlagSrc.V0);
        Cpu.combineAluFlags(128, FlagSrc.CPU, FlagSrc.V0 , FlagSrc.V0  , FlagSrc.V0);
        Cpu.combineAluFlags(128, FlagSrc.CPU, FlagSrc.V1 , FlagSrc.V0  , FlagSrc.V0);
    }

}
