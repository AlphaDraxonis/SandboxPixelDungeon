package com.shatteredpixel.shatteredpixeldungeon.editor.inv.other;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;

public interface PermaGas {

    //WARNING! Do not add PSacrificialFire or PEternalFire -> change getOnly() in these cases!

    class PFire extends Fire implements PermaGas {
        @Override
        protected void evolve() {
            for (int i = 0; i < cur.length; i++) {
                if (cur[i] > 0) {
                    cur[i] += 1;
                    off[i] = cur[i];
                    volume += cur[i];
                }
            }
            super.evolve();
        }
    }

    class PFreezing extends Freezing implements PermaGas {
        @Override
        protected void evolve() {
            for (int i = 0; i < cur.length; i++) {
                if (cur[i] > 0) {
                    cur[i] += 1;
                    off[i] = cur[i];
                    volume += cur[i];
                }
            }
            super.evolve();
        }
    }

    class PToxicGas extends ToxicGas implements PermaGas {
        @Override
        protected void evolve() {
            for (int i = 0; i < cur.length; i++) {
                off[i] = cur[i];
                volume += cur[i];
            }
            super.evolve();
        }
    }

    class PCorrosiveGas extends CorrosiveGas implements PermaGas {
        @Override
        protected void evolve() {
            for (int i = 0; i < cur.length; i++) {
                off[i] = cur[i];
                volume += cur[i];
            }
            super.evolve();
        }
    }

    class PConfusionGas extends ConfusionGas implements PermaGas {
        @Override
        protected void evolve() {
            for (int i = 0; i < cur.length; i++) {
                off[i] = cur[i];
                volume += cur[i];
            }
            super.evolve();
        }
    }

    class PParalyticGas extends ParalyticGas implements PermaGas {
        @Override
        protected void evolve() {
            for (int i = 0; i < cur.length; i++) {
                off[i] = cur[i];
                volume += cur[i];
            }
            super.evolve();
        }
    }

    class PStenchGas extends StenchGas implements PermaGas {
        @Override
        protected void evolve() {
            for (int i = 0; i < cur.length; i++) {
                off[i] = cur[i];
                volume += cur[i];
            }
            super.evolve();
        }
    }

    class PSmokeScreen extends SmokeScreen implements PermaGas {
        @Override
        protected void evolve() {
            for (int i = 0; i < cur.length; i++) {
                off[i] = cur[i];
                volume += cur[i];
            }
            super.evolve();
        }
    }

    class PElectricity extends Electricity implements PermaGas {
        @Override
        protected void evolve() {
            for (int i = 0; i < cur.length; i++) {
                if (cur[i] > 0) {
                    cur[i] += 1;
                    off[i] = cur[i];
                    volume += cur[i];
                }
            }
            super.evolve();
        }
    }

    class PStormCloud extends StormCloud implements PermaGas {
        @Override
        protected void evolve() {
            for (int i = 0; i < cur.length; i++) {
                off[i] = cur[i];
                volume += cur[i];
            }
            super.evolve();
        }
    }

}