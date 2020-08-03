package com.github.ontio.smartcontract.neovm.anonymous;

public class Accumulator {
    public byte[] accumulator;
    public byte[] witness;

    public Accumulator(byte[] accumulator, byte[] witness) {
        this.accumulator = accumulator;
        this.witness = witness;
    }

    public byte[] getAccumulator() {
        return accumulator;
    }

    public void setAccumulator(byte[] accumulator) {
        this.accumulator = accumulator;
    }

    public byte[] getWitness() {
        return witness;
    }

    public void setWitness(byte[] witness) {
        this.witness = witness;
    }
}
