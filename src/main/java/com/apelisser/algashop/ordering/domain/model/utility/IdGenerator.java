package com.apelisser.algashop.ordering.domain.model.utility;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;
import io.hypersistence.tsid.TSID;

import java.util.UUID;

public final class IdGenerator {

    private static final TimeBasedEpochRandomGenerator timeBasedGenerator = Generators.timeBasedEpochRandomGenerator();

    private static final TSID.Factory tsidFactory = TSID.Factory.INSTANCE;

    private IdGenerator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static UUID generateTimeBasedUUID() {
        return timeBasedGenerator.generate();
    }

    /*
     * TSID_NODE
     * TSID_NODE_COUNT
     */
    public static TSID generateTSID() {
        return tsidFactory.generate();
    }

}
