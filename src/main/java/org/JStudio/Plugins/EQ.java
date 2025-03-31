package org.JStudio.Plugins;

import java.util.ArrayList;

public class EQ { //might require some optimization in the arraylist
    private ArrayList<Filter> filters = new ArrayList<Filter>();

    EQ(int sampleRate) {
        for (int i = 0; i < 3; i++) {
            this.filters.add(new Filter((((double) 20000 / 3) * (i + 1)), 1.0, 1.0, sampleRate, Filter.Type.PEAKING));
        }
    }

    EQ(int sampleRate, byte numBands) {
        for (int i = 0; i < numBands; i++) {
            this.filters.add(new Filter((double) (20000 / numBands) * (i + 1), 1.0, 1.0, sampleRate, Filter.Type.PEAKING));
        }
    }

    public double process(double sample) {
        for (Filter filter : filters) {
            sample = filter.process(sample);
        }
        return sample;
    }

    public double[] process(double[] samples) {
        for (int i = 0; i < samples.length; i++) {
            samples[i] = process(samples[i]);
        }
        return samples;
    }
}

class Filter {
    private double a0, a1, a2, b1, b2; //ax values are input coefficients, bx is output coefficients
    private double z1 = 0, z2 = 0; //storing memory of prev frame/data

    enum Type {
        PEAKING, LOWSHELF, HIGHSHELF
    }

    Filter(double freq, double q, double gain, int sampleRate, Type type) {
        double A = Math.pow(10, gain / 40);
        double omega = 2 * Math.PI * freq / sampleRate;
        double alpha = Math.sin(omega) / (2 * q);

        switch (type) {
            case PEAKING -> {
                a0 = 1 + alpha / A;
                a1 = -2 * Math.cos(omega);
                a2 = 1 - alpha / A;
                b1 = -2 * Math.cos(omega);
                b2 = (1 - alpha * A);
            }
            case LOWSHELF, HIGHSHELF -> {
                double beta = Math.sqrt(A) / q;
                a0 = A * ((A + 1) - (A - 1) * Math.cos(omega) + beta * Math.sin(omega));
                a1 = 2 * A * ((A - 1) - (A + 1) * Math.cos(omega));
                a2 = A * ((A + 1) - (A - 1) * Math.cos(omega) - beta * Math.sin(omega));
                b1 = -2 * (A - 1 + A * Math.cos(omega));
                b2 = (A + 1) + (A - 1) * Math.cos(omega) - beta * Math.sin(omega);
            }
            default -> {return;}
        }
        a0 = 1 / a0;
        a1 *= a0;
        a2 *= a0;
        b1 *= a0;
        b2 *= a0;
    }

    public double process(double sample) {
        double result = sample * a0 + z1;
        z1 = sample * a1 + z2 - b1 * result;
        z2 = sample * a2 - b2 * result;
        return result;
    }
}