package com.apelisser.algashop.ordering.infrastructure.config.modelmapper;

import com.apelisser.algashop.ordering.core.ports.in.customer.CustomerOutput;
import com.apelisser.algashop.ordering.core.ports.out.order.OrderDetailOutput;
import com.apelisser.algashop.ordering.core.ports.out.order.OrderItemDetailOutput;
import com.apelisser.algashop.ordering.core.application.utility.Mapper;
import com.apelisser.algashop.ordering.core.domain.model.commons.FullName;
import com.apelisser.algashop.ordering.core.domain.model.customer.BirthDate;
import com.apelisser.algashop.ordering.core.domain.model.customer.Customer;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderItemPersistenceEntity;
import com.apelisser.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntity;
import io.hypersistence.tsid.TSID;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class ModelMapperConfig {

    private static final Converter<FullName, String> fullNameToFirstNameConverter;
    private static final Converter<FullName, String> fullNameToLastNameConverter;
    private static final Converter<BirthDate, LocalDate> birthDateToLocalDateConverter;
    private static final Converter<Long, String> longToStringTSIDConverter;

    @Bean
    public Mapper mapper() {
        ModelMapper modelMapper = new ModelMapper();
        this.configuration(modelMapper);
        return modelMapper::map;
    }

    private void configuration(ModelMapper modelMapper) {
        modelMapper.getConfiguration()
            .setSourceNamingConvention(NamingConventions.NONE)
            .setDestinationNamingConvention(NamingConventions.NONE)
            .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Customer.class, CustomerOutput.class)
            .addMappings(mapping -> mapping.using(fullNameToFirstNameConverter).map(Customer::fullName, CustomerOutput::setFirstName))
            .addMappings(mapping -> mapping.using(fullNameToLastNameConverter).map(Customer::fullName, CustomerOutput::setLastName))
            .addMappings(mapping -> mapping.using(birthDateToLocalDateConverter).map(Customer::birthDate, CustomerOutput::setBirthDate));

        modelMapper.createTypeMap(OrderPersistenceEntity.class, OrderDetailOutput.class)
            .addMappings(mapping -> mapping.using(longToStringTSIDConverter).map(OrderPersistenceEntity::getId, OrderDetailOutput::setId));

        modelMapper.createTypeMap(OrderItemPersistenceEntity.class, OrderItemDetailOutput.class)
            .addMappings(mapping -> mapping.using(longToStringTSIDConverter).map(OrderItemPersistenceEntity::getId, OrderItemDetailOutput::setId))
            .addMappings(mapping -> mapping.using(longToStringTSIDConverter).map(OrderItemPersistenceEntity::getOrderId, OrderItemDetailOutput::setOrderId));
    }

    static {
        fullNameToFirstNameConverter = mappingContext -> {
            FullName fullName = mappingContext.getSource();
            return fullName != null ? fullName.firstName() : null;
        };

        fullNameToLastNameConverter = mappingContext -> {
            FullName fullName = mappingContext.getSource();
            return fullName != null ? fullName.lastName() : null;
        };

        birthDateToLocalDateConverter = mappingContext -> {
            BirthDate birthDate = mappingContext.getSource();
            return birthDate != null ? birthDate.value() : null;
        };

        longToStringTSIDConverter = mappingContext -> {
            Long tsidAsLong = mappingContext.getSource();
            return tsidAsLong != null ? new TSID(tsidAsLong).toString() : null;
        };
    }

}
