package com.apelisser.algashop.ordering.domain.utility;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;

import java.util.UUID;

public final class IdGenerator {

    private static final TimeBasedEpochRandomGenerator timeBasedGenerator = Generators.timeBasedEpochRandomGenerator();

    private IdGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static UUID generateTimeBasedUUID() {
        return timeBasedGenerator.generate();
    }

}
